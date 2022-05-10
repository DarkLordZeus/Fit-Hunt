package com.example.fithunt.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.fithunt.R
import com.example.fithunt.Util.utilvalues
import com.example.fithunt.databinding.FragmentLoginBinding
import com.facebook.*
import com.facebook.FacebookSdk.getApplicationContext
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.AndroidEntryPoint
import io.ak1.BubbleTabBar
import java.util.*


@AndroidEntryPoint
class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding?=null
    private val binding get()=_binding!!
    lateinit var firebaseauth: FirebaseAuth
    lateinit var firebaseUser: FirebaseUser
    var builder: AlertDialog.Builder? = null
    var progressDialog: AlertDialog? = null
    private val RC_SIGN_IN=101
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var mCallbackManager: CallbackManager
    val TAG=""
    lateinit var accessTokenTracker:AccessTokenTracker

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        activity?.findViewById<BubbleTabBar>(R.id.bubbleTabBar)?.visibility=View.GONE
        activity?.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)?.visibility=View.GONE
        _binding= FragmentLoginBinding.inflate(inflater,container,false)
        progressDialog=getDialogProgressBar()?.create()
        firebaseauth= FirebaseAuth.getInstance()
        FacebookSdk.sdkInitialize(getApplicationContext());
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

        mCallbackManager= CallbackManager.Factory.create()
        return binding.root
    }
    fun onClickgoogle(){
        progressDialog?.show()
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //for fb
        mCallbackManager.onActivityResult(requestCode, resultCode, data)
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!

                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(requireContext(), "improper"+e.message, Toast.LENGTH_SHORT).show()
                progressDialog?.dismiss()
            }
        }
    }
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseauth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    progressDialog?.dismiss()
                    val isNewUser = task.result.additionalUserInfo!!.isNewUser
                        val user = firebaseauth.currentUser
                    updateUI(user, isNewUser)
                } else {
                    // If sign in fails, display a message to the user.
                        progressDialog?.dismiss()
                        Toast.makeText(requireContext(), "fail"+task.exception?.localizedMessage, Toast.LENGTH_SHORT).show()

                }
            }
    }

    private fun updateUI(user: FirebaseUser?, isNewUser: Boolean) {
        if(isNewUser)
        findNavController().navigate(R.id.biometricFragment)
        else
            findNavController().navigate(R.id.welcome_fragment)
    }

    fun getDialogProgressBar(): AlertDialog.Builder? {
            builder = AlertDialog.Builder(requireContext())
            builder!!.setTitle("Logging in...")
            val progressBar = ProgressBar(requireContext())
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                400)
            progressBar.layoutParams = lp
            builder!!.setView(progressBar)

        return builder
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonlogin.setOnClickListener {
            performlogin()
        }
        binding.registeruser.setOnClickListener {
            val action=LoginFragmentDirections.actionLoginFragmentToSignupfragment()
            findNavController().navigate(action)
        }
        binding.googlesignin.setOnClickListener {
            onClickgoogle()
        }
        binding.fbsignin.setOnClickListener {
            loginfb()
        }

    }

    private fun loginfb() {
        LoginManager.getInstance().logInWithReadPermissions(this, listOf("public_profile"))

        LoginManager.getInstance().registerCallback(mCallbackManager, object : FacebookCallback<LoginResult>{
            override fun onCancel() {
                Log.d(TAG, "facebook:onCancel")
                Toast.makeText(requireContext(), "Login Cancelled", Toast.LENGTH_SHORT).show()
            }

            override fun onError(error: FacebookException) {
                Log.d(TAG, "facebook:onError", error)
                Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
            }

            override fun onSuccess(result: LoginResult) {
                progressDialog?.show()
                handleFacebookAccessToken(result.accessToken)
            }

        })
    }

    private fun handleFacebookAccessToken(token: AccessToken) {

        val credential = FacebookAuthProvider.getCredential(token.token)
        firebaseauth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val isNewUser = task.result.additionalUserInfo!!.isNewUser
                    val user = firebaseauth.currentUser
                    progressDialog?.dismiss()
                    updateUI(user,isNewUser)
                } else {
                    // If sign in fails, display a message to the user.
                    progressDialog?.dismiss()
                        Toast.makeText(requireContext(), ""+task.exception?.localizedMessage,
                        Toast.LENGTH_SHORT).show()
                }
            }
    }


    private fun performlogin() {
        val email=binding.editTextemail.text.toString()
        val password=binding.editTextpassword.text.toString()
        if(!utilvalues.emailRegex.matcher(email).matches()){
            binding.editTextemail.requestFocus()
            binding.editTextemail.error = "Enter Correct Email"
        }
        else if(password.isNullOrEmpty()||password.length<6)
        {
            binding.editTextpassword.requestFocus()
            binding.editTextpassword.error = "Enter Proper password"
        }
        else
        {

            progressDialog?.show()
            firebaseauth.signInWithEmailAndPassword(email,password).addOnCompleteListener{ task->
                if(task.isSuccessful){
                    progressDialog?.dismiss()
                    val isNewUser = task.result.additionalUserInfo!!.isNewUser
                    val user = firebaseauth.currentUser
                    updateUI(user, isNewUser)
                    Toast.makeText(requireContext(), "Login Successfull", Toast.LENGTH_SHORT).show()
                }
                else{
                    progressDialog?.dismiss()
                    Toast.makeText(requireContext(), ""+task.exception?.localizedMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    override fun onStart() {
        super.onStart()

        // Checking if the user is signed in (non-null) and update UI accordingly.
        val currentUser: FirebaseUser? = firebaseauth.getCurrentUser()
        if (currentUser != null) {

            Toast.makeText(requireContext(),
                "Currently Logged in: " + currentUser.email,
                Toast.LENGTH_LONG).show()
            val ref= FirebaseDatabase.getInstance().getReference("Bio-metrics")
            ref.child(FirebaseAuth.getInstance().currentUser?.uid!!.toString()).get().addOnSuccessListener{
                if(it.exists())
                {
                    utilvalues.nameofuser.value= it.child("Name").value as String?}}
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToWelcomeFragment())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }
}