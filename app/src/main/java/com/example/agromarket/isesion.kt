package com.example.agromarket

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.*
import com.google.firebase.ktx.Firebase

class isesion : AppCompatActivity() {
    lateinit var usuariotext: EditText
    lateinit var contratext: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_isesion)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        usuariotext = findViewById<EditText>(R.id.usuarioEditText)
        contratext = findViewById<EditText>(R.id.contraEditText)
    }

    fun sendData(view: View) {
        //Obtener inicio
        val usuario = usuariotext.text.toString()
        val contra = contratext.text.toString()
        //Dirigir path
        val database = Firebase.database
        val myRef = database.getReference("usuarios")

        //Revisar existencia de usuario
        myRef.child(usuario).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // El usuario ya existe
                    Toast.makeText(
                        view.context,
                        "El nombre de usuario ya está en uso",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    // El usuario no existe, crear el nuevo usuario
                    val userMap = mapOf("contra" to contra)
                    myRef.child(usuario).setValue(userMap)
                    Toast.makeText(view.context, "Usuario creado exitosamente", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Error en la lectura de la base de datos
                Toast.makeText(
                    view.context,
                    "Error en la base de datos: ${databaseError.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}