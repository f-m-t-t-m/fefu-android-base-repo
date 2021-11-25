package ru.fefu.activitytracker.Screens.Tracker

import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import org.osmdroid.util.GeoPoint
import ru.fefu.activitytracker.App
import ru.fefu.activitytracker.R

class TrackerService : Service() {
    override fun onBind(p0: Intent?): IBinder? = null

    private var id: Int = -1
    private val fusedLocationClient by lazy { LocationServices.getFusedLocationProviderClient(this) }

    companion object {
        var coordinatesList = mutableListOf<GeoPoint>()
        var distance:Double= 0.0
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == "stop_service") {
            App.INSTANCE.db.activityDao().finishActivity(System.currentTimeMillis(), distance, id)
            coordinatesList.clear()
            distance = 0.0
            val activityIntent = Intent(this, Activity::class.java)
            activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(activityIntent)
            stopForeground(true)
            stopSelf()
            return START_NOT_STICKY
        }
        else if (intent?.action == "start_service") {
            id = intent?.getIntExtra("activity_id", -1)!!
            startLocationUpdates(id)
            super.onStartCommand(intent, flags, startId)
            return START_REDELIVER_INTENT
        }
        return START_NOT_STICKY
    }


    @SuppressLint("UnspecifiedImmutableFlag")
    private fun showNotification() {
        createChannel()
        val activityIntent = Intent(this, Activity::class.java)
        activityIntent.putExtra("notification", 1)
        activityIntent.putExtra("activity_id", id)
        val pendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
            addNextIntent(activityIntent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }
        val cancelIntent = Intent(this, TrackerService::class.java)
        cancelIntent.action = "stop_service"
        val cancelPendingIntent = PendingIntent.getService(
            this,
            137,
            cancelIntent,
            0
        )
        val notification = NotificationCompat.Builder(this, "tracker_service_id")
            .setContentTitle("Hello")
            .setContentText("Tracking your activity")
            .setSmallIcon(R.drawable.ic_fab)
            .setContentIntent(pendingIntent)
            .addAction(R.drawable.ic_pause_button, "stop", cancelPendingIntent)
            .build()
        startForeground(1, notification)
    }

    private fun createChannel() {
        val channel = NotificationChannel("tracker_service_id", "default channel", NotificationManager.IMPORTANCE_LOW)
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    private fun startLocationUpdates(id: Int) {
        if (id == -1) stopSelf()
        val locationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(10000L)
            .setSmallestDisplacement(10f)
        val callback = MyActivityLocationCallback(id)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            callback,
            Looper.getMainLooper()
        )
        showNotification()
    }

    inner class MyActivityLocationCallback(private val id: Int): LocationCallback() {
        override fun onLocationResult(result: LocationResult?) {
            val lastLocation = result?.lastLocation ?: return
            coordinatesList.add(GeoPoint(lastLocation.latitude, lastLocation.longitude))
            if (coordinatesList.size > 1) {
                val locationA = Location("A")
                locationA.latitude = coordinatesList[coordinatesList.size-2].latitude
                locationA.longitude = coordinatesList[coordinatesList.size-2].longitude
                val locationB = Location("B")
                locationB.latitude = coordinatesList[coordinatesList.size-1].latitude
                locationB.longitude = coordinatesList[coordinatesList.size-1].longitude
                distance += locationA.distanceTo(locationB)
            }
            App.INSTANCE.db.activityDao().updateCoordinates(lastLocation.latitude, lastLocation.longitude, id)
        }
    }
}