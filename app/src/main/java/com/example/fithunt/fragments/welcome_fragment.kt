package com.example.fithunt.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.fithunt.R
import com.example.fithunt.Util.OnSwipeTouchListener
import com.example.fithunt.Util.utilvalues
import com.example.fithunt.databinding.FragmentWelcomeFragmentBinding

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import io.ak1.BubbleTabBar


class welcome_fragment : Fragment() {
    private var _binding: FragmentWelcomeFragmentBinding?=null
    private val binding get()=_binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        activity?.findViewById<BubbleTabBar>(R.id.bubbleTabBar)?.visibility=View.GONE
        activity?.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)?.visibility=View.GONE
        _binding= FragmentWelcomeFragmentBinding.inflate(inflater,container,false)
        val ref=FirebaseDatabase.getInstance().getReference("Bio-metrics")
        ref.child(FirebaseAuth.getInstance().currentUser?.uid!!.toString()).get().addOnSuccessListener{
            if(it.exists())
            { utilvalues.nameofuser.value= it.child("Name").value as String?}}
        val sharedPreferencess=activity?.getSharedPreferences("shared_preference", Context.MODE_PRIVATE)
        val editor=sharedPreferencess?.edit()
        utilvalues.nameofuser.observe(viewLifecycleOwner, Observer {
            if(sharedPreferencess?.getString("tempname","--")=="--")
                binding.welcomebackuser.text="Hello ${it}!"
            else
            binding.welcomebackuser.text="Hello ${sharedPreferencess?.getString("tempname","--")}!"
        })
        binding.textViewmotivate.text=utilvalues.quotes.random()


        return binding.root
    }
    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.imageViewswipeup.setOnTouchListener(object : OnSwipeTouchListener(requireContext()) {
            override fun onSwipeLeft() {
                super.onSwipeLeft()
            }
            override fun onSwipeRight() {
                super.onSwipeRight()
            }
            override fun onSwipeUp() {
                super.onSwipeUp()
                findNavController().navigate(welcome_fragmentDirections.actionWelcomeFragmentToHomeFragment())
            }
            override fun onSwipeDown() {
                super.onSwipeDown()
            }
        })

    }
    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }
}
