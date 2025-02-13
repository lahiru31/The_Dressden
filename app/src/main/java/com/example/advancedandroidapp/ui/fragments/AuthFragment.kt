package com.example.advancedandroidapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.advancedandroidapp.R
import com.example.advancedandroidapp.databinding.FragmentAuthBinding
import com.example.advancedandroidapp.ui.viewmodels.AuthViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AuthFragment : Fragment() {

    private var _binding: FragmentAuthBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAuthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        binding.apply {
            // Toggle between sign in and sign up
            buttonToggleMode.setOnClickListener {
                viewModel.toggleAuthMode()
            }

            // Sign in/up button
            buttonAuth.setOnClickListener {
                val email = textEmail.text.toString()
                val password = textPassword.text.toString()
                val confirmPassword = textConfirmPassword.text.toString()

                when {
                    email.isEmpty() -> {
                        textInputEmail.error = getString(R.string.error_field_required)
                    }
                    password.isEmpty() -> {
                        textInputPassword.error = getString(R.string.error_field_required)
                    }
                    viewModel.isSignUpMode.value && password != confirmPassword -> {
                        textInputConfirmPassword.error = getString(R.string.error_passwords_dont_match)
                    }
                    else -> {
                        if (viewModel.isSignUpMode.value) {
                            viewModel.signUp(email, password)
                        } else {
                            viewModel.signIn(email, password)
                        }
                    }
                }
            }

            // Reset password button
            buttonResetPassword.setOnClickListener {
                val email = textEmail.text.toString()
                if (email.isEmpty()) {
                    textInputEmail.error = getString(R.string.error_field_required)
                } else {
                    viewModel.resetPassword(email)
                }
            }

            // Google Sign In
            buttonGoogleSignIn.setOnClickListener {
                viewModel.signInWithGoogle()
            }
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Observe auth mode
                launch {
                    viewModel.isSignUpMode.collect { isSignUp ->
                        updateUIForAuthMode(isSignUp)
                    }
                }

                // Observe loading state
                launch {
                    viewModel.isLoading.collect { isLoading ->
                        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                        binding.buttonAuth.isEnabled = !isLoading
                        binding.buttonGoogleSignIn.isEnabled = !isLoading
                    }
                }

                // Observe auth state
                launch {
                    viewModel.authState.collect { state ->
                        when (state) {
                            is AuthViewModel.AuthState.Success -> {
                                findNavController().navigate(R.id.action_auth_to_home)
                            }
                            is AuthViewModel.AuthState.Error -> {
                                Snackbar.make(
                                    binding.root,
                                    state.message,
                                    Snackbar.LENGTH_LONG
                                ).show()
                            }
                            else -> {
                                // Handle other states if needed
                            }
                        }
                    }
                }
            }
        }
    }

    private fun updateUIForAuthMode(isSignUp: Boolean) {
        binding.apply {
            // Update title
            textTitle.text = getString(
                if (isSignUp) R.string.create_account else R.string.welcome_back
            )

            // Show/hide confirm password
            textInputConfirmPassword.visibility = if (isSignUp) View.VISIBLE else View.GONE

            // Update button texts
            buttonAuth.text = getString(
                if (isSignUp) R.string.sign_up else R.string.sign_in
            )
            buttonToggleMode.text = getString(
                if (isSignUp) R.string.already_have_account else R.string.need_account
            )

            // Show/hide reset password
            buttonResetPassword.visibility = if (isSignUp) View.GONE else View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
