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
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class register : AppCompatActivity() {

    lateinit var correotext: EditText
    lateinit var contratext: EditText
    lateinit var nombretext: EditText
    lateinit var apellidotext: EditText
    lateinit var telefonotext: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        correotext = findViewById(R.id.correoEditText)
        contratext = findViewById(R.id.contraEditText)
        nombretext = findViewById(R.id.nombreEditText)
        apellidotext = findViewById(R.id.apellidosEditText)
        telefonotext = findViewById(R.id.telefonoEditText)

    }
    fun sendUser(view: View) {

        //Obtener inicio
        val correo = correotext.text.toString()
        val contra = contratext.text.toString()
        val nombre = nombretext.text.toString()
        val apellido = apellidotext.text.toString()
        val telefono = telefonotext.text.toString()
        val tipo = "Comprador"

        //Dirigir path
        val database = Firebase.database
        val myRef = database.getReference("usuarios")

        //Codificar aca
        val encodedEmail = encodeEmail(correo)

        // Validar que los campos no estén vacíos
        if (correo.isEmpty() || contra.isEmpty() || nombre.isEmpty() || apellido.isEmpty() || telefono.isEmpty()) {
            Toast.makeText(view.context, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        //Revisar existencia de usuario
        myRef.child(encodedEmail).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // El usuario ya existe
                    Toast.makeText(
                        view.context,
                        "El Correo ya está en uso",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    // El usuario no existe, crear el nuevo usuario
                    val userMap = mapOf(
                        "contra" to contra,
                        "Nombres" to nombre,
                        "Apellidos" to apellido,
                        "Telefono" to telefono,
                        "Tipo" to tipo
                    )
                    myRef.child(encodedEmail).setValue(userMap)
                    Toast.makeText(view.context, "Usuario creado exitosamente", Toast.LENGTH_SHORT)
                        .show()
                    val intent = Intent(this@register, MainActivity::class.java)
                    startActivity(intent)

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
    //Codificar correo pq fire no acepta .
    fun encodeEmail(email: String): String {
        return email.replace(".", ",")
    }
}