package com.example.fithunt.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.fithunt.FitHuntroomdatabase.FitHuntEntity
import com.example.fithunt.FitHuntroomdatabase.FitHuntRoomViewmodel
import com.example.fithunt.ForeGroundService
import com.example.fithunt.R
import com.example.fithunt.Util.Stopwatch
import com.example.fithunt.Util.utilvalues
import com.example.fithunt.Util.utilvalues.Companion.ACTION_PAUSE_SERVICE
import com.example.fithunt.Util.utilvalues.Companion.ACTION_START_OR_RESUME_SERVICE
import com.example.fithunt.Util.utilvalues.Companion.ACTION_STOP_SERVICE
import com.example.fithunt.Util.utilvalues.Companion.MAP_ZOOM
import com.example.fithunt.Util.utilvalues.Companion.MetWalk
import com.example.fithunt.Util.utilvalues.Companion.POLYLINE_COLOR
import com.example.fithunt.Util.utilvalues.Companion.POLYLINE_WIDTH
import com.example.fithunt.Util.utilvalues.Companion.thisexercisemet
import com.example.fithunt.Util.utilvalues.Companion.typeofexercise
import com.example.fithunt.Util.utilvalues.Companion.weightofuser
import com.example.fithunt.databinding.FragmentTrackingBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import io.ak1.BubbleTabBar
import java.lang.Math.round
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class TrackingFragment : Fragment() {
    private var _binding: FragmentTrackingBinding?=null
    private val binding get()=_binding!!
    private val roomViewmodel: FitHuntRoomViewmodel by viewModels()
    private var map:GoogleMap?= null
    private var currenttimeinmills =0L
    private var isTracking = false
    private var pathPoints = mutableListOf<com.example.fithunt.Polyline>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding= FragmentTrackingBinding.inflate(inflater,container,false)
        activity?.findViewById<BubbleTabBar>(R.id.bubbleTabBar)?.visibility=View.GONE
        (requireActivity() as AppCompatActivity).supportActionBar?.title = utilvalues.typeofexercise
        binding.mapView.getMapAsync {
            map = it
            connectallpolyline()
        }

        return binding.root
    }
    @SuppressLint("ResourceType")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mapView.onCreate(savedInstanceState)
        binding.btnToggleRun.setOnClickListener {
            togglerun()
            }
        attachtoobservers()
        binding.btnFinishRun.setOnClickListener {
            if(pathPoints.last().size <=1)
                Toast.makeText(requireContext(), "Cannot be saved due to small tracksize", Toast.LENGTH_SHORT).show()
            else{
            zoomtoseeWholetrack()
            runsavetoDatabase()}
            sendCommandtoservice(ACTION_STOP_SERVICE)
            findNavController().navigate(TrackingFragmentDirections.actionTrackingFragmentToHomeFragment())
        }
    }

    private fun togglerun()
    {
        if(isTracking)
        {sendCommandtoservice(ACTION_PAUSE_SERVICE)
            binding.btnFinishRun.visibility =View.VISIBLE}
        else
        {
            sendCommandtoservice(ACTION_START_OR_RESUME_SERVICE)
            binding.btnFinishRun.visibility =View.GONE
        }
    }

    private fun attachtoobservers()
    {
        ForeGroundService.istracking.observe(viewLifecycleOwner, Observer {
            updateTracking(it)
        })

        ForeGroundService.pathpoints.observe(viewLifecycleOwner , Observer {
            pathPoints=it
            connectlatestpolyline()
            moveCameraToUser()
        })

        ForeGroundService.timeruninmillis.observe(viewLifecycleOwner, Observer {
            currenttimeinmills = it
            val formattedTime = Stopwatch.getFormattedStopWatchTime(it,true)
            binding.tvTimer.text = formattedTime
        })
    }

    private fun updateTracking(istracking:Boolean)
    {
        this.isTracking=istracking
        if(!isTracking) {
            binding.btnToggleRun.text = "Start"
            binding.btnFinishRun.visibility = View.VISIBLE
        } else {
            binding.btnToggleRun.text = "Stop"
            binding.btnFinishRun.visibility = View.GONE
        }

    }

    private fun moveCameraToUser() {
        if(pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()) {
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    pathPoints.last().last(),
                    MAP_ZOOM
                )
            )
        }
    }

    private fun connectallpolyline()
    {
        for(polyline in pathPoints)
        {
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .addAll(polyline)
            map?.addPolyline(polylineOptions)
        }
    }

    private fun connectlatestpolyline()
    {
        if(pathPoints.isNotEmpty() && pathPoints.last().size >1 )
        {
            val prelastpolylinelatlng = pathPoints.last()[pathPoints.last().size -2]
            val lastpolylinelatlong = pathPoints.last().last()
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .add(prelastpolylinelatlng)
                .add(lastpolylinelatlong)

            map?.addPolyline(polylineOptions)

        }
    }

    private fun sendCommandtoservice(action:String)
    {
        Intent(requireContext(),ForeGroundService::class.java).also {
            it.action= action
            requireContext().startService(it)

        }
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()

        _binding=null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }

    private fun zoomtoseeWholetrack()
    {
        val bounds = LatLngBounds.builder()
        for (polyline in pathPoints)
        {
            for(pos in polyline)
            {
                bounds.include(pos)
            }
        }

        map?.moveCamera(CameraUpdateFactory.newLatLngBounds(
            bounds.build(),
            binding.mapView.width,
            binding.mapView.height,
            (binding.mapView.height * 0.05f).toInt()
        ))
    }

    private fun runsavetoDatabase(){
        map?.snapshot { bmp->
            var distanceinmeters = 0
            for(polylines in pathPoints)
            {
                distanceinmeters += Stopwatch.calculatepolylineDistance(polylines).toInt()
            }
            var avgspeed = round((distanceinmeters/1000f)/(currenttimeinmills/1000f/60/60) * 10) /10f
            var calorieslost = ((currenttimeinmills/1000f/60)*(thisexercisemet*3.5* weightofuser.value.toString().toFloat())/200).toInt().toLong()
            val sdf = SimpleDateFormat("dd.MMM.yy")
            val currentDateandTime: String = sdf.format(Date())
            val fitrun=FitHuntEntity(0,bmp,currentDateandTime,avgspeed,distanceinmeters,currenttimeinmills,calorieslost,
                typeofexercise)

            roomViewmodel.inserttrack(fitrun)
            Snackbar.make(
                requireActivity().findViewById(R.id.rootView),
                "Saved successfully",Snackbar.LENGTH_LONG
            ).show()
        }

    }
}
