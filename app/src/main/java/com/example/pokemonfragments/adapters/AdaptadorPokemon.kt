package com.example.pokemonfragments.adapters

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pokemonfragments.model.Pokemon
import com.example.pokemonfragments.R
import com.google.firebase.database.FirebaseDatabase

class AdaptadorPokemon(
    var context: Context,
    var uidCurrentUser: String,
    var firebaseDatabase: FirebaseDatabase,
    var isCartView: Boolean,
    var isFavView: Boolean,
    var navController: NavController
) : RecyclerView.Adapter<AdaptadorPokemon.MyHolder>() {

    val listaPokemon: MutableList<Pokemon> = mutableListOf()

    class MyHolder(item: View) : RecyclerView.ViewHolder(item) {
        var imagen: ImageView = item.findViewById(R.id.imagenFila)
        var titulo: TextView = item.findViewById(R.id.tituloFila)
        var btnAddToFavorites: Button = item.findViewById(R.id.btnAddToFavorites)
        var btnAddToCart: Button = item.findViewById(R.id.btnAddToCart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val vista: View = LayoutInflater.from(context).inflate(R.layout.item_pokemon, parent, false)
        return MyHolder(vista)
    }

    override fun getItemCount(): Int {
        return listaPokemon.size
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val currentPokemon = listaPokemon[position]
        holder.titulo.text = currentPokemon.name
        Glide.with(context).load(currentPokemon.spriteUrl).placeholder(R.drawable.placeholder).into(holder.imagen)

        // Controlar la visibilidad de los botones según las vistas
        holder.btnAddToCart.visibility = if (isCartView) View.GONE else View.VISIBLE
        holder.btnAddToFavorites.visibility = if (isFavView) View.GONE else View.VISIBLE

        // Set onClickListeners para añadir a favoritos y al carrito
        holder.btnAddToFavorites.setOnClickListener {
            addToFavorites(currentPokemon)
        }

        holder.btnAddToCart.setOnClickListener {
            addToCart(currentPokemon)
        }

        // Navegar al detalle del Pokémon al hacer clic en la imagen
        holder.imagen.setOnClickListener {
            val bundle = Bundle().apply {
                putSerializable("pokemon", currentPokemon)
                putString("uid", uidCurrentUser)
            }
            if (isCartView) Toast.makeText(context, "Esta opción no está disponible en la vista del carrito", Toast.LENGTH_SHORT).show()//navController.navigate(R.id.action_cartFragment_to_pokemonDetailFragment, bundle)
            else if (isFavView) navController.navigate(R.id.action_favouritesFragment_to_pokemonDetailFragment, bundle)
            else navController.navigate(R.id.action_pokemonListFragment_to_pokemonDetailFragment, bundle)
        }
    }

    fun getPokemon(position: Int): Pokemon {
        return listaPokemon[position]
    }

    private fun sortPokemonListById() {
        listaPokemon.sortBy { it.id }
    }

    fun addPokemon(pokemon: Pokemon) {
        listaPokemon.add(pokemon)
        sortPokemonListById()
        notifyItemInserted(listaPokemon.size - 1)
    }

    private fun addToFavorites(pokemon: Pokemon) {
        val reference = firebaseDatabase.reference.child("usuarios").child(uidCurrentUser).child("favorites")
        reference.child(pokemon.id.toString()).setValue(pokemon)
            .addOnSuccessListener {
                Log.d("AddToFavorites", "Pokemon añadido a favoritos: ${pokemon.name}")
            }
            .addOnFailureListener {
                Log.e("AddToFavorites", "Error añadiendo a favoritos", it)
            }
    }

    private fun addToCart(pokemon: Pokemon) {
        val reference = firebaseDatabase.reference.child("usuarios").child(uidCurrentUser).child("cart")
        val uniqueId = reference.push().key ?: pokemon.id.toString() // Usar push().key para un ID único
        reference.child(uniqueId).setValue(pokemon)
            .addOnSuccessListener {
                Log.d("AddToCart", "Pokemon añadido al carrito: ${pokemon.name}")
            }
            .addOnFailureListener {
                Log.e("AddToCart", "Error añadiendo al carrito", it)
            }
    }
}
