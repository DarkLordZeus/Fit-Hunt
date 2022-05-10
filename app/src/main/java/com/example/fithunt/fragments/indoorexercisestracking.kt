package com.example.fithunt.fragments

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.fithunt.FitHuntroomdatabase.FitHuntEntity
import com.example.fithunt.FitHuntroomdatabase.FitHuntRoomViewmodel
import com.example.fithunt.R
import com.example.fithunt.Util.Stopwatch
import com.example.fithunt.Util.utilvalues
import com.example.fithunt.databinding.FragmentIndoorexercisestrackingBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import io.ak1.BubbleTabBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
open class indoorexercisestracking : Fragment() {

    private var _binding: FragmentIndoorexercisestrackingBinding?=null
    private val binding get()=_binding!!
    var isfirstrun = true
    private val timeinseconds=MutableLiveData<Long>(0L)
    private var currenttimeinmills =0L
    private var isTracking = false
    val timeinmillis = MutableLiveData<Long>()
    val istrackingindoor = MutableLiveData<Boolean>()
    private val roomViewmodel: FitHuntRoomViewmodel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding= FragmentIndoorexercisestrackingBinding.inflate(inflater,container,false)
        activity?.findViewById<BubbleTabBar>(R.id.bubbleTabBar)?.visibility=View.GONE
        (requireActivity() as AppCompatActivity).supportActionBar?.title = utilvalues.typeofexercise
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.startinexc.setOnClickListener {
            togglerun()
        }
        attachtoobservers()
        binding.endexc.setOnClickListener {
            saveexctodb()
            //runsavetoDatabase()
            isfirstrun = true
            isTimerEnabled = false
            timeinseconds.postValue(0L)
            timeinmillis.postValue(0L)
            istrackingindoor.postValue(false)

            findNavController().popBackStack()
        }
    }

    private fun saveexctodb() {
        var drawableindoor: Int =R.drawable.indorrexcercises
        when(utilvalues.typeofexercise){
            "Tread Mill"->{drawableindoor = R.drawable.treadmilldb}
            "Swimming"->{drawableindoor=R.drawable.dbswimming}
            "Yoga"->{drawableindoor=R.drawable.dbyoga}
            "Calisthenics"->{drawableindoor=R.drawable.calisthenicsdb}
            "Pilates"->{drawableindoor=R.drawable.pilatesdb}
        }
        val bmp = BitmapFactory.decodeResource(resources, drawableindoor)
        var distanceinmeters=0
        var avgspeed = 0f
        var calorieslost = ((currenttimeinmills/1000f/60)*(utilvalues.thisexercisemet *3.5* utilvalues.weightofuser.value.toString().toFloat())/200).toInt().toLong()
        val sdf = SimpleDateFormat("dd.MMM.yy")
        val currentDateandTime: String = sdf.format(Date())
        val fitrun= FitHuntEntity(0,bmp,currentDateandTime,avgspeed,distanceinmeters,currenttimeinmills,calorieslost,
            utilvalues.typeofexercise)

        roomViewmodel.inserttrack(fitrun)
        Snackbar.make(
            requireActivity().findViewById(R.id.rootView),
            "Saved successfully", Snackbar.LENGTH_LONG
        ).show()
    }

    private fun togglerun()
    {
        if(isTracking)
        {   isTimerEnabled = false
            istrackingindoor.postValue(false)
            binding.endexc.visibility =View.VISIBLE}
        else
        {
            if(isfirstrun)
            {
                startcountingtime()
                isfirstrun=false
            }
            else{
                starttimer()

            }
            binding.endexc.visibility =View.GONE
        }
    }
    private fun attachtoobservers()
    {
        istrackingindoor.observe(viewLifecycleOwner, Observer {
            updateTracking(it)
        })

        timeinmillis.observe(viewLifecycleOwner, Observer {
            currenttimeinmills = it
            val formattedTime = Stopwatch.getFormattedStopWatchTime(it,true)
            binding.timerindoor.text = formattedTime
        })
    }
    fun startcountingtime(){
        starttimer()
        istrackingindoor.postValue(true)
    }
    private fun updateTracking(istracking:Boolean)
    {
        this.isTracking=istracking
        if(!isTracking) {
            binding.startinexc.text = "Start"
            binding.endexc.visibility = View.VISIBLE
        } else {
            binding.startinexc.text = "Stop"
            binding.endexc.visibility = View.GONE
        }

    }
    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }
    private var isTimerEnabled = false
    private var lapTime = 0L
    private var timeRun = 0L
    private var timeStarted = 0L
    private var lastSecondTimestamp = 0L

    private fun starttimer() {
        istrackingindoor.postValue(true)
        timeStarted = System.currentTimeMillis()
        isTimerEnabled = true
        CoroutineScope(Dispatchers.Main).launch {
            while (istrackingindoor.value!!) {
                // time difference between now and timeStarted
                lapTime = System.currentTimeMillis() - timeStarted
                // post the new lapTime
                timeinmillis.value=(timeRun + lapTime)
                if (timeinmillis.value!! >= lastSecondTimestamp + 1000L) {
                    timeinseconds.value=(timeinseconds.value!! + 1)
                    lastSecondTimestamp += 1000L
                }
                delay(50L)
            }
            timeRun += lapTime
        }
    }


}