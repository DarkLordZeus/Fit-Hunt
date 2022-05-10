package com.example.fithunt.fragments

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.fithunt.R
import com.example.fithunt.Util.utilvalues
import com.example.fithunt.databinding.FragmentNewbiometricsBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import io.ak1.BubbleTabBar
import java.util.*


class newbiometrics : Fragment() {
    private var _binding: FragmentNewbiometricsBinding?=null
    private val binding get()=_binding!!
    val GALLERY_REQUEST=101
    var mProfileUri: Uri? = null
    lateinit var storageReference:StorageReference
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding= FragmentNewbiometricsBinding.inflate(inflater,container,false)
        activity?.findViewById<BubbleTabBar>(R.id.bubbleTabBar)?.visibility=View.GONE
        activity?.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)?.visibility=View.GONE
        binding.name.setText(utilvalues.nameofuser.value)
        binding.age.setText(utilvalues.ageofuser.value)
        binding.height.setText(utilvalues.heightofuser.value)
        binding.weight.setText(utilvalues.weightofuser.value)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPreferencess=activity?.getSharedPreferences("shared_preference", Context.MODE_PRIVATE)
        val firebasedb: FirebaseDatabase = FirebaseDatabase.getInstance()
        val rootref: DatabaseReference =firebasedb.reference.child("Bio-metrics").child(FirebaseAuth.getInstance().currentUser?.uid!!.toString())
        val HashMapdb: HashMap<String, String> = HashMap()
        val editor=sharedPreferencess?.edit()
        storageReference=FirebaseStorage.getInstance().reference
        val startForProfileImageResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                val resultCode = result.resultCode
                val data = result.data

                when (resultCode) {
                    Activity.RESULT_OK -> {
                        //Image Uri will not be null for RESULT_OK
                        val fileUri = data?.data!!
                        mProfileUri = fileUri
                        binding.imageViewdp.setImageURI(fileUri)
                    }
                    ImagePicker.RESULT_ERROR -> {
                        Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        utilvalues.imageprofiledpuri.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if(it!=null)
            {
                Glide.with(requireContext()).load(it).into(binding.imageViewdp)
            }
            else{
                if(sharedPreferencess?.getString("gender","--").equals("Male",true))
                    Glide.with(requireContext()).load(R.drawable.malevector).into(binding.imageViewdp)
                else
                    Glide.with(requireContext()).load(R.drawable.womenvector).into(binding.imageViewdp)
            }
        })

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
        binding.updatefab.setOnClickListener {
            if(!binding.name.text.isNullOrBlank() && !binding.age.text.isNullOrBlank() && !binding.age.text.isNullOrBlank() && !binding.height.text.isNullOrBlank()) {
                HashMapdb.apply {
                    put("Name",binding.name.text.toString())
                    put("age",binding.age.text.toString())
                    put("height",binding.height.text.toString().toFloat().toString())
                    put("weight",binding.weight.text.toString().toFloat().toString())
                    put("bloodgrp",bloodgrp)
                    put("gender",gender)
                }
                utilvalues.nameofuser.value= binding.name.text.toString()
                utilvalues.ageofuser.value=binding.age.text.toString()
                utilvalues.weightofuser.value=binding.weight.text.toString().toFloat().toString()
                utilvalues.heightofuser.value=binding.height.text.toString().toFloat().toString()
                utilvalues.bloodgrpofuser.value=bloodgrp
                utilvalues.genderofuser.value=gender

                rootref.setValue(HashMapdb).addOnSuccessListener(
                    OnSuccessListener<Any?> {
                        // Local temp file has been created
                        //Toast.makeText(requireContext(), "success", Toast.LENGTH_SHORT).show()
                    })
                    .addOnFailureListener(OnFailureListener { e ->
                        print(e.message)
                    })
                if(mProfileUri!=null) {
                    uploadimagetofirebase(mProfileUri!!)
                    utilvalues.imageprofiledpuri.value=mProfileUri
                }
                editor?.apply()
                findNavController().popBackStack()
            }
            else {
                Toast.makeText(requireContext(), "Please fill up all fields", Toast.LENGTH_SHORT)
                    .show()
            }


        }
        binding.selectimagefromgallery.setOnClickListener {
            ImagePicker.with(this)
                .compress(1024)         //Final image size will be less than 1 MB(Optional)
                .cropSquare()
                .galleryMimeTypes(  //Exclude gif images
                    mimeTypes = arrayOf(
                        "image/png",
                        "image/jpg",
                        "image/jpeg"
                    )
                )
                .maxResultSize(1080, 1080)  //Final image resolution will be less than 1080 x 1080(Optional)
                .createIntent { intent ->
                    startForProfileImageResult.launch(intent)
                }
        }

    }

    private fun uploadimagetofirebase(mProfileUri: Uri) {
        val fileref:StorageReference = storageReference.child("${FirebaseAuth.getInstance().currentUser?.uid}/profilepic.jpg")
        fileref.putFile(mProfileUri).addOnSuccessListener(OnSuccessListener<Any?> {
            // Local temp file has been created

        })
            .addOnFailureListener(OnFailureListener { e ->
                print(e.message)
            })

    }



    override fun onDestroy() {
        super.onDestroy()

        _binding=null
    }
}