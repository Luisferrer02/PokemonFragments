package com.example.pokemonfragments.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.pokemonfragments.adapters.AdaptadorPokemon
import com.example.pokemonfragments.model.Pokemon
import com.example.pokemonfragments.databinding.FragmentPokemonListBinding
import com.google.firebase.database.FirebaseDatabase

class PokemonListFragment : Fragment() {

    private var _binding: FragmentPokemonListBinding? = null
    private val binding get() = _binding!!
    private lateinit var adaptadorPokemon: AdaptadorPokemon
    private lateinit var uidCurrentUser: String
    private lateinit var firebaseDatabase: FirebaseDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPokemonListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        uidCurrentUser = arguments?.getString("uid") ?: ""
        instancias()

        binding.recyclerModelos.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerModelos.adapter = adaptadorPokemon

        peticionJSON()
    }

    private fun peticionJSON() {
        // Crear la petición
        val url = "https://pokeapi.co/api/v2/pokemon/"
        val request = JsonObjectRequest(
            url,
            { response ->
                val jsonArray = response.getJSONArray("results")

                for (i in 0 until jsonArray.length()) {
                    val pokemonObject = jsonArray.getJSONObject(i)
                    val pokemonUrl = pokemonObject.getString("url")
                    obtenerDetallePokemon(pokemonUrl)
                }
            },
            { error ->
                // Manejar errores de la petición
                Log.e("API Request", "Error: ${error.message}")
                Toast.makeText(requireContext(), "Error al obtener los datos", Toast.LENGTH_SHORT).show()
            }
        )

        // Añadir la petición a la cola de solicitudes
        Volley.newRequestQueue(requireContext()).add(request)
    }

    private fun obtenerDetallePokemon(url: String) {
        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                val id = response.getInt("id")
                val name = response.getString("name")
                val height = response.getInt("height")
                val weight = response.getInt("weight")
                val spriteUrl = response.getJSONObject("sprites").getString("front_default")
                val pokemon = Pokemon(id, name, height, weight, spriteUrl)
                adaptadorPokemon.addPokemon(pokemon)
            },
            { error ->
                Log.e("API Request", "Error: ${error.message}")
            })

        Volley.newRequestQueue(requireContext()).add(request)
    }

    private fun instancias() {
      //  uidCurrentUser = requireArguments().getString("uid")!!
        firebaseDatabase = FirebaseDatabase.getInstance("https://luis-4c9db-default-rtdb.europe-west1.firebasedatabase.app/")
        val navController = findNavController()
        adaptadorPokemon = AdaptadorPokemon(requireContext(), uidCurrentUser, firebaseDatabase, false, false, navController)
    }

    fun onPokemonItemClick(view: View) {
        val recyclerView = binding.recyclerModelos
        val position = recyclerView.getChildAdapterPosition(view)
        val pokemon = adaptadorPokemon.getPokemon(position)

        //TODO navegacion a detalle
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
