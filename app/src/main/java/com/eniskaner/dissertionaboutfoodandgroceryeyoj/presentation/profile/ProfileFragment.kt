package com.eniskaner.dissertionaboutfoodandgroceryeyoj.presentation.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.R
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.databinding.FragmentProfileBinding
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.utils.launchAndRepeatWithViewLifecycle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth : FirebaseAuth
    var user: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        user = auth.currentUser?.email
        updateVisibilityWith(user)
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnLogin.setOnClickListener { openAuthNavigation() }
        binding.btnLogout.setOnClickListener {
            onLogout(it)
        }
        launchAndRepeatWithViewLifecycle {
            launch { updateUIWithLoggedInUser() }
        }
    }

    private  fun updateUIWithLoggedInUser() {
        user = auth.currentUser?.email
        user?.let {
            binding.progressUser.isVisible = true
            updateVisibilityWith(it)
            it?.let {
                val fullName = it
                binding.txtFullName.text = fullName
                binding.txtAvatarLabel.text = fullName.take(1)
                binding.txtPhone.text = getString(R.string.phone_number)
                binding.txtAddress.text = getString(R.string.user_adress)
            }
            binding.progressUser.isVisible = false
        }
    }

    private fun onLogout(view: View) {
        FirebaseAuth.getInstance().signOut()
        user = null
        binding.btnLogin.isVisible = user != null
        binding.cardviewProfile.isVisible = user == null
        binding.btnLogout.isVisible = user == null
        val action = ProfileFragmentDirections.actionNavigationProfileToFragmentAuth()
        Navigation.findNavController(view).navigate(action)
    }

    private fun updateVisibilityWith(user: String?) {
        binding.btnLogin.isVisible = user == null
        binding.cardviewProfile.isVisible = user != null
        binding.btnLogout.isVisible = user != null
    }

    private fun openAuthNavigation() {
        val direction = ProfileFragmentDirections.actionNavigationProfileToFragmentAuth()
        requireActivity().findNavController(R.id.fragmentContainerView).navigate(direction)
    }
}
