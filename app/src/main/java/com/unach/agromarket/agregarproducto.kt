package com.unach.agromarket

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.unach.agromarket.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class agregarproducto : AppCompatActivity() {
    private lateinit var subir: Button
    private lateinit var volver: Button
    private lateinit var enviar: Button
    private lateinit var correo: String

    private lateinit var imagenET: ImageView
    private lateinit var nombreET :EditText
    private lateinit var descET: EditText
    private lateinit var precioET: EditText

    private lateinit var storageRef: StorageReference
    private lateinit var imagenUri: Uri


    companion object{
        val IMAGE_REQUEST_CODE = 100
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_agregarproducto)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        subir = findViewById(R.id.subirimg)
        volver = findViewById(R.id.volver)
        enviar = findViewById(R.id.enviarprod)

        imagenET = findViewById(R.id.img)
        nombreET = findViewById(R.id.nombreET)
        descET = findViewById(R.id.descET)
        precioET = findViewById(R.id.precioET)

        val spinner = findViewById<Spinner>(R.id.spinner)
        val opciones = arrayOf("Verduras", "Frutas", "Frutos Secos")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, opciones)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        storageRef = FirebaseStorage.getInstance().reference

        subir.setOnClickListener(){
            elegirimg()

        }

        volver.setOnClickListener(){
            var intent2 = Intent(this@agregarproducto, mainvendedor::class.java)
            intent2.putExtra("correo", correo)
            startActivity(intent2)
        }

        enviar.setOnClickListener(){
            uploadImage()
        }

        try {
            correo = intent.getStringExtra("correo") ?: throw Exception("Correo no proporcionado")

        } catch (e: Exception){
            Toast.makeText(this, "Error al recibir el correo: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }

    }

    private fun elegirimg() {
        val intentimg = Intent(Intent.ACTION_PICK)
        intentimg.type = "image/*"
        startActivityForResult(intentimg, IMAGE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK){
            data?.data?.let{
                imagenUri = it
                imagenET.setImageURI(it)
            }

        }
    }

    private fun uploadImage() {
        if (imagenUri == null) {
            Toast.makeText(this, "Por favor, seleccione una imagen primero.", Toast.LENGTH_SHORT).show()
            return
        }

        val imagenRef = storageRef.child("productos/${nombreET.text.toString()}.jpg")
        imagenRef.putFile(imagenUri!!)
            .addOnSuccessListener { taskSnapshot ->
                // Correctamente se obtiene la URL de descarga aquí
                taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    sendProd(downloadUrl)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al subir la imagen: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    fun sendProd(downloadUrl: String) {
        // new code here
        val nombre = nombreET.text.toString()
        val desc = descET.text.toString()
        val precio = precioET.text.toString()
        val estado = "Activo"


        //path
        val database = Firebase.database
        val myRef = database.getReference("productos")

        //validar
        if (nombre.isEmpty() || desc.isEmpty() || precio.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        //Revisar existencia de producto

        myRef.child(correo).child(nombre).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // El producto ya existe
                    Toast.makeText(
                        this@agregarproducto,
                        "El Producto ya existe",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    // El producto no existe, crear el nuevo producto
                    val userMap = mapOf(
                        "Nombre" to nombre,
                        "Precio" to precio,
                        "Descripcion" to desc,
                        "Estado" to estado,
                        "Imagen" to downloadUrl,
                        "Categoria" to findViewById<Spinner>(R.id.spinner).selectedItem.toString()
                    )
                    myRef.child(correo).child(nombre).setValue(userMap)
                    Toast.makeText(this@agregarproducto, "Producto creado exitosamente", Toast.LENGTH_SHORT)
                        .show()
                    var intent2 = Intent(this@agregarproducto, mainvendedor::class.java)
                    intent2.putExtra("correo", correo)
                    startActivity(intent2)

                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Error en la lectura de la base de datos
                Toast.makeText(
                    this@agregarproducto,
                    "Error en la base de datos: ${databaseError.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}
