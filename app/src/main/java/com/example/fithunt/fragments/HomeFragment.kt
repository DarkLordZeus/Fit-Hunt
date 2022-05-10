package com.example.fithunt.fragments

import android.Manifest
import android.annotation.TargetApi
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.iterator
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.fithunt.FitHuntroomdatabase.FitHuntRoomViewmodel
import com.example.fithunt.R
import com.example.fithunt.Util.TrackingPermission
import com.example.fithunt.Util.utilvalues
import com.example.fithunt.Util.utilvalues.Companion.MetCycling
import com.example.fithunt.Util.utilvalues.Companion.MetRun
import com.example.fithunt.Util.utilvalues.Companion.MetWalk
import com.example.fithunt.Util.utilvalues.Companion.ageofuser
import com.example.fithunt.Util.utilvalues.Companion.bloodgrpofuser
import com.example.fithunt.Util.utilvalues.Companion.genderofuser
import com.example.fithunt.Util.utilvalues.Companion.heightofuser
import com.example.fithunt.Util.utilvalues.Companion.issamedate
import com.example.fithunt.Util.utilvalues.Companion.maxsteps
import com.example.fithunt.Util.utilvalues.Companion.nameofuser
import com.example.fithunt.Util.utilvalues.Companion.previoustotalsteps
import com.example.fithunt.Util.utilvalues.Companion.shouldloadbio
import com.example.fithunt.Util.utilvalues.Companion.steps
import com.example.fithunt.Util.utilvalues.Companion.thisexercisemet
import com.example.fithunt.Util.utilvalues.Companion.todayminutes
import com.example.fithunt.Util.utilvalues.Companion.todaysglassofwater
import com.example.fithunt.Util.utilvalues.Companion.totalsteps
import com.example.fithunt.Util.utilvalues.Companion.weightofuser
import com.example.fithunt.databinding.FragmentHomeBinding
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.AndroidEntryPoint
import io.ak1.BubbleTabBar
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class HomeFragment : Fragment() ,EasyPermissions.PermissionCallbacks {
    private var _binding: FragmentHomeBinding?=null
    private val binding get()=_binding!!
    private val roomViewmodel:FitHuntRoomViewmodel by viewModels()
    val sdf = SimpleDateFormat("dd-MMM-yy")
    val currentDateandTime: String = sdf.format(Date())
    var todaysworkoutduration:Long= 0L
    var todayscalorieburnt:Long =0L
    var currentsteps=0
    lateinit var storageReference: StorageReference



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding=FragmentHomeBinding.inflate(inflater,container,false)
        activity?.findViewById<BubbleTabBar>(R.id.bubbleTabBar)?.visibility=View.VISIBLE
        activity?.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)?.visibility=View.VISIBLE
        if(shouldloadbio) {
            getuserbiometrics()
            shouldloadbio=false
        }
        //FUNCTION TO CHECK FIREBASE USER AND GETS ITS BIO METRICS

        val sharedPreferencess=activity?.getSharedPreferences("shared_preference", Context.MODE_PRIVATE)
        val editor=sharedPreferencess?.edit()
        val changestepsdatecheck=sharedPreferencess?.getString("todaydate","0")
        if(changestepsdatecheck==null || changestepsdatecheck=="0")
        {
            editor?.putString("todaydate",currentDateandTime)
            editor?.apply()
        }
        val glassofwater=sharedPreferencess?.getInt("glassofwater",0)
        if(glassofwater!=null)
        {
            todaysglassofwater.value=glassofwater
        }
        resetSteps()
        roomViewmodel.todayworkoutduration(currentDateandTime).observe(viewLifecycleOwner, Observer {
            if(it!=null)
            {
                todaysworkoutduration = (it / 1000 / 60)
                binding.textViewdtime.text="${steps.value!!/96+todaysworkoutduration.toInt()}"
                binding.progressBartime.progress= steps.value!!/96+todaysworkoutduration.toInt()
            }
        })
        roomViewmodel.todaycalorieburnt(currentDateandTime).observe(viewLifecycleOwner, Observer {
            if(it!=null)
            {
                todayscalorieburnt = it
                binding.textViewdcal.text="${(todayscalorieburnt + (steps.value!!/96)*(utilvalues.MetWalk *3.5* utilvalues.weightofuser.value.toString().toFloat())/200).toInt()}"
                binding.progressBarcalorie.progress= (todayscalorieburnt+(steps.value!!/96)*(utilvalues.MetWalk *3.5* utilvalues.weightofuser.value.toString().toFloat())/200).toInt()
            }
        })
        storageReference= FirebaseStorage.getInstance().reference
        if(utilvalues.imageprofiledpuri.value==null) {
            val profilepicref: StorageReference =
                storageReference.child("${FirebaseAuth.getInstance().currentUser?.uid}/profilepic.jpg")
            profilepicref.downloadUrl.addOnSuccessListener(OnSuccessListener {
                if (it != null)
                    utilvalues.imageprofiledpuri.value = it
            })
        }
        return binding.root
    }

    private fun getuserbiometrics() {
        val sharedPreferencess=activity?.getSharedPreferences("shared_preference", Context.MODE_PRIVATE)
        val editor=sharedPreferencess?.edit()

        binding.llProgressBar.progressbarloadbio.visibility=View.VISIBLE
        activity?.findViewById<BubbleTabBar>(R.id.bubbleTabBar)?.iterator()?.forEach {
            it.isClickable = false
        }
        val ref=FirebaseDatabase.getInstance().getReference("Bio-metrics")
        ref.child(FirebaseAuth.getInstance().currentUser?.uid!!.toString()).get().addOnSuccessListener{
            if(it.exists())
            {
                nameofuser.value= it.child("Name").value as String?
                editor?.putString("tempname",nameofuser.value)
                editor?.apply()
                ageofuser.value=it.child("age").value as String?
                weightofuser.value=it.child("weight").value as String?
                heightofuser.value=it.child("height").value as String?
                bloodgrpofuser.value=it.child("bloodgrp").value as String?
                genderofuser.value=it.child("gender").value as String?
                binding.llProgressBar.progressbarloadbio.visibility=View.GONE
                activity?.findViewById<BubbleTabBar>(R.id.bubbleTabBar)?.iterator()?.forEach {
                    it.isClickable=true

                }
                //activity?.findViewById<BubbleTabBar>(R.id.bubbleTabBar)?.setSelectedWithId(R.id.homeFragment,false)
            }
            else
            {
                Toast.makeText(requireContext(), "User doesn't exist please sign up", Toast.LENGTH_SHORT).show()
                binding.llProgressBar.progressbarloadbio.visibility=View.GONE
                activity?.findViewById<BubbleTabBar>(R.id.bubbleTabBar)?.iterator()?.forEach {
                    it.isClickable = true
                }
                //activity?.findViewById<BubbleTabBar>(R.id.bubbleTabBar)?.setSelectedWithId(R.id.homeFragment,false)
            }

        }.addOnFailureListener(OnFailureListener { e ->
            binding.llProgressBar.progressbarloadbio.visibility=View.GONE
            print(e.message)
        })

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPreferencess=activity?.getSharedPreferences("shared_preference", Context.MODE_PRIVATE)
        val editor=sharedPreferencess?.edit()
        requestpermissions()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            requestBackgroundPermission()

        nameofuser.observe(viewLifecycleOwner, Observer {
            if(it!="--")
                binding.llProgressBar.progressbarloadbio.visibility=View.GONE
        })
        val trackingaction=HomeFragmentDirections.actionHomeFragmentToTrackingFragment()
        binding.imageButtonrunning.setOnClickListener {
            utilvalues.typeofexercise="Running"
            thisexercisemet= MetRun.toFloat()
            findNavController().navigate(trackingaction)
        }
        binding.imageButtonwalking.setOnClickListener {
            utilvalues.typeofexercise="Walking"
            thisexercisemet= MetWalk.toFloat()
            findNavController().navigate(trackingaction)
        }
        binding.imageButtoncycling.setOnClickListener {
            utilvalues.typeofexercise="Cycling"
            thisexercisemet= MetCycling.toFloat()
            findNavController().navigate(trackingaction)
        }
        binding.imageButtonhistory.setOnClickListener {
            val action=HomeFragmentDirections.actionHomeFragmentToExcercisestochoose()
            findNavController().navigate(action)
        }
        binding.openbodycomp.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToBodyComposition()
            findNavController().navigate(action)
        }

        utilvalues.weightofuser.observe(viewLifecycleOwner, Observer {
            binding.weight.text=it
            if(heightofuser.value!="--" && weightofuser.value!="--") {
                utilvalues.bmiofuser =
                    (((it.toString().toFloat() / (heightofuser.value.toString()
                        .toFloat() * heightofuser.value.toString()
                        .toFloat() * 0.3048f * 0.3048f)) * 100).toInt() / 100f).toString()
                binding.bmi.text=utilvalues.bmiofuser+" BMI"
            }
            else{binding.bmi.text=utilvalues.bmiofuser}
        })
    utilvalues.heightofuser.observe(viewLifecycleOwner, Observer {
            binding.height.text = it
            if (weightofuser.value != "--" && heightofuser.value != "--") {
                utilvalues.bmiofuser =
                    (((weightofuser.value.toString().toFloat() / (it.toString()
                        .toFloat() * it.toString()
                        .toFloat() * 0.3048f * 0.3048f)) * 100).toInt() / 100f).toString()
                binding.bmi.text = utilvalues.bmiofuser+" BMI"
            } else {
                binding.bmi.text = utilvalues.bmiofuser
            }
        })
        steps.observe(viewLifecycleOwner, Observer {
            binding.todayssteps.text=it.toString()
            binding.stepprogressbar.progress=it
            binding.stepspercentage.text="${((it.toFloat()/ maxsteps.toFloat())*100).toInt()}%"
            binding.textViewdsteps.text=it.toString()
            binding.progressBarsteps.progress=it
            binding.textViewdtime.text="${it/96+todaysworkoutduration.toInt()}"
            binding.progressBartime.progress= it/96+todaysworkoutduration.toInt()
            weightofuser.observe(viewLifecycleOwner, Observer {  weightofuser->
                if(weightofuser!=null && weightofuser!="--") {
                    binding.textViewdcal.text =
                        "${(todayscalorieburnt + (it / 96) * (utilvalues.MetWalk * 3.5 * weightofuser.toFloat()) / 200).toInt()}"
                    binding.progressBarcalorie.progress =
                        (todayscalorieburnt + (it / 96) * (utilvalues.MetWalk * 3.5 * weightofuser.toFloat()) / 200).toInt()
                }
            })

        })

        binding.stepprogressbar.max= maxsteps
        binding.progressBarsteps.max= maxsteps
        binding.todayswater.text="${todaysglassofwater.value}"
        todaysglassofwater.observe(viewLifecycleOwner, Observer {
            binding.todayswater.text=it.toString()
        })
        binding.floatingActionButtonwateradd.setOnClickListener {
            todaysglassofwater.value= todaysglassofwater.value?.plus(1)
            editor?.putInt("glassofwater", todaysglassofwater.value!!)
            editor?.apply()

        }
        binding.floatingActionButton2waterminus.setOnClickListener {
            if(todaysglassofwater.value!=0) {
                todaysglassofwater.value=todaysglassofwater.value?.minus(1)
                editor?.putInt("glassofwater", todaysglassofwater.value!!)
                editor?.apply()
            }
        }
        var todaytotal=0
        roomViewmodel.todaytotalworkouts(currentDateandTime).observe(viewLifecycleOwner, Observer {
         it?.let {
             todaytotal=it.toInt()
         }
        })
        binding.opendailyactivity.setOnClickListener {
            val dailyactaction = HomeFragmentDirections.actionHomeFragmentToDailyactivitiesdata(
                binding.textViewdsteps.text.toString().toInt().toLong(),
                binding.textViewdtime.text.toString().toLong(),
                binding.textViewdcal.text.toString().toLong(),
                todaytotal,
                if(todaysglassofwater.value!=null)
                    todaysglassofwater.value!!.toInt()
            else 0
            )
            findNavController().navigate(dailyactaction)
        }
    }

    private fun requestpermissions(){
        if(TrackingPermission.hastrackingpermissions(requireContext())){
            return
        }
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q)
        {
            EasyPermissions.requestPermissions(this,
                "You need to accept location permission to use this app",
                utilvalues.REQUEST_CODE_LOCATION_PERMISSION,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.INTERNET
            )
        }
        else{
            EasyPermissions.requestPermissions(this,
                "You need to accept location permission to use this app",
                utilvalues.REQUEST_CODE_LOCATION_PERMISSION,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.INTERNET)
        }
    }


    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {

    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if(EasyPermissions.somePermissionPermanentlyDenied(this,perms))
        {
            AppSettingsDialog.Builder(this).build().show()
        }
        else{
            requestpermissions()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this)
    }
    @RequiresApi(Build.VERSION_CODES.Q)
    fun requestBackgroundPermission() {
        if (checkPermissionGranted(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) return
        AlertDialog.Builder(requireContext())
            .setTitle("Background location permission")
            .setMessage("Allow location permission to get location updates in background")
            .setPositiveButton("Allow") { _, _ ->
                ActivityCompat.requestPermissions(requireActivity(),
                    arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                    222)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    fun resetSteps() {
        val sharedPreferencess=activity?.getSharedPreferences("shared_preference", Context.MODE_PRIVATE)
        val editor=sharedPreferencess?.edit()
        if(sharedPreferencess?.getString("todaydate","0")!=currentDateandTime)
        {
            previoustotalsteps.value= totalsteps
            currentsteps= steps.value!!
            steps.value=0
            saveData()
            Toast.makeText(requireContext(), "Steps are now reset", Toast.LENGTH_SHORT).show()

            editor?.putString("todaydate",currentDateandTime)
            editor?.apply()
        }
        binding.todayssteps.setOnClickListener {
            // This will give a toast message if the user want to reset the steps
            Toast.makeText(requireContext(), "Long tap to reset steps", Toast.LENGTH_SHORT).show()
        }

        binding.todayssteps.setOnLongClickListener {
            previoustotalsteps.value = totalsteps
            currentsteps= steps.value!!
            steps.value=0
            saveData()
            true
        }
        previoustotalsteps.observe(viewLifecycleOwner, Observer {
            if(previoustotalsteps.value?.toInt()==0){
                saveData()
            }
        }
        )

    }

    private fun saveData() {
        // Shared Preferences will allow us to save
        // and retrieve data in the form of key,value pair.
        // In this function we will save data
        val sharedPreferences = context?.getSharedPreferences("steps", Context.MODE_PRIVATE)
        val editor = sharedPreferences?.edit()
        if(sharedPreferences?.getInt("alltimesteps",0)!=null)
        {
            editor?.putInt("alltimesteps",
                (currentsteps+ sharedPreferences.getInt("alltimesteps", 0)).toInt())
        }
        else{
            editor?.putInt("alltimesteps", currentsteps)
        }
        editor?.putFloat("key1", previoustotalsteps.value!!)

        //Toast.makeText(requireContext(), "${sharedPreferences?.getFloat("key1", previoustotalsteps)}", Toast.LENGTH_SHORT).show()
        editor?.apply()
    }

    private fun checkPermissionGranted(permission: String) : Boolean {
        return ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }
}
//        val sharedPreferences = context?.getSharedPreferences("steps", Context.MODE_PRIVATE)
//        val editor = sharedPreferences?.edit()
//        val saveddate=sharedPreferences?.getInt("date",0)
//        if(saveddate!=currentDate){
//            Toast.makeText(requireContext(), "${saveddate}", Toast.LENGTH_SHORT).show()
//            issamedate=false
//            editor?.putInt("date",currentDate)
//            resetSteps()
//        }
