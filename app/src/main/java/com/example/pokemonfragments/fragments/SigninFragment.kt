package com.example.pokemonfragments.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.pokemonfragments.R
import com.example.pokemonfragments.databinding.FragmentSigninBinding
import com.example.pokemonfragments.model.Usuario
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SigninFragment : Fragment() {

    private var _binding: FragmentSigninBinding? = null
    private val binding get() = _binding!!
    private lateinit var authFirebase: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSigninBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        instancias()

        binding.botonRegistro.setOnClickListener {
            if (!binding.editCorreo.text.toString().isEmpty()
                && !binding.editNombre.text.toString().isEmpty()
                && !binding.editPass.text.toString().isEmpty()
                && !binding.editPass2.text.toString().isEmpty()
                && (binding.editPass.text.toString() == binding.editPass2.text.toString())
            ) {
                val perfil: String = binding.spinnerPerfil.selectedItem.toString()
                val radioSeleccionado: RadioButton =
                    binding.radioGroup.findViewById(binding.radioGroup.checkedRadioButtonId)
                val genero = radioSeleccionado.text.toString()

                val usuario = Usuario(
                    nombre = binding.editNombre.text.toString(),
                    correo = binding.editCorreo.text.toString(),
                    genero = genero,
                    perfil = perfil
                )

                authFirebase.createUserWithEmailAndPassword(
                    usuario.correo!!, binding.editPass.text.toString()
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        firebaseDatabase.reference.child("usuarios")
                            .child(authFirebase.currentUser!!.uid).setValue(usuario)

                        Snackbar.make(binding.root, "Usuario registrado con éxito", Snackbar.LENGTH_SHORT)
                            .setAction("Ir a login") {
                                findNavController().navigate(R.id.action_signinFragment_to_loginFragment)
                            }.show()
                    } else {
                        Snackbar.make(binding.root, "Fallo en el proceso", Snackbar.LENGTH_SHORT).show()
                    }
                }
            } else {
                Snackbar.make(binding.root, "Fallo en el proceso", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun instancias() {
        authFirebase = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance("https://luis-4c9db-default-rtdb.europe-west1.firebasedatabase.app/")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
