package com.unach.agromarket

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.unach.agromarket.R
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class opciones : AppCompatActivity() {
    private lateinit var correo: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_opciones)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        try {
            correo = intent.getStringExtra("correo") ?: throw Exception("Correo no proporcionado")
        } catch (e: Exception) {
            Toast.makeText(this, "Error al recibir el correo: ${e.message}", Toast.LENGTH_LONG)
                .show()
            e.printStackTrace()
        }
        val volver: Button = findViewById(R.id.volverop)
        val actualizar: Button = findViewById(R.id.actualizar)

        actualizar.setOnClickListener(){
            guardarCambios()
        }
        volver.setOnClickListener(){
            finish()
        }

        //leer datos de la DB
        val database = Firebase.database.reference
        val userRef = database.child("usuarios").child(correo)
        val TipoU: Spinner = findViewById(R.id.TipoUser)
        val tiposUsuario = arrayOf("Comprador", "Vendedor")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, tiposUsuario)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        TipoU.adapter = adapter

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    findViewById<EditText>(R.id.NombreUser).setText(dataSnapshot.child("Nombres").getValue(String::class.java))
                    findViewById<EditText>(R.id.ApellidoUser).setText(dataSnapshot.child("Apellidos").getValue(String::class.java))
                    findViewById<EditText>(R.id.CorreoUser).setText(correo.replace(",","."))
                    findViewById<EditText>(R.id.TelefonoUser).setText(dataSnapshot.child("Telefono").getValue(String::class.java))
                    findViewById<EditText>(R.id.ContraUser).setText(dataSnapshot.child("contra").getValue(String::class.java))
                    val tipoUsuario = dataSnapshot.child("Tipo").getValue(String::class.java)
                    val spinner = findViewById<Spinner>(R.id.TipoUser)
                    val adapter = spinner.adapter as ArrayAdapter<String>
                    tipoUsuario?.let {
                        val position = adapter.getPosition(it)
                        spinner.setSelection(position)
                    }
                    if (dataSnapshot.child("Tipo").getValue(String::class.java) == "Vendedor") {
                        findViewById<EditText>(R.id.NombreET).apply {
                            setText(dataSnapshot.child("NombreTienda").getValue(String::class.java))
                        }
                    }
                } else {
                    Toast.makeText(this@opciones, "Usuario no registrado", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@opciones, "Usuario Error", Toast.LENGTH_SHORT).show()
            }
        })

    }
    // gurdar cambios
     fun guardarCambios() {
        val nombre = findViewById<EditText>(R.id.NombreUser).text.toString()
        val apellido = findViewById<EditText>(R.id.ApellidoUser).text.toString()
        val correo = findViewById<EditText>(R.id.CorreoUser).text.toString()
        val telefono = findViewById<EditText>(R.id.TelefonoUser).text.toString()
        val contrasena = findViewById<EditText>(R.id.ContraUser).text.toString()
        val tipoUsuario = findViewById<Spinner>(R.id.TipoUser).selectedItem.toString()
        val usuarioInfo: Map<String, Any>
        if (tipoUsuario == "Vendedor"){
            val nombretienda = findViewById<EditText>(R.id.NombreET).text.toString()
                usuarioInfo = mapOf(
                "Nombres" to nombre,
                "Apellidos" to apellido,
                "Telefono" to telefono,
                "contra" to contrasena,
                "Tipo" to tipoUsuario,
                "NombreTienda" to nombretienda
            )}
        else{
                 usuarioInfo = mapOf(
                    "Nombres" to nombre,
                    "Apellidos" to apellido,
                    "Telefono" to telefono,
                    "contra" to contrasena,
                    "Tipo" to tipoUsuario
                )}



        val dbRef = Firebase.database.reference.child("usuarios").child(correo.replace(".", ","))

        dbRef.updateChildren(usuarioInfo)
            .addOnSuccessListener {
                Toast.makeText(this, "Información actualizada correctamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al actualizar información: ${e.message}", Toast.LENGTH_LONG).show()
            }


    }
}