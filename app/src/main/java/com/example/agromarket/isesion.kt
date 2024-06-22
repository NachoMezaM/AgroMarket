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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.*
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase


class isesion : AppCompatActivity() {
    lateinit var correotext: EditText
    lateinit var contratext: EditText
    private lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_isesion)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        correotext  = findViewById<EditText>(R.id.correoEditText)
        contratext = findViewById<EditText>(R.id.contraEditText)
        database = FirebaseDatabase.getInstance().reference.child("usuarios")
    }

    fun sendData(view: View) {
        //Obtener inicio
        val correo = correotext.text.toString().replace(".", ",")
        val contra = contratext.text.toString()

        //Dirigir path
        val database = Firebase.database
        val myRef = database.getReference("usuarios")

        //Revisar que campos esten llenos
        if (correo.isEmpty() || contra.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        //Revisar existencia de usuario
        val userRef = myRef.child(correo)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // El usuario con ese correo existe, comparamos la contraseña
                    val storedContra = dataSnapshot.child("contra").getValue(String::class.java)
                    if (storedContra == contra) {
                        //Si es comprador / vendedor redirige a su pagina respectiva
                        val userType = dataSnapshot.child("Tipo").getValue(String::class.java)
                        if (userType == "Comprador") {
                            startActivity(Intent(this@isesion, MainComprador::class.java))
                        } else if (userType == "Vendedor") {
                            startActivity(Intent(this@isesion, mainvendedor::class.java))
                        }
                        finish()
                    } else {
                        Toast.makeText(this@isesion, "Contraseña incorrecta", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@isesion, "Usuario no registrado", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@isesion, "Error en la consulta: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}