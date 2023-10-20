package com.eniskaner.dissertionaboutfoodandgroceryeyoj.presentation.auth

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import timber.log.Timber

class SignInViewModel: ViewModel() {

    fun signIn(auth: FirebaseAuth, view: View, email:String, password:String){
        auth.signInWithEmailAndPassword(email,password)
            .addOnSuccessListener {
                val action = SignInFragmentDirections.actionFragmentSigninToNavigationHome()
                Navigation.findNavController(view).navigate(action)
            }
            .addOnFailureListener { exception ->
                Timber.tag("FirebaseAuth").e("Error: %s", exception.message)
            }
    }

    fun goToSignUp(view: View){
        val action = AuthFragmentDirections.actionFragmentAuthToFragmentSignup()
        Navigation.findNavController(view).navigate(action)
    }
}
