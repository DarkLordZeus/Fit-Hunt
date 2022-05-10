package com.example.fithunt.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.fithunt.R
import com.example.fithunt.Util.utilvalues
import com.example.fithunt.databinding.FragmentExcercisestochooseBinding
import com.example.fithunt.databinding.FragmentSettingBinding
import io.ak1.BubbleTabBar

class Excercisestochoose : Fragment() {
    private var _binding: FragmentExcercisestochooseBinding?=null
    private val binding get()=_binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding= FragmentExcercisestochooseBinding.inflate(inflater,container,false)
        activity?.findViewById<BubbleTabBar>(R.id.bubbleTabBar)?.visibility=View.VISIBLE
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val trackingaction=ExcercisestochooseDirections.actionExcercisestochooseToTrackingFragment()
        val indooraction=ExcercisestochooseDirections.actionExcercisestochooseToIndoorexercisestracking()
        binding.fabstartrunning.setOnClickListener {
            utilvalues.typeofexercise="Running"
            utilvalues.thisexercisemet = utilvalues.MetRun.toFloat()
            findNavController().navigate(trackingaction)
        }
        binding.fabstartwalking.setOnClickListener {
            utilvalues.typeofexercise="Walking"
            utilvalues.thisexercisemet = utilvalues.MetRun.toFloat()
            findNavController().navigate(trackingaction)
        }
        binding.fabstartcycling.setOnClickListener {
            utilvalues.typeofexercise="Cycling"
            utilvalues.thisexercisemet = utilvalues.MetCycling.toFloat()
            findNavController().navigate(trackingaction)
        }
        binding.fabstarthiking.setOnClickListener {
            utilvalues.typeofexercise="Hiking"
            utilvalues.thisexercisemet = utilvalues.MetHiking.toFloat()
            findNavController().navigate(trackingaction)
        }
        ///restdontneedmaps
        binding.fabstarttreadmill.setOnClickListener {
            utilvalues.typeofexercise="Tread Mill"
            utilvalues.thisexercisemet = utilvalues.MetTreadmill.toFloat()
            findNavController().navigate(indooraction)
        }
        binding.fabstartswimming.setOnClickListener {
            utilvalues.typeofexercise="Swimming"
            utilvalues.thisexercisemet = utilvalues.MetSwimming.toFloat()
            findNavController().navigate(indooraction)
        }
        binding.fabstartyoga.setOnClickListener {
            utilvalues.typeofexercise="Yoga"
            utilvalues.thisexercisemet = utilvalues.MetYoga.toFloat()
            findNavController().navigate(indooraction)
        }
        binding.fabstartcalisthenics.setOnClickListener {
            utilvalues.typeofexercise="Calisthenics"
            utilvalues.thisexercisemet = utilvalues.Metcalisthenics.toFloat()
            findNavController().navigate(indooraction)
        }
        binding.fabstartpilates.setOnClickListener {
            utilvalues.typeofexercise="Pilates"
            utilvalues.thisexercisemet = utilvalues.MetPilates.toFloat()
            findNavController().navigate(indooraction)
        }

    }
    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }
}