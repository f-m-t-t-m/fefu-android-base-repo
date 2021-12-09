package ru.fefu.activitytracker.Screens.Tracker

import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.CountDownTimer
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
import java.util.*

class TrackerService : Service() {
    override fun onBind(p0: Intent?): IBinder? = null

    private var id: Int = -1
    private val fusedLocationClient by lazy { LocationServices.getFusedLocationProviderClient(this) }
    private val timer = Timer()
    private var distance = 0.0
    lateinit var callback: LocationCallback

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == "stop_service") {
            id = intent.getIntExtra("activity_id", -2)
            Log.d("id_", id.toString())
            App.INSTANCE.db.activityDao().finishActivity(System.currentTimeMillis(), id)
            val activityIntent = Intent(this, Activity::class.java)
            activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(activityIntent)
            if (this::callback.isInitialized) {
                fusedLocationClient.removeLocationUpdates(callback)
            }
            stopForeground(true)
            stopSelf()
        }
        else if (intent?.action == "start_service") {
            val time = intent.getDoubleExtra("timeExtra", 0.0)
            timer.scheduleAtFixedRate(TimeTask(time), 0, 1000)
            id = intent.getIntExtra("activity_id", -1)
            startLocationUpdates(id)
            super.onStartCommand(intent, flags, startId)
            return START_REDELIVER_INTENT
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        timer.cancel()
        super.onDestroy()
    }

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
        cancelIntent.putExtra("activity_id", id)
        cancelIntent.action = "stop_service"
        val cancelPendingIntent = PendingIntent.getService(
            this,
            137,
            cancelIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
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
        callback = MyActivityLocationCallback(id)
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
            val coordinatesList = App.INSTANCE.db.activityDao().getById(id).coordinates.toMutableList()
            coordinatesList.add(lastLocation.latitude to lastLocation.longitude)
            if (coordinatesList.size > 1) {
                val locationA = Location("A")
                locationA.latitude = coordinatesList[coordinatesList.size-2].first
                locationA.longitude = coordinatesList[coordinatesList.size-2].second
                val locationB = Location("B")
                locationB.latitude = coordinatesList[coordinatesList.size-1].first
                locationB.longitude = coordinatesList[coordinatesList.size-1].second
                distance += locationA.distanceTo(locationB)
            }
            Log.d("distance", distance.toString())
            App.INSTANCE.db.activityDao().updateCoordinates(coordinatesList, distance, id)
        }
    }

    private inner class TimeTask(private var time: Double): TimerTask() {
        override fun run() {
            val intent = Intent("timerUpdated")
            time++
            Log.d("timeService", time.toString())
            intent.putExtra("timeExtra", time)
            sendBroadcast(intent)
        }
    }
}