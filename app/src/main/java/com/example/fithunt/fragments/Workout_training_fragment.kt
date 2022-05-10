package com.example.fithunt.fragments

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.fithunt.R
import com.example.fithunt.adapter.TrainingExcercisesAdapter
import com.example.fithunt.adapter.Training_details
import com.example.fithunt.databinding.FragmentWorkoutTrainingFragmentBinding
import io.ak1.BubbleTabBar


class Workout_training_fragment : Fragment() {
    private var _binding: FragmentWorkoutTrainingFragmentBinding?=null
    private val binding get()=_binding!!
    lateinit var training_adapter : TrainingExcercisesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding= FragmentWorkoutTrainingFragmentBinding.inflate(inflater,container,false)
        activity?.findViewById<BubbleTabBar>(R.id.bubbleTabBar)?.visibility=View.VISIBLE
        activity?.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)?.visibility=View.VISIBLE
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setuprecyclerview()


    }

    private fun setuprecyclerview() {
        val weight_gain=Training_details(R.drawable.girlgymming,"Weight Gain", Uri.parse("https://www.myfitfuel.in/mffblog/exercise-gain-weight-home-men-women/"))
        val waist_slimming=Training_details(R.drawable.waistmeasure,"Waist Slimming", Uri.parse("https://skinnyms.com/12-exercises-for-a-smaller-waist-and-shapely-hips/"))
        val strength_gain=Training_details(R.drawable.liftweight,"Strength Training", Uri.parse("https://eliteclubs.com/6-strength-training-moves-you-should-try-today/"))
        val meditation=Training_details(R.drawable.morningfarm,"Meditation", Uri.parse("https://www.livehappy.com/practice/6-steps-to-mindfulness-meditation"))
        val circuit_training=Training_details(R.drawable.circuittraining,"Circuit Training", Uri.parse("https://www.verywellfit.com/circuit-training-workout-1230851"))
        val yoga_peace=Training_details(R.drawable.yoga,"Yoga for Peace", Uri.parse("https://protips.dickssportinggoods.com/sports-and-activities/yoga-and-studio/8-yoga-poses-relax-mind"))
        val arrayList=arrayListOf<Training_details>(weight_gain,waist_slimming,strength_gain,meditation,circuit_training,yoga_peace)
        training_adapter=TrainingExcercisesAdapter(arrayList)
        binding.recylerviewtrainings.adapter=training_adapter
        binding.recylerviewtrainings.layoutManager=GridLayoutManager(requireContext(),3)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }
}
