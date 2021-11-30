package ru.fefu.activitytracker.Screens.Tracker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import ru.fefu.activitytracker.Adapters.NewActivityListAdapter
import ru.fefu.activitytracker.App
import ru.fefu.activitytracker.Enums.ActivitiesEnum
import ru.fefu.activitytracker.R
import ru.fefu.activitytracker.Room.ActivityRoom
import ru.fefu.activitytracker.databinding.StartedActivityFragmentBinding
import org.osmdroid.views.overlay.Polyline
import kotlin.math.roundToInt

class StartedActivityFragment(private val id_: Int): Fragment() {
    private var _binding: StartedActivityFragmentBinding? = null
    private val binding get() = _binding!!
    private var time = 0.0

    private val polyline by lazy {
        Polyline().apply {
            outlinePaint.color = ContextCompat.getColor(
                requireContext(),
                R.color.blue
            )
        }
    }

    companion object {
        fun newInstance(id: Int): StartedActivityFragment {
            return StartedActivityFragment(id)
        }
        const val tag = "started_activity"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.registerReceiver(updateTime, IntentFilter("timerUpdated"))
        activity?.registerReceiver(updateDistance, IntentFilter("distanceUpdated"))
        _binding = StartedActivityFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    private val updateTime: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            time = intent?.getDoubleExtra("timeExtra", 0.0)!!
            Log.d("timeFragment", time.toString())
            binding.time.text = DoubleToTime(time)
        }
    }

    private val updateDistance: BroadcastReceiver = object :BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            var newDistance = intent?.getDoubleExtra("distance", 0.0)!!
            val longitude = intent.getDoubleExtra("longitude", 0.0)
            val latitude = intent.getDoubleExtra("latitude", 0.0)
            if (latitude != 0.0 && longitude != 0.0) {
                polyline.addPoint(GeoPoint(latitude, longitude))
            }
            if (newDistance > 1000) {
                newDistance /= 1000
                binding.distance.text = "%.1f".format(newDistance) + " км"
            }
            else {
                binding.distance.text = "%.0f".format(newDistance) + " м"
            }
        }
    }

    private fun DoubleToTime(time: Double): String {
        val result = time.roundToInt()
        val hours = result % 86400 / 3600
        val minutes = result % 86400 % 3600 / 60
        val seconds = result % 86400 % 3600 % 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Configuration.getInstance().load(requireActivity(), activity?.getPreferences(Context.MODE_PRIVATE))
        showUserLocation()
        initMap()

        val activityUnfinished = App.INSTANCE.db.activityDao().getById(id_)
        var type = ActivitiesEnum.values()[activityUnfinished.type].type
        binding.activityName.setText(type)
        binding.backButton.setOnClickListener {
            activity?.onBackPressed()
        }
        binding.finishBtn.setOnClickListener {
            val cancelIntent = Intent(this.requireActivity(), TrackerService::class.java)
            cancelIntent.action = "stop_service"
            this.requireActivity().startService(cancelIntent)
        }
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
        val geoPoints = TrackerService.coordinatesList
        for(point in geoPoints) {
            polyline.addPoint(point)
        }
        binding.mapView.overlayManager.add(polyline)
    }

    private fun showUserLocation() {
        val locationOverlay = MyLocationNewOverlay(binding.mapView)
        val locationIcon = BitmapFactory.decodeResource(resources, R.drawable.marker2)
        locationOverlay.setDirectionArrow(locationIcon, locationIcon)
        locationOverlay.setPersonHotspot(locationIcon.width / 2f, locationIcon.height.toFloat())
        locationOverlay.enableMyLocation()
        binding.mapView.overlays.add(locationOverlay)
    }

    override fun onDestroyView() {
        activity?.unregisterReceiver(updateTime)
        activity?.unregisterReceiver(updateDistance)
        _binding = null
        super.onDestroyView()
    }
}