package com.example.fithunt.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.fithunt.R
import com.example.fithunt.Util.utilvalues
import com.example.fithunt.databinding.FragmentBodyCompositionBinding
import com.example.fithunt.databinding.FragmentSettingBinding


class BodyComposition : Fragment() {
    private var _binding: FragmentBodyCompositionBinding?=null
    private val binding get()=_binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding= FragmentBodyCompositionBinding.inflate(inflater,container,false)
        val sharedPreferencess=activity?.getSharedPreferences("shared_preference", Context.MODE_PRIVATE)
        val editor=sharedPreferencess?.edit()
        var weight=utilvalues.weightofuser.value.toString().toFloat()
        val height=utilvalues.heightofuser.value.toString().toFloat()
        binding.progressBar.max=108
        if(weight<62f){
            binding.progressBar.progress= (weight- 26.7f).toInt()
        }
        else{
            binding.progressBar.progress= ((weight-62f)*(1.756f)+(35.3f)).toInt()
        }
        binding.progressBar2.max=90
        if(height<5f){
            binding.progressBar2.progress=(height*10f -20f).toInt()
        }
        else{
            binding.progressBar2.progress=((height*10f - 50f)*(1.66f)+30f).toInt()
        }
        binding.progressBar3.max=315
        val bmi=(((weight /(height *height*0.3048f*0.3048f))*100).toInt()/100f)
        if(bmi<18.5f)
        {
            binding.progressBar3.progress=(bmi*10f - 80f).toInt()
        }
        else{
            binding.progressBar3.progress=((bmi*10f - 185f)*(1.76f)+105f).toInt()
        }

        binding.textViewweighttt.text= "$weight kg"
        binding.textViewheighttt.text= "$height ft"
        binding.textViewbmiii.text=(((weight /(height *height*0.3048f*0.3048f))*100).toInt()/100f).toString()
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.openalltraining.setOnClickListener {
            findNavController().navigate(R.id.workout_training_fragment)
        }

    }
    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }
}