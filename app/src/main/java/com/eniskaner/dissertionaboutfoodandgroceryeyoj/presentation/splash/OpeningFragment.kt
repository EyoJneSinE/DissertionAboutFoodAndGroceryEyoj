package com.eniskaner.dissertionaboutfoodandgroceryeyoj.presentation.splash

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.R
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.databinding.FragmentOpenningBinding
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.utils.launchAndRepeatWithViewLifecycle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class OpeningFragment : Fragment() {

    private var _binding: FragmentOpenningBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOpenningBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        launchAndRepeatWithViewLifecycle {
            launch {
            delay(5000)
            authControl(auth, view)
            }
        }
    }


    private fun authControl(auth: FirebaseAuth, view:View){
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val action = OpeningFragmentDirections.actionOpeningFragmentToNavigationHome()
            Navigation.findNavController(view).navigate(action)
        } else {
            val action = OpeningFragmentDirections.actionOpeningFragmentToFragmentAuth()
            Navigation.findNavController(view).navigate(action)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
