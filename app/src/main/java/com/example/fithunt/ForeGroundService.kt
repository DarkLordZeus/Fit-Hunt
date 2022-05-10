package com.example.fithunt

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.fithunt.Util.Stopwatch
import com.example.fithunt.Util.TrackingPermission
import com.example.fithunt.Util.utilvalues
import com.example.fithunt.Util.utilvalues.Companion.ACTION_PAUSE_SERVICE
import com.example.fithunt.Util.utilvalues.Companion.ACTION_SHOW_TRACKING_FRAGMENT
import com.example.fithunt.Util.utilvalues.Companion.ACTION_START_OR_RESUME_SERVICE
import com.example.fithunt.Util.utilvalues.Companion.ACTION_STOP_SERVICE
import com.example.fithunt.Util.utilvalues.Companion.NOTIFICATION_ID
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

typealias Polyline = MutableList<LatLng>
 typealias Polylines = MutableList<Polyline>

@AndroidEntryPoint
class ForeGroundService : LifecycleService() {


    var isfirstrun = true
    var isservicekilled = false

    private val timeruninseconds=MutableLiveData<Long>()

    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    @Inject
    lateinit var baseNotificationBuilder:NotificationCompat.Builder

    lateinit var currNotificationBuilder:NotificationCompat.Builder

    companion object {
        val timeruninmillis = MutableLiveData<Long>()
        val istracking = MutableLiveData<Boolean>()
        val pathpoints = MutableLiveData<Polylines>()
    }

    private fun postinitialvalues()
    {
        istracking.postValue(false)
        pathpoints.postValue(mutableListOf())
        timeruninseconds.postValue(0L)
        timeruninmillis.postValue(0L)
    }

    private fun addEmptyPolyline()= pathpoints.value?.apply {
        add(mutableListOf())
        pathpoints.postValue(this)
    } ?: pathpoints.postValue(mutableListOf(mutableListOf()))

    private fun addnewPolyline(location: Location?){
        location?.let {
            val position =LatLng(location.latitude , location.longitude)
            pathpoints.value?.apply {
                last().add(position)
                pathpoints.postValue(this)
            }
        }
    }
    override fun onCreate() {
        super.onCreate()
        currNotificationBuilder = baseNotificationBuilder
        postinitialvalues()
        fusedLocationProviderClient = FusedLocationProviderClient(this)

        istracking.observe(this, Observer {
            updateLocationTracking(it)
            updateNotificationTrackingState(it)
        })
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when(it.action)
            {
                ACTION_START_OR_RESUME_SERVICE -> {
                    if(isfirstrun)
                    {
                        startForegroundservice()
                        isfirstrun=false
                    }
                    else{
                        startTimer()
                        Timber.d("Resuming Serviceee")
                    }

                }
                ACTION_PAUSE_SERVICE -> {
                    Timber.d("PAaused service")
                    isTimerEnabled = false
                    istracking.postValue(false)
                }
                ACTION_STOP_SERVICE -> {
                    Timber.d("Stopped service")
                    killservice()
                }

            }
        }
        return super.onStartCommand(intent, flags, startId)

    }

    private var isTimerEnabled = false
    private var lapTime = 0L
    private var timeRun = 0L
    private var timeStarted = 0L
    private var lastSecondTimestamp = 0L

    private fun startTimer() {
        addEmptyPolyline()
        istracking.postValue(true)
        timeStarted = System.currentTimeMillis()
        isTimerEnabled = true
        CoroutineScope(Dispatchers.Main).launch {
            while (istracking.value!!) {
                // time difference between now and timeStarted
                lapTime = System.currentTimeMillis() - timeStarted
                // post the new lapTime
                timeruninmillis.postValue(timeRun + lapTime)
                if (timeruninmillis.value!! >= lastSecondTimestamp + 1000L) {
                    timeruninseconds.postValue(timeruninseconds.value!! + 1)
                    lastSecondTimestamp += 1000L
                }
                delay(50L)
            }
            timeRun += lapTime
        }
    }


    @SuppressLint("MissingPermission")
    private fun updateLocationTracking(isTracking: Boolean) {
        if(isTracking) {
            if(TrackingPermission.hastrackingpermissions(this)) {
                val request = LocationRequest.create().apply {
                    interval = 5000L
                    fastestInterval = 2000L
                    priority = PRIORITY_HIGH_ACCURACY
                }
                fusedLocationProviderClient.requestLocationUpdates(
                    request,
                    locationCallback,
                    Looper.getMainLooper()
                )
            }
        } else {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            if(istracking.value!!) {

                    for(location in result.locations) {
                        addnewPolyline(location)
                        Timber.d("NEW LOCATION: ${location.latitude}, ${location.longitude}")
                    }

            }
        }
    }

    private fun killservice()
    {
        isservicekilled = true
        isfirstrun = true
        isTimerEnabled = false
        istracking.postValue(false)
        postinitialvalues()
        stopForeground(true)
        stopSelf()
    }

    private fun updateNotificationTrackingState(isTracking: Boolean) {
        val notificationActionText = if(isTracking) "Pause" else "Resume"
        val pendingIntent = PendingIntent.getService(
            this,
            if (isTracking) 1 else 2,
            Intent(this, ForeGroundService::class.java).apply {
                action = if (isTracking) ACTION_PAUSE_SERVICE else ACTION_START_OR_RESUME_SERVICE},
            FLAG_UPDATE_CURRENT
        )

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        currNotificationBuilder.javaClass.getDeclaredField("mActions").apply {
            isAccessible = true
            set(currNotificationBuilder, ArrayList<NotificationCompat.Action>())
        }
        if(!isservicekilled){
            currNotificationBuilder = baseNotificationBuilder
                .addAction(R.drawable.ic_baseline_pause_24, notificationActionText, pendingIntent)
            notificationManager.notify(NOTIFICATION_ID, currNotificationBuilder.build())
        }
    }

    private fun startForegroundservice(){

        startTimer()
        istracking.postValue(true)
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            createNotificationChannel(manager)
        }
        startForeground(NOTIFICATION_ID,baseNotificationBuilder.build())


        timeruninseconds.observe(this, Observer {
            if(!isservicekilled){
                val notification = currNotificationBuilder
                    .setContentText(Stopwatch.getFormattedStopWatchTime(it * 1000L))
                manager.notify(NOTIFICATION_ID, notification.build())
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager){
        val channel= NotificationChannel(utilvalues.CHANNEL_ID, utilvalues.CHANNEL_NAME , NotificationManager.IMPORTANCE_LOW)
        notificationManager.createNotificationChannel(channel)
    }



    override fun onDestroy() {
        super.onDestroy()

    }

}