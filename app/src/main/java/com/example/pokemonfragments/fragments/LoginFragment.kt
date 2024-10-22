package com.example.pokemonfragments.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.pokemonfragments.MainActivity
import com.example.pokemonfragments.R
import com.example.pokemonfragments.databinding.FragmentLoginBinding
import com.example.pokemonfragments.model.Usuario
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var authFirebase: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authFirebase = FirebaseAuth.getInstance()

        // Check if there is a passed user data and fill in the fields
        val usuario = arguments?.getSerializable("usuario") as? Usuario
        usuario?.let {
            binding.editCorreo.setText(it.correo)
            binding.editPass.setText(it.pass)
        }

        binding.botonLogin.setOnClickListener {
            if (binding.editCorreo.text.toString().isNotEmpty() &&
                binding.editPass.text.toString().isNotEmpty()
            ) {
                authFirebase.signInWithEmailAndPassword(
                    binding.editCorreo.text.toString(),
                    binding.editPass.text.toString()
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val uid = authFirebase.currentUser?.uid ?: ""
                        (activity as MainActivity).setUidCurrentUser(uid)
                        val bundle = Bundle().apply {
                            putString("uid", uid)
                        }
                        findNavController().navigate(R.id.action_loginFragment_to_pokemonListFragment, bundle)
                    } else {
                        Snackbar.make(binding.root, "Fallo en los datos", Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.botonSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signinFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
