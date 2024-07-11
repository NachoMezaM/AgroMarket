package com.example.agromarket

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
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
import com.squareup.picasso.Picasso

class MainComprador : AppCompatActivity() {
    private lateinit var databaseprod: DatabaseReference
    private lateinit var productoslay: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_comprador)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        productoslay = findViewById(R.id.productsLayOT)
        databaseprod = FirebaseDatabase.getInstance().getReference("productos")
    }
    //buscar
    private fun buscarProductos(correo: String) {
        val userProductsRef = databaseprod.child(correo)
        userProductsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (productSnapshot in snapshot.children) {
                        findViewById<ImageView>(R.id.sinprod).visibility = View.GONE
                        val nombre = productSnapshot.child("Nombre").getValue(String::class.java)
                        val precio = productSnapshot.child("Precio").getValue(String::class.java)
                        val descripcion =
                            productSnapshot.child("Descripcion").getValue(String::class.java)
                        val imagen = productSnapshot.child("Imagen").getValue(String::class.java)


                        if (nombre != null && precio != null && descripcion != null && imagen != null) {
                            agregarProducto(nombre, precio, descripcion, imagen)
                        }
                    }
                } else {
                    Toast.makeText(
                        this@MainComprador,
                        "No se encontraron productos",
                        Toast.LENGTH_SHORT
                    ).show()
                    findViewById<ImageView>(R.id.sinprod).visibility = View.VISIBLE

                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@MainComprador,
                    "Error en la consulta: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
    //Vista
    private fun agregarProducto(nombre: String, precio: String, descripcion: String, imagen: String) {
        val inflater = LayoutInflater.from(this)
        val productoView = inflater.inflate(R.layout.producto_mostrar, productoslay, false)

        val nombreTextView = productoView.findViewById<TextView>(R.id.nombreProductoTextView)
        val precioTextView = productoView.findViewById<TextView>(R.id.precioProductoTextView)
        val descripcionTextView = productoView.findViewById<TextView>(R.id.descripcionProductoTextView)
        val imagenImageView = productoView.findViewById<ImageView>(R.id.imagenProductoImageView)

        nombreTextView.text = nombre
        precioTextView.text = precio
        descripcionTextView.text = descripcion
        Picasso.get().load(imagen).into(imagenImageView)
        productoslay.addView(productoView)
    }
}




