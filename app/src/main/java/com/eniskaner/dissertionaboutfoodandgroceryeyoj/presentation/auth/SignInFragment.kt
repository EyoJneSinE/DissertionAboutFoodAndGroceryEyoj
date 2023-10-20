package com.eniskaner.dissertionaboutfoodandgroceryeyoj.presentation.auth

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.databinding.FragmentSignInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignInFragment : Fragment() {
    private lateinit var binding: FragmentSignInBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var viewModel: SignInViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val temViewModel : SignInViewModel by viewModels()
        viewModel = temViewModel
        auth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignInBinding.inflate(inflater, container, false)
        passwordFocusListener()
        binding.btnAction.setOnClickListener {
            signInClicked(it, binding.signInEmail.text.toString().trim(), binding.signInPassword.text.toString().trim())
        }
        return binding.root
    }

    fun signInClicked(view: View,email:String,password:String){
        val emailHelperText = binding.textFieldEmail.helperText
        val passwordHelperText = binding.textFieldPassword.helperText
        if(emailHelperText == null && passwordHelperText == null){
            viewModel.signIn(auth,view,email,password)
        }else{
            //showToast(requireContext(),R.string.required_email_and_password)
        }
    }

    private fun passwordFocusListener(){
        binding.signInPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                binding.textFieldPassword.helperText = validPassword()
            }
        })
    }

    private fun validPassword(): String? {
        val password = binding.signInPassword.text.toString()
        if(password.isEmpty()){
            return "Need Password"
        }
        return null
    }
}
