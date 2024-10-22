package com.example.pokemonfragments.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.DialogFragment


class EmptyCartDialogFragment : DialogFragment() {

    interface EmptyCartDialogListener{
        fun onDialogPositiveClick(dialog: DialogFragment)
    }

    private lateinit var listener: EmptyCartDialogListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            // Verifica si el contexto implementa la interfaz de escucha
            listener = context as EmptyCartDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context debe implementar EmptyCartDialogListener")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle("Vaciar carrito")
        builder.setMessage("¿Estás seguro de que quieres vaciar el carrito?")

        builder.setPositiveButton("Si", DialogInterface.OnClickListener{_, _ ->
        listener.onDialogPositiveClick(this)
            Log.v("Dialogo", "Pulsado positivo")
        })

        builder.setNegativeButton("Cancelar", DialogInterface.OnClickListener{_, _ -> //_, _ parametros no necesarios
            Log.v("Dialogo", "Pulsado positivo")
        })



        return builder.create()
    }

}