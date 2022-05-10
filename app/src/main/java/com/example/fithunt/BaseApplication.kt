package com.example.fithunt

import android.app.Application
import com.facebook.FacebookSdk
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class BaseApplication:Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        FacebookSdk.sdkInitialize(getApplicationContext());
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

    }
}