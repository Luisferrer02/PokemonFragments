package com.example.pokemonfragments

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.DialogFragment
import com.example.pokemonfragments.databinding.ActivityMainBinding
import com.example.pokemonfragments.fragments.EmptyCartDialogFragment
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity(), EmptyCartDialogFragment.EmptyCartDialogListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var uidCurrentUser :String
    private lateinit var firebaseDatabase : FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseDatabase = FirebaseDatabase.getInstance("https://luis-4c9db-default-rtdb.europe-west1.firebasedatabase.app/")

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return when (item.itemId) {
            R.id.menu_view_cart -> {
                val bundle = Bundle().apply {
                    putString("uid", uidCurrentUser)
                }
                navController.navigate(R.id.cartFragment, bundle)
                true
            }
            R.id.menu_view_favorites -> {
                val bundle = Bundle().apply {
                    putString("uid", uidCurrentUser)
                }
                navController.navigate(R.id.favouritesFragment, bundle)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun setUidCurrentUser(uid: String) {
        uidCurrentUser = uid
    }

    override fun onDialogPositiveClick(dialog: DialogFragment) {
        firebaseDatabase.reference.child("usuarios").child(uidCurrentUser).child("cart").removeValue()

    }
}
