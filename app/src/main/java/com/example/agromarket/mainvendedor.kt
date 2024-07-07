package com.example.agromarket

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class mainvendedor : AppCompatActivity() {
    private lateinit var correo: String
    private lateinit var database: DatabaseReference
    private lateinit var nombreTiendaTV: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_mainvendedor)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val agregar: Button = findViewById(R.id.agregar)
        nombreTiendaTV = findViewById(R.id.nombretienda)
        database = FirebaseDatabase.getInstance().getReference("usuarios")
    try {
        correo = intent.getStringExtra("correo") ?: throw Exception("Correo no proporcionado")
        buscarnombre(correo)

    } catch (e: Exception){
        Toast.makeText(this, "Error al recibir el correo: ${e.message}", Toast.LENGTH_LONG).show()
        e.printStackTrace()
    }
        agregar.setOnClickListener(){
            var intent2 = Intent(this@mainvendedor, agregarproducto::class.java)
            intent2.putExtra("correo", correo)
            startActivity(intent2)
        }
    }

    private fun buscarnombre(correo: String){
        val userRef = database.child(correo)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val nombreTienda = dataSnapshot.child("NombreTienda").getValue(String::class.java)
                    nombreTienda?.let {
                        Toast.makeText(this@mainvendedor, "Bienvenido: $it", Toast.LENGTH_LONG).show()
                        nombreTiendaTV.text = nombreTienda
                    } ?: run {
                        Toast.makeText(this@mainvendedor, "NombreTienda no encontrado", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    println("Usuario no encontrado")
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                println("Error en la consulta: ${databaseError.message}")
            }
        })

    }

    }
