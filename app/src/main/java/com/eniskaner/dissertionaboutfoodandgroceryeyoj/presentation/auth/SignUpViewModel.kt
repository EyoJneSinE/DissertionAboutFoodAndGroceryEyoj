package com.eniskaner.dissertionaboutfoodandgroceryeyoj.presentation.auth

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import timber.log.Timber

class SignUpViewModel: ViewModel() {

    fun signUp(auth: FirebaseAuth, view: View, email:String, password:String){
        auth.createUserWithEmailAndPassword(email,password)
            .addOnSuccessListener {
                val action = SignUpFragmentDirections.actionFragmentSignupToSignUpSplashFragment()
                Navigation.findNavController(view).navigate(action)
            }
            .addOnFailureListener { exception ->
                Timber.tag("FirebaseAuth").e("Error: %s", exception.message)
            }
    }

    fun goToSignIn(view: View){
        val action = SignUpFragmentDirections.actionFragmentSignupToFragmentSignin()
        Navigation.findNavController(view).navigate(action)
    }
}
