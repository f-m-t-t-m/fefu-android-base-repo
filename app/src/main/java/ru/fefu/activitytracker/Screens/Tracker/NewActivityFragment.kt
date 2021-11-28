package ru.fefu.activitytracker.Screens.Tracker

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import ru.fefu.activitytracker.Adapters.NewActivityListAdapter
import ru.fefu.activitytracker.App
import ru.fefu.activitytracker.Enums.ActivitiesEnum
import ru.fefu.activitytracker.Models.NewActivityData
import ru.fefu.activitytracker.R
import ru.fefu.activitytracker.Room.ActivityRoom
import ru.fefu.activitytracker.databinding.NewActivityFragmentBinding
import android.graphics.BitmapFactory
import android.location.Location
import android.location.LocationManager
import android.util.Log
import org.osmdroid.config.Configuration
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay


class NewActivityFragment: Fragment() {
    private var _binding: NewActivityFragmentBinding? = null
    private val binding get() = _binding!!
    private var activities = mutableListOf<NewActivityData>()
    private lateinit var adapter: NewActivityListAdapter
    private lateinit var serviceIntent: Intent

    private val polyline by lazy {
        Polyline().apply {
            outlinePaint.color = ContextCompat.getColor(
                requireActivity(),
                R.color.purple_700
            )
        }
    }

    companion object {
        fun newInstance(): NewActivityFragment {
            return NewActivityFragment()
        }
        const val tag = "new_activity"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fillActivities()
        _binding = NewActivityFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Configuration.getInstance().load(requireActivity(), activity?.getPreferences(Context.MODE_PRIVATE))
        showUserLocation()
        initMap()
        val recycleView = binding.newActivityList
        recycleView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recycleView.adapter = adapter

        binding.backButton.setOnClickListener {
            activity?.onBackPressed()
        }

        binding.buttonContinue.setOnClickListener {
            if (adapter.selected != -1) {
                val endDate = System.currentTimeMillis()
                val startDate = System.currentTimeMillis()

                val id_ = App.INSTANCE.db.activityDao().insert (
                    ActivityRoom (
                        0,
                        adapter.selected,
                        startDate,
                        endDate,
                        0.0,
                        0.0,
                        0.0,
                        0
                    )
                )
                val serviceIntent = Intent(this.requireActivity(), TrackerService::class.java)
                serviceIntent.putExtra("activity_id", id_.toInt())
                serviceIntent.action = "start_service"
                serviceIntent.putExtra("timeExtra", 0.0)
                this.requireActivity().startService(serviceIntent)

                val manager =
                    activity?.supportFragmentManager?.findFragmentByTag(ActivityTabs.tag)?.childFragmentManager
                manager?.beginTransaction()?.apply {
                    manager.fragments.forEach(::hide)
                    add(
                        R.id.activity_fragment_container,
                        StartedActivityFragment.newInstance(id_.toInt()),
                        StartedActivityFragment.tag,
                    )
                    addToBackStack(null)
                    commit()
                }
            }
        }
    }


    fun fillActivities() {
        for(i in ActivitiesEnum.values()) {
            activities.add(NewActivityData(i.type, false))
        }
        adapter = NewActivityListAdapter(activities)
    }

    private fun initMap() {
        binding.mapView.minZoomLevel = 4.0
        binding.mapView.post {
            binding.mapView.zoomToBoundingBox(
                BoundingBox(
                    43.232111,
                    132.117062,
                    42.968866,
                    131.768039
                ),
                false
            )
        }
    }

    private fun showUserLocation() {
        val locationOverlay = MyLocationNewOverlay(
            object : GpsMyLocationProvider(requireActivity()) {
                private var mapMoved = false
                override fun onLocationChanged(location: Location) {
                    location.removeBearing()
                    super.onLocationChanged(location)
                    if (mapMoved) return
                    mapMoved = true
                }
            },
            binding.mapView
        )
        val locationIcon = BitmapFactory.decodeResource(resources, R.drawable.marker)
        locationOverlay.setDirectionArrow(locationIcon, locationIcon)
        locationOverlay.setPersonHotspot(locationIcon.width / 2f, locationIcon.height.toFloat())
        locationOverlay.enableMyLocation()
        binding.mapView.overlays.add(locationOverlay)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}