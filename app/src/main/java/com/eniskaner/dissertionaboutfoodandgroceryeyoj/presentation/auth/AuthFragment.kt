package com.eniskaner.dissertionaboutfoodandgroceryeyoj.presentation.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.databinding.FragmentAuthBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthFragment : Fragment() {
    private lateinit var binding: FragmentAuthBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAuthBinding.inflate(inflater, container, false)
        binding.btnLogin.setOnClickListener {
            Navigation.findNavController(it).navigate(AuthFragmentDirections.actionFragmentAuthToFragmentSignin())
        }
        binding.btnRegister.setOnClickListener {
            Navigation.findNavController(it).navigate(AuthFragmentDirections.actionFragmentAuthToFragmentSignup())
        }
        return binding.root
    }
}
