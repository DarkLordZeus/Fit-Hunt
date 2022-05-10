package com.example.fithunt

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.example.fithunt.Util.onNavDestinationSelected
import com.example.fithunt.Util.utilvalues
import com.example.fithunt.Util.utilvalues.Companion.ACTION_SHOW_TRACKING_FRAGMENT
import com.example.fithunt.Util.utilvalues.Companion.imageprofiledpuri
import com.example.fithunt.Util.utilvalues.Companion.moving
import com.example.fithunt.Util.utilvalues.Companion.previoustotalsteps
import com.example.fithunt.Util.utilvalues.Companion.totalsteps
import com.example.fithunt.databinding.ActivityMainBinding
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() ,SensorEventListener {
    private var sensorManager:SensorManager?=null

    private lateinit var navController : NavController
    private lateinit var AppBarConfiguration : AppBarConfiguration
    private var cal = Calendar.getInstance()
    private var currentDate = cal.get(Calendar.DAY_OF_YEAR)
    lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setTheme(R.style.Theme_FitHunt)
        setContentView(binding.root)
        loadData()
        utilvalues.shouldloadbio=true
        //resetSteps()
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED){
            //ask for permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                requestPermissions(arrayOf(Manifest.permission.ACTIVITY_RECOGNITION), 0)
            };
        }
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET) == PackageManager.PERMISSION_DENIED){
            //ask for permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                requestPermissions(arrayOf(Manifest.permission.INTERNET), 0)
            };
        }
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
            //ask for permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 0)
            };
        }
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        setSupportActionBar(binding.toolbar)
        val navHostFragment=supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController=navHostFragment.navController
        AppBarConfiguration=AppBarConfiguration(
            setOf(R.id.homeFragment,R.id.profileFragment,R.id.statisticsFragment)
        )

        //after initializing navcontroller
        navigatetoTrackingFragmentifneeded(intent)
        setupActionBarWithNavController(navController,AppBarConfiguration)

        binding.bubbleTabBar.addBubbleListener { id ->
            binding.bubbleTabBar.onNavDestinationSelected(id, navController)
        }
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bubbleTabBar.setSelectedWithId(destination.id, false)
        }


    }
    override fun onSupportNavigateUp() : Boolean
    {
        return navController.navigateUp(AppBarConfiguration)||super.onSupportNavigateUp()
    }
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navigatetoTrackingFragmentifneeded(intent)
    }

    private fun navigatetoTrackingFragmentifneeded(intent: Intent?)
    {
        if(intent?.action == ACTION_SHOW_TRACKING_FRAGMENT){
            navController.navigate(R.id.action_global_trackingFragment)
        }
    }

    override fun onResume() {
        super.onResume()
        moving=true
        val stepsensor=sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        if(stepsensor==null)
        {
            Toast.makeText(this, "No sensor detected on this device", Toast.LENGTH_SHORT).show()
        }
        else
        {

            sensorManager?.registerListener(this,stepsensor,SensorManager.SENSOR_DELAY_UI)
        }
    }
    override fun onSensorChanged(event: SensorEvent?) {
        if(moving==true)
        {
            totalsteps=event!!.values[0]
            if(totalsteps.toInt()==0)
            {
                
                val sharedPreferences = getSharedPreferences("steps", Context.MODE_PRIVATE)
                val editor = sharedPreferences?.edit()
                previoustotalsteps.value=0f
                editor?.putFloat("key1", 0f)
                editor?.apply()
            }
            val currentsteps=totalsteps.toInt() - previoustotalsteps.value!!.toInt()
            utilvalues.steps.value=currentsteps
        }
    }


    private fun loadData() {
        val sharedPreferences = getSharedPreferences("steps", Context.MODE_PRIVATE)
        val editor = sharedPreferences?.edit()
        // In this function we will retrieve data
        val savedNumber = sharedPreferences?.getFloat("key1", 0f)

        // Log.d is used for debugging purposes
        Log.d("MainActivity", "$savedNumber")
        if (savedNumber != null) {
            previoustotalsteps.value = savedNumber
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }
}