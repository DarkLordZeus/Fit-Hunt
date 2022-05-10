package com.example.fithunt.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.fithunt.R
import com.example.fithunt.Util.utilvalues
import com.example.fithunt.databinding.BiometricfragmentBinding
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import io.ak1.BubbleTabBar


class BiometricFragment : Fragment() {
    private var _binding: BiometricfragmentBinding?=null
    private val binding get()=_binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding= BiometricfragmentBinding.inflate(inflater,container,false)
        activity?.findViewById<BubbleTabBar>(R.id.bubbleTabBar)?.visibility=View.GONE
        activity?.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)?.visibility=View.GONE
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val firebasedb:FirebaseDatabase= FirebaseDatabase.getInstance()
        val rootref:DatabaseReference=firebasedb.reference.child("Bio-metrics").child(FirebaseAuth.getInstance().currentUser?.uid!!.toString())
        val HashMapdb: HashMap<String, String> = HashMap()
        val sharedPreferencess=activity?.getSharedPreferences("shared_preference", Context.MODE_PRIVATE)
        val editor=sharedPreferencess?.edit()
        binding.layout.setOnClickListener {
            binding.dropdownBldgrp.hideDropdown()
            binding.dropdownGender.hideDropdown()
        }
        binding.name.setOnClickListener {
            binding.dropdownBldgrp.hideDropdown()
            binding.dropdownGender.hideDropdown()
        }
        var gender="--"
        var bloodgrp="--"
        binding.dropdownBldgrp.setItemClickListener{ i,item->
            bloodgrp=item.text
        }
        binding.dropdownGender.setItemClickListener{i,item->
            gender=item.text
        }

        binding.savencon.setOnClickListener {
            if(!binding.name.text.isNullOrBlank() && !binding.age.text.isNullOrBlank() && !binding.age.text.isNullOrBlank() && !binding.height.text.isNullOrBlank()) {
                //adding to firebase database
                HashMapdb.apply {
                    put("Name",binding.name.text.toString())
                    put("age",binding.age.text.toString())
                    put("height",binding.height.text.toString().toFloat().toString())
                    put("weight",binding.weight.text.toString().toFloat().toString())
                    put("bloodgrp",bloodgrp)
                    put("gender",gender)

                }
                rootref.setValue(HashMapdb).addOnSuccessListener(
                    OnSuccessListener<Any?> {
                    // Local temp file has been created
                        //toast wont work as we are removing all backstack data
                        //Toast.makeText(requireActivity().applicationContext, "success", Toast.LENGTH_SHORT).show()

                })
                    .addOnFailureListener(OnFailureListener { e ->
                        print(e.message)
                    })


                editor?.apply()
                findNavController().navigate(R.id.action_biometricFragment_to_homeFragment)
            }
            else
                Toast.makeText(requireContext(), "Please fill up all fields", Toast.LENGTH_SHORT).show()


        }

    }
    override fun onDestroy() {
        super.onDestroy()

        _binding=null
    }
}