package com.example.fithunt.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.fithunt.R
import com.example.fithunt.databinding.FragmentDailyactivitiesdataBinding
import com.example.fithunt.databinding.FragmentSettingBinding


class Dailyactivitiesdata : Fragment() {
    private var _binding: FragmentDailyactivitiesdataBinding?=null
    private val binding get()=_binding!!
    private val args:DailyactivitiesdataArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding= FragmentDailyactivitiesdataBinding.inflate(inflater,container,false)

        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.progressView.apply {
            setInnerProgress(args.steps.toInt())
            setCenterProgress(args.activetime.toInt())
            setOuterProgress(args.calorieburnt.toInt())
        }
        binding.DASTEPS.text=args.steps.toString()
        binding.DATIME.text=args.activetime.toString()
        binding.DAKCAL.text=args.calorieburnt.toString()
        binding.glassesofwater.text=args.glassesofwater.toString()
        binding.totalexcperform.text=args.totalexcperformed.toString()
        binding.openalltraining.setOnClickListener {
            findNavController().navigate(R.id.workout_training_fragment)
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }
}