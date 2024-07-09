package com.example.agromarket

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Switch
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

class mainvendedor : AppCompatActivity() {
    private lateinit var correo: String
    private lateinit var database: DatabaseReference
    private lateinit var databaseprod: DatabaseReference
    private lateinit var nombreTiendaTV: TextView

    private lateinit var productosLayOut: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_mainvendedor)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //Listar productos
        productosLayOut = findViewById(R.id.productoLO)
        databaseprod = FirebaseDatabase.getInstance().getReference("productos")

        //Listar nombre Tienda
        val agregar: Button = findViewById(R.id.agregar)
        nombreTiendaTV = findViewById(R.id.nombretienda)
        database = FirebaseDatabase.getInstance().getReference("usuarios")
        try {
            correo = intent.getStringExtra("correo") ?: throw Exception("Correo no proporcionado")
            buscarnombre(correo)
            buscarProductos(correo)

        } catch (e: Exception) {
            Toast.makeText(this, "Error al recibir el correo: ${e.message}", Toast.LENGTH_LONG)
                .show()
            e.printStackTrace()
        }
        agregar.setOnClickListener() {
            var intent2 = Intent(this@mainvendedor, agregarproducto::class.java)
            intent2.putExtra("correo", correo)
            startActivity(intent2)
        }
        //OPCIONES
        val opcion: Button = findViewById(R.id.opciones)
        opcion.setOnClickListener(){
            var intent2 = Intent(this@mainvendedor, opciones::class.java)
            intent2.putExtra("correo", correo)
            startActivity(intent2)
        }

    }



    //Nombre de tienda
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


    //Funcion llamar productos
    private fun buscarProductos(correo: String) {
        val userProductsRef = databaseprod.child(correo)
        userProductsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (productSnapshot in snapshot.children) {
                        findViewById<ImageView>(R.id.sinprod).visibility = View.GONE
                        val nombre = productSnapshot.child("Nombre").getValue(String::class.java)
                        val precio = productSnapshot.child("Precio").getValue(String::class.java)
                        val descripcion = productSnapshot.child("Descripcion").getValue(String::class.java)
                        val estado = productSnapshot.child("Estado").getValue(String::class.java)
                        val imagen = productSnapshot.child("Imagen").getValue(String::class.java)


                        if (nombre != null && precio != null && descripcion != null && estado != null && imagen != null) {
                            agregarProductoVista(nombre, precio, descripcion, estado, imagen)
                        }
                    }
                } else {
                    Toast.makeText(this@mainvendedor, "No se encontraron productos", Toast.LENGTH_SHORT).show()
                    findViewById<ImageView>(R.id.sinprod).visibility = View.VISIBLE

                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@mainvendedor, "Error en la consulta: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
//Vista
private fun agregarProductoVista(nombre: String, precio: String, descripcion: String, estado: String, imagen: String) {
    val inflater = LayoutInflater.from(this)
    val productoView = inflater.inflate(R.layout.producto_item, productosLayOut, false)

    val nombreTextView = productoView.findViewById<TextView>(R.id.nombreProductoTextView)
    val precioTextView = productoView.findViewById<TextView>(R.id.precioProductoTextView)
    val descripcionTextView = productoView.findViewById<TextView>(R.id.descripcionProductoTextView)
    val estadoSwitch = productoView.findViewById<Switch>(R.id.estadoProductoSwitch)
    val imagenImageView = productoView.findViewById<ImageView>(R.id.imagenProductoImageView)

    nombreTextView.text = nombre
    precioTextView.text = precio
    descripcionTextView.text = descripcion
    estadoSwitch.isChecked = estado == "Activo"
    Picasso.get().load(imagen).into(imagenImageView)
    productosLayOut.addView(productoView)
}

    }
