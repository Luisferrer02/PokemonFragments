package com.example.pokemonfragments.fragments

import com.bumptech.glide.Glide
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.pokemonfragments.model.Pokemon
import com.example.pokemonfragments.R
import com.example.pokemonfragments.databinding.FragmentPokemonDetailBinding
import com.google.firebase.database.FirebaseDatabase

class PokemonDetailFragment : Fragment() {

    private var _binding: FragmentPokemonDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var uidCurrentUser: String
    private lateinit var pokemon: Pokemon

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPokemonDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obtener los datos del Pokémon y el UID del usuario
        pokemon = arguments?.getSerializable("pokemon") as Pokemon
        uidCurrentUser = arguments?.getString("uid") ?: ""
        firebaseDatabase = FirebaseDatabase.getInstance("https://luis-4c9db-default-rtdb.europe-west1.firebasedatabase.app/")

        // Mostrar los detalles del Pokémon en las vistas usando View Binding
        binding.textViewPokemonName.text = pokemon.name
        binding.textViewPokemonId.text = getString(R.string.pokemon_id, pokemon.id)
        binding.textViewPokemonHeight.text = getString(R.string.pokemon_height, pokemon.height)
        binding.textViewPokemonWeight.text = getString(R.string.pokemon_weight, pokemon.weight)
        Glide.with(this)
            .load(pokemon.spriteUrl)
            .into(binding.imageViewPokemon)

        binding.carritoBtn.setOnClickListener {
            addToCart(pokemon)
        }
        binding.favBtn.setOnClickListener{
            addToFavorites(pokemon)
        }
    }

    private fun addToCart(pokemon: Pokemon) {
        val reference = firebaseDatabase.reference.child("usuarios").child(uidCurrentUser).child("cart")
        reference.child(pokemon.id.toString()).setValue(pokemon)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Pokemon añadido al carrito", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error al añadir pokemon al carrito", Toast.LENGTH_SHORT).show()
            }
    }

    private fun addToFavorites(pokemon: Pokemon) {
        val reference = firebaseDatabase.reference.child("usuarios").child(uidCurrentUser).child("favorites")
        reference.child(pokemon.id.toString()).setValue(pokemon)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Pokemon añadido a favoritos", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error al añadir pokemon a favoritos", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
