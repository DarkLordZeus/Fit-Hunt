package com.example.fithunt.hildagger

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.fithunt.MainActivity
import com.example.fithunt.R
import com.example.fithunt.Util.utilvalues
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped

@Module
@InstallIn(ServiceComponent::class)
object ServiceModule {

    @ServiceScoped
    @Provides
    fun fusedlocationProviderclient( @ApplicationContext app:Context)=FusedLocationProviderClient(app)

    @ServiceScoped
    @Provides
    fun getmainactivtypendingintent(@ApplicationContext app:Context) = PendingIntent.getActivity(
        app,
        0,
        Intent(app, MainActivity::class.java).also {
            it.action = utilvalues.ACTION_SHOW_TRACKING_FRAGMENT
        },
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    @ServiceScoped
    @Provides
    fun  providenotificationBuilder(@ApplicationContext app:Context , pendingIntent: PendingIntent) = NotificationCompat.Builder(app, utilvalues.CHANNEL_ID)
        .setAutoCancel(false)
        .setOngoing(true)
        .setSmallIcon(R.drawable.running)
        .setContentTitle("Running")
        .setContentText("00:00:00")
        .setContentIntent(pendingIntent)
}