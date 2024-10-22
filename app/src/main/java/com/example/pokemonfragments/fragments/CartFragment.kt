package com.example.pokemonfragments.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pokemonfragments.adapters.AdaptadorPokemon
import com.example.pokemonfragments.model.Pokemon
import com.example.pokemonfragments.databinding.FragmentCartBinding
import com.example.pokemonfragments.fragments.EmptyCartDialogFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CartFragment : Fragment(), EmptyCartDialogFragment.EmptyCartDialogListener {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    private lateinit var adaptadorPokemon: AdaptadorPokemon
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var uidCurrentUser: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        instancias()

        binding.recyclerCart.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerCart.adapter = adaptadorPokemon

        binding.btnEmptyCart.setOnClickListener {
            EmptyCartDialogFragment().show(parentFragmentManager, "EmptyCartDialog")
        }

        firebaseDatabase.reference.child("usuarios").child(uidCurrentUser).child("cart")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (_binding != null) {
                        adaptadorPokemon.listaPokemon.clear()
                        var totalWeight = 0
                        snapshot.children.forEach {
                            val pokemon = it.getValue(Pokemon::class.java)
                            if (pokemon != null) {
                                adaptadorPokemon.addPokemon(pokemon)
                                totalWeight += pokemon.weight ?: 0
                            }
                        }
                        // Actualizar el TextView con el peso total
                        binding.totalWeightTextView.text = "Peso Total: $totalWeight"
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun instancias() {
        firebaseDatabase = FirebaseDatabase.getInstance("https://luis-4c9db-default-rtdb.europe-west1.firebasedatabase.app/")
        uidCurrentUser = requireArguments().getString("uid")!!
        val navController = findNavController()
        adaptadorPokemon = AdaptadorPokemon(requireContext(), uidCurrentUser, firebaseDatabase, true, false, navController)
    }

    override fun onDialogPositiveClick(dialog: DialogFragment) {
        firebaseDatabase.reference.child("usuarios").child(uidCurrentUser).child("cart").removeValue()
        // Aquí no necesitas iniciar una nueva actividad ya que estás en un fragmento.
        // Solo actualiza la UI si es necesario.
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
