package com.example.fithunt.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.view.menu.MenuBuilder
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.fithunt.FitHuntroomdatabase.FitHuntRoomViewmodel
import com.example.fithunt.R
import com.example.fithunt.Util.utilvalues
import com.example.fithunt.Util.utilvalues.Companion.heightofuser
import com.example.fithunt.Util.utilvalues.Companion.shouldloadbio
import com.example.fithunt.Util.utilvalues.Companion.weightofuser
import com.example.fithunt.databinding.FragmentProfileBinding
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.facebook.HttpMethod
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import io.ak1.BubbleTabBar
import kotlin.math.roundToInt


@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding?=null
    private val binding get()=_binding!!
    private val roomViewmodel: FitHuntRoomViewmodel by viewModels()
    lateinit var mGoogleSignInClient: GoogleSignInClient
    private val auth by lazy {
        FirebaseAuth.getInstance()
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding= FragmentProfileBinding.inflate(inflater,container,false)
        activity?.findViewById<BubbleTabBar>(R.id.bubbleTabBar)?.visibility=View.VISIBLE
        activity?.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)?.visibility=View.VISIBLE

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient= GoogleSignIn.getClient(requireActivity(),gso)

        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        //val sharedPreferencess=activity?.getSharedPreferences("shared_preference", Context.MODE_PRIVATE)
        utilvalues.nameofuser.observe(viewLifecycleOwner, Observer {
            binding.nameprofile.text=it
        })
        utilvalues.ageofuser.observe(viewLifecycleOwner, Observer {
            binding.textViewageval.text=it
        })
        utilvalues.weightofuser.observe(viewLifecycleOwner, Observer {
            binding.textViewweight.text=it
            if(heightofuser.value!="--" && weightofuser.value!="--") {
                utilvalues.bmiofuser =
                    (((it.toString().toFloat() / (heightofuser.value.toString()
                        .toFloat() * heightofuser.value.toString()
                        .toFloat() * 0.3048f * 0.3048f)) * 100).toInt() / 100f).toString()
                binding.textViewbmi.text=utilvalues.bmiofuser
            }
            else{binding.textViewbmi.text=utilvalues.bmiofuser}
        })
        utilvalues.heightofuser.observe(viewLifecycleOwner, Observer {
            binding.textViewheight.text=it
            if(weightofuser.value!="--" && heightofuser.value!="--") {
                utilvalues.bmiofuser =
                    (((weightofuser.value.toString().toFloat() / (it.toString()
                        .toFloat() * it.toString()
                        .toFloat() * 0.3048f * 0.3048f)) * 100).toInt() / 100f).toString()
                binding.textViewbmi.text=utilvalues.bmiofuser
            }
            else{binding.textViewbmi.text=utilvalues.bmiofuser}
        })
        utilvalues.genderofuser.observe(viewLifecycleOwner, Observer {
            binding.textViewgender.text=it
        })
        utilvalues.bloodgrpofuser.observe(viewLifecycleOwner, Observer {
            binding.textViewbloodgrp.text=it

        })



        binding.editbio.setOnClickListener {
            val action=ProfileFragmentDirections.actionProfileFragmentToNewbiometrics()
            findNavController().navigate(action)
        }
        utilvalues.imageprofiledpuri.observe(viewLifecycleOwner, Observer {
            if(it!=null)
            {
                Glide.with(requireContext()).load(it).into(binding.imageViewprofiledp)
            }
            else{
                if(utilvalues.genderofuser.value.equals("Male",true))
                    Glide.with(requireContext()).load(R.drawable.malevector).into(binding.imageViewprofiledp)
                else
                    Glide.with(requireContext()).load(R.drawable.womenvector).into(binding.imageViewprofiledp)
            }
        })

        roomViewmodel.avgspeedofAllTracks.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.textViewavgspeed.text=((it * 10f).roundToInt() / 10f).toString()
            }
        })
        roomViewmodel.sumofdistancesofAllTracks.observe(viewLifecycleOwner, Observer {
            it?.let {
                val km = it / 1000f
                val totalDistance = (km * 10f).roundToInt() / 10f
                binding.textViewdistance.text= totalDistance.toString()
            }
        })
        roomViewmodel.sumofcaloriesburntofAllTracks.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.textViewcalorieburnt.text=it.toString()
            }
        })
        roomViewmodel.sumoftimeofAllTracks.observe(viewLifecycleOwner, Observer {
            it?.let {
                val time=it/1000/60
                binding.textViewduration.text=time.toString()
            }
        })
        val sharedPreferences = context?.getSharedPreferences("steps", Context.MODE_PRIVATE)
        val editor = sharedPreferences?.edit()
        var alltimesteps=sharedPreferences?.getInt("alltimesteps", 0)
        if(alltimesteps!!>999){
            alltimesteps/=1000
        }
        binding.textViewsteps.text="${alltimesteps}k"

    }

    @SuppressLint("RestrictedApi")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        if (menu is MenuBuilder) (menu as MenuBuilder).setOptionalIconsVisible(true)
        inflater.inflate(R.menu.profilefrag_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            R.id.logout -> {
                Firebase.auth.signOut()
                auth.signOut()
                mGoogleSignInClient.signOut()
                if (AccessToken.getCurrentAccessToken() != null) {
                    GraphRequest(
                        AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE,
                        GraphRequest.Callback {
                            AccessToken.setCurrentAccessToken(null)
                            LoginManager.getInstance().logOut()
                        }
                    ).executeAsync()
                }

                utilvalues.imageprofiledpuri.value=null
                utilvalues.nameofuser.value="--"
                utilvalues.ageofuser.value="--"
                utilvalues.weightofuser.value="--"
                utilvalues.heightofuser.value="--"
                utilvalues.bloodgrpofuser.value="--"
                utilvalues.genderofuser.value="--"
                val sharedPreferencess=activity?.getSharedPreferences("shared_preference", Context.MODE_PRIVATE)
                val editor=sharedPreferencess?.edit()
                editor?.putString("tempname", "--")
                editor?.apply()
                shouldloadbio=true
                //pop up wont work why cause we have removed login fragment from bacstack so it stays here lol
                //findNavController().popBackStack(R.id.loginFragment,false)
                findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToLoginFragment())
                return true
            }
            R.id.settingoffithunt ->{
                findNavController().navigate(R.id.settingFragment)
                return true
            }


            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }
}