package com.eniskaner.dissertionaboutfoodandgroceryeyoj.presentation.auth

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.databinding.FragmentSignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private lateinit var binding: FragmentSignUpBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var viewModel: SignUpViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val temViewModel : SignUpViewModel by viewModels()
        viewModel = temViewModel
        auth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        confirmPasswordFocusListener()
        binding.btnAction.setOnClickListener {
            signUpClicked(it, binding.signUpEmail.text.toString().trim(), binding.signUpPassword.text.toString().trim())
        }
        return binding.root
    }

    fun signUpClicked(view: View,email:String,password:String){
        val emailHelperText = binding.textFieldEmail.helperText
        val passwordHelperText = binding.textFieldPassword.helperText
        val confirmPasswordHelperText = binding.textFieldConfirmPassword.helperText
        if(emailHelperText == null && passwordHelperText == null && confirmPasswordHelperText == null){
            viewModel.signUp(auth,view,email,password)
        }else{
            //showToast(requireContext(),R.string.lutfen_eposta_sifre_kontrol_ediniz)
        }
    }
    private fun confirmPasswordFocusListener(){
        binding.signUpConfirmPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                val password = binding.signUpPassword.text.toString()
                val confirmPassword = binding.signUpConfirmPassword.text.toString()
                if(password != confirmPassword){
                    binding.textFieldConfirmPassword.error = "Passwords not the same"
                }else{
                    binding.textFieldConfirmPassword.error = null
                }
            }
        })
    }
}
