package com.example.fithunt.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.fithunt.R
import com.example.fithunt.Util.utilvalues.Companion.emailRegex
import com.example.fithunt.databinding.SignupfragmentBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.AndroidEntryPoint
import io.ak1.BubbleTabBar


@AndroidEntryPoint
class Signupfragment : Fragment() {
    private var _binding: SignupfragmentBinding?=null
    private val binding get()=_binding!!
    lateinit var firebaseauth:FirebaseAuth
    lateinit var firebaseUser: FirebaseUser
    var builder: AlertDialog.Builder? = null
    var progressDialog: AlertDialog? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding= SignupfragmentBinding.inflate(inflater,container,false)
        activity?.findViewById<BubbleTabBar>(R.id.bubbleTabBar)?.visibility=View.GONE
        activity?.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)?.visibility=View.GONE
        progressDialog=getDialogProgressBar()?.create()
        firebaseauth= FirebaseAuth.getInstance()
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.signup.setOnClickListener {
            PerformAuth()
        }


    }
    fun getDialogProgressBar(): AlertDialog.Builder? {
        if (builder == null) {
            builder = AlertDialog.Builder(requireContext())
            builder!!.setTitle("Registering...")
            val progressBar = ProgressBar(requireContext())
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)
            lp.height=200
            progressBar.layoutParams = lp
            builder!!.setView(progressBar)
        }
        return builder
    }

    private fun PerformAuth() {

        val email=binding.editTextemail.text.toString()
        val password=binding.editTextpassword.text.toString()
        val confirmpassword=binding.editTextconfirmpass.text.toString()
        if(!emailRegex.matcher(email).matches()){
            binding.editTextemail.requestFocus()
            binding.editTextemail.error = "Enter Correct Email"
        }
        else if(password.isNullOrEmpty()||password.length<6)
        {
            binding.editTextpassword.requestFocus()
            binding.editTextpassword.error = "Enter Proper password"
        }
        else if(!password.toString().equals(confirmpassword.toString()))
        {
            binding.editTextconfirmpass.requestFocus()
            binding.editTextconfirmpass.error = "Confirm password didn't matched"
        }
        else
        {
            progressDialog?.show()
            firebaseauth.createUserWithEmailAndPassword(email,
                password).addOnCompleteListener { task->
                if(task.isSuccessful){
                    progressDialog?.dismiss()
                    val action=SignupfragmentDirections.actionSignupfragmentToBiometricFragment()
                    findNavController().navigate(action)
                }
                else{
                    progressDialog?.dismiss()
                    Toast.makeText(requireContext(), ""+task.exception?.localizedMessage, Toast.LENGTH_SHORT).show()
                }

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }
}