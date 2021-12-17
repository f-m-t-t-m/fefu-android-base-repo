package ru.fefu.activitytracker.Screens.Tracker

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.material.bottomnavigation.BottomNavigationView
import ru.fefu.activitytracker.Adapters.ActivityListAdapter
import ru.fefu.activitytracker.App
import ru.fefu.activitytracker.Enums.ActivitiesEnum
import ru.fefu.activitytracker.Models.ActivityData
import ru.fefu.activitytracker.Models.DateData
import ru.fefu.activitytracker.R
import ru.fefu.activitytracker.databinding.ActivityFragmentTrackingMyBinding
import java.lang.Exception
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class ActivityMyTrackerFragment : Fragment(R.layout.activity_fragment_tracking_my) {
    private var _binding: ActivityFragmentTrackingMyBinding? = null
    private val binding get() = _binding!!
    private val activities = mutableListOf<ActivityData>()
    private val data_activities = mutableListOf<Any>()
    private val adapter = ActivityListAdapter(data_activities)

    val map = mapOf(1 to "Январь", 2 to "Февраль", 3 to "Март",
        4 to "Апрель", 5 to "Май", 6 to "Июнь",
        7 to "Июль", 8 to "Август", 9 to "Сентябрь",
        10 to "Октябрь", 11 to "Ноябрь", 12 to "Декабрь")

    private val locationPermissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions[Manifest.permission.ACCESS_FINE_LOCATION]?.let {
                if (it) {
                    if (isGoogleServiceAvailable()) {
                        checkGpsEnabled(
                            { val unfinished = App.INSTANCE.db.activityDao().getUnfinished()
                                if (unfinished !== null) {
                                    switchToUnfinishedActivity(unfinished.id)
                                }
                                else {
                                    switchToNewActivity()
                                }
                            },
                            { if (it is ResolvableApiException) {
                                it.startResolutionForResult(this.requireActivity(), 2)
                            }
                        })
                    }
                } else {
                    if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                        showPermissionBlocked()
                    }
                }
            }
        }

    private fun requestLocationPermissionAndLocate() {
        when {
            ContextCompat.checkSelfPermission(
                this.requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION,
            ) == PackageManager.PERMISSION_GRANTED -> {
                if (isGoogleServiceAvailable()) {
                    checkGpsEnabled(
                        {   val unfinished = App.INSTANCE.db.activityDao().getUnfinished()
                            if (unfinished !== null) {
                                switchToUnfinishedActivity(unfinished.id)
                            }
                            else {
                                switchToNewActivity()
                            }
                        },
                        { if (it is ResolvableApiException) {
                            it.startResolutionForResult(this.requireActivity(), 2)
                        }
                    })
                }
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                showRationale()
            }
            else -> locationPermissionRequest.launch(
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                )
            )
        }
    }

    private fun showRationale() {
        AlertDialog.Builder(this.requireContext())
            .setTitle("Permission required")
            .setMessage("Нужно местоположение для работы карты")
            .setPositiveButton("Proceed") { _, _ -> locationPermissionRequest.launch(
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                )
            )}
            .setNegativeButton("Cancel") { _, _ -> }
            .show()
    }

    private fun showPermissionBlocked() {
        AlertDialog.Builder(this.requireContext())
            .setTitle("Permission denied")
            .setMessage("Измените найстройки геолокации приложения")
            .setPositiveButton("Settings") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", "ru.fefu.activitytracker", null)
                intent.data = uri
                startActivity(intent)
            }
            .setNegativeButton("Cancel") { _, _ -> }
            .show()
    }

    private fun isGoogleServiceAvailable(): Boolean {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val result = googleApiAvailability.isGooglePlayServicesAvailable(this.requireActivity())
        if (result == ConnectionResult.SUCCESS) {
            return true
        }
        if (googleApiAvailability.isUserResolvableError(result)) {
            googleApiAvailability.getErrorDialog(
                this.requireActivity(),
                result,
                1
            ).show()
        }
        else {
            Toast.makeText(this.requireActivity(), "Сервисы гугл недоступны", Toast.LENGTH_SHORT).show()
        }
        return false
    }

    private fun checkGpsEnabled(success: () -> Unit, error: (Exception) -> Unit) {
        LocationServices.getSettingsClient(this.requireActivity())
            .checkLocationSettings(
                LocationSettingsRequest.Builder()
                    .addLocationRequest(LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY))
                    .build()
            )
            .addOnSuccessListener {
                success.invoke()
            }
            .addOnFailureListener {
                error.invoke(it)
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            switchToNewActivity()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ActivityFragmentTrackingMyBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun fill_date(activities: List<ActivityData>) {
        val cur = LocalDateTime.now()
        var date = DateData("")
        for (activity in activities) {
            if (cur.year == activity.endDate.year &&
                cur.monthValue == activity.endDate.monthValue &&
                    cur.dayOfMonth == activity.endDate.dayOfMonth) {
                if (date.Date != "Сегодня") {
                    date = DateData("Сегодня")
                    data_activities.add(date)
                }
            }
            else {
                if (date.Date != map.get(activity.endDate.monthValue) + ' ' + activity.endDate.year.toString()  + " года") {
                    date = DateData(map.get(activity.endDate.monthValue) + ' '+activity.endDate.year.toString() + " года")
                    data_activities.add(date)
                }
            }
            data_activities.add(activity)
        }
    }

    private fun changeFragment(position: Int) {
        if (position in data_activities.indices) {
            val manager = activity?.supportFragmentManager?.findFragmentByTag(ActivityTabs.tag)?.childFragmentManager
            manager?.beginTransaction()?.apply {
                manager.fragments.forEach(::hide)
                add (
                    R.id.activity_fragment_container,
                    MyActivityInfo.newInstance(data_activities[position] as ActivityData),
                    MyActivityInfo.tag,
                        )
                addToBackStack(null)
                commit()
           }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        App.INSTANCE.db.activityDao().getAll().observe(viewLifecycleOwner) {
            activities.clear()
            data_activities.clear()
            for(activity in it) {
                var startDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(activity.dateStart), ZoneId.systemDefault())
                var endDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(activity.dateEnd), ZoneId.systemDefault())
                var type = ActivitiesEnum.values()[activity.type].type
                var distance = ""
                if (activity.distance > 1000) {
                    val dist = activity.distance/1000
                    distance = "%.1f".format(dist) + " км"
                }
                else {
                    distance = "%.0f".format(activity.distance) + " м"
                }
                activities.add(ActivityData(distance, type, startDate, endDate))
            }
            fill_date(activities)
            adapter.notifyDataSetChanged()
        }

        val recycleView = binding.recyclerView
        recycleView.layoutManager = LinearLayoutManager(requireContext())
        recycleView.adapter = adapter
        adapter.setItemClickListener { changeFragment(it) }
        binding.startNewActivity.setOnClickListener{
            requestLocationPermissionAndLocate()
        }
    }

    private fun switchToNewActivity() {
        val manager = activity?.supportFragmentManager?.findFragmentByTag(ActivityTabs.tag)?.childFragmentManager
        val navbar = activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        navbar?.visibility = View.GONE
        manager?.beginTransaction()?.apply {
            manager?.fragments.forEach(::hide)
            add(R.id.activity_fragment_container, NewActivityFragment.newInstance(), NewActivityFragment.tag)
            addToBackStack("new_active")
            commit()
        }
    }

    private fun switchToUnfinishedActivity(id: Int) {
        val manager = activity?.supportFragmentManager?.findFragmentByTag(ActivityTabs.tag)?.childFragmentManager
        val navbar = activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        navbar?.visibility = View.GONE
        manager?.beginTransaction()?.apply {
            manager?.fragments.forEach(::hide)
            add(R.id.activity_fragment_container, StartedActivityFragment.newInstance(id), StartedActivityFragment.tag)
            addToBackStack("new_active")
            commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}