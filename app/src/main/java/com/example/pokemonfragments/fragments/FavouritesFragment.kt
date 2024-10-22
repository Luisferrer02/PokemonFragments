package com.example.pokemonfragments.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pokemonfragments.adapters.AdaptadorPokemon
import com.example.pokemonfragments.model.Pokemon
import com.example.pokemonfragments.databinding.FragmentFavouritesBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FavouritesFragment : Fragment() {

    private var _binding: FragmentFavouritesBinding? = null
    private val binding get() = _binding!!
    private lateinit var adaptadorPokemon: AdaptadorPokemon
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var uidCurrentUser: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavouritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        instancias()

        binding.recyclerFavorites.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerFavorites.adapter = adaptadorPokemon

        firebaseDatabase.reference.child("usuarios").child(uidCurrentUser).child("favorites")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    adaptadorPokemon.listaPokemon.clear()
                    snapshot.children.forEach {
                        val pokemon = it.getValue(Pokemon::class.java)
                        if (pokemon != null) {
                            adaptadorPokemon.addPokemon(pokemon)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun instancias() {
        firebaseDatabase = FirebaseDatabase.getInstance("https://luis-4c9db-default-rtdb.europe-west1.firebasedatabase.app/")
        uidCurrentUser = requireArguments().getString("uid")!!
        val navController = findNavController() // Obtener el NavController aquí
        adaptadorPokemon = AdaptadorPokemon(requireContext(), uidCurrentUser, firebaseDatabase, false, true, navController)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
