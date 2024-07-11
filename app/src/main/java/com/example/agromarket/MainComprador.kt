package com.example.agromarket

import agromarket.unach.VisualizarProd
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
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
    private lateinit var productsLayout: LinearLayout
    private lateinit var database: DatabaseReference
    private lateinit var correo: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_comprador)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        productsLayout = findViewById(R.id.productsLayOT)
        database = FirebaseDatabase.getInstance().getReference("productos")
        loadProducts()

    }
    private fun loadProducts() {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                productsLayout.removeAllViews()  // Limpia el layout antes de agregar nuevos productos

                if (snapshot.exists()) {
                    // Itera sobre cada usuario (correo del vendedor)
                    for (userSnapshot in snapshot.children) {
                        val sellerEmail = userSnapshot.key  // Correo del vendedor es la clave del nodo

                        // Itera sobre cada producto del vendedor
                        for (productSnapshot in userSnapshot.children) {
                            val nombre = productSnapshot.child("Nombre").getValue(String::class.java)
                            val precio = productSnapshot.child("Precio").getValue(String::class.java)
                            val descripcion = productSnapshot.child("Descripcion").getValue(String::class.java)
                            val imageUrl = productSnapshot.child("Imagen").getValue(String::class.java) // Asumiendo que hay una imagen

                            if (nombre != null && precio != null && descripcion != null) {
                                // Inflate y población del view de producto
                                val productView = LayoutInflater.from(this@MainComprador).inflate(R.layout.producto_mostrar, productsLayout, false)
                                productView.findViewById<TextView>(R.id.productName).text = nombre
                                productView.findViewById<TextView>(R.id.productDescription).text = descripcion
                                productView.findViewById<TextView>(R.id.productPrice).text = precio
                                val imageView = productView.findViewById<ImageView>(R.id.productImage)

                                if (imageUrl != null) {
                                    Picasso.get().load(imageUrl).into(imageView)
                                }

                                val productImageButton = productView.findViewById<ImageButton>(R.id.productBuy)
                                productImageButton.setOnClickListener {
                                    val intent = Intent(this@MainComprador, VisualizarProd::class.java)
                                    intent.putExtra("productName", nombre)
                                    intent.putExtra("productDescription", descripcion)
                                    intent.putExtra("productPrice", precio)
                                    intent.putExtra("productImage", imageUrl)
                                    intent.putExtra("sellerEmail", sellerEmail)  // Pasa el correo del vendedor
                                    intent.putExtra("correo", correo)
                                    startActivity(intent)
                                }

                                productsLayout.addView(productView)
                            }
                        }
                    }
                } else {
                    Toast.makeText(this@MainComprador, "No se encontraron productos", Toast.LENGTH_LONG).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainComprador, "Error en la consulta: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })
    }


}



