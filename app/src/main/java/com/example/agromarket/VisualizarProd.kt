package agromarket.unach

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.agromarket.MainComprador
import com.example.agromarket.R
import com.squareup.picasso.Picasso

class VisualizarProd : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_visualizar_prod)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //RECIBIR INTENT
        val nombre = intent.getStringExtra("productName")
        val descripcion = intent.getStringExtra("productDescription")
        val precio = intent.getStringExtra("productPrice")
        val imageUrl = intent.getStringExtra("productImage")
        val correo = intent.getStringExtra("correo")
        //iniciar
        val productNameTextView = findViewById<TextView>(R.id.productNameV)
        val productDescriptionTextView = findViewById<TextView>(R.id.productDescriptionV)
        val productPriceTextView = findViewById<TextView>(R.id.productPriceV)
        val productImageView = findViewById<ImageView>(R.id.imageView4)

        val volvercomprador = findViewById<ImageButton>(R.id.volvercomprador)
        val comprar = findViewById<ImageButton>(R.id.comprar)

        productNameTextView.text = nombre ?: "Nombre no disponible"
        productDescriptionTextView.text = descripcion ?: "Descripción no disponible"
        productPriceTextView.text = precio ?: "Precio no disponible"

        if (!imageUrl.isNullOrEmpty()) {
            Picasso.get().load(imageUrl).into(productImageView)
        }
        volvercomprador.setOnClickListener(){
            val intent = Intent(this@VisualizarProd, MainComprador::class.java)
            intent.putExtra("correo", correo)
            startActivity(intent)
        }
        comprar.setOnClickListener(){
            solicitudCompra()
        }
    }
    private fun solicitudCompra() {
        val productName = intent.getStringExtra("productName")
        val productDescription = intent.getStringExtra("productDescription")
        val productPrice = intent.getStringExtra("productPrice")
        val sellerEmail = intent.getStringExtra("sellerEmail")?.replace(",",".") ?: ""

        if (sellerEmail.isEmpty()) {
            Toast.makeText(this, "Correo del vendedor no disponible.", Toast.LENGTH_LONG).show()
            return
        }

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(sellerEmail))
            putExtra(Intent.EXTRA_SUBJECT, "Solicitud de compra para $productName")
            putExtra(Intent.EXTRA_TEXT, "Me gustaría comprar el producto: $productName\nDescripción: $productDescription\nPrecio: $productPrice")
        }

        try {
            startActivity(Intent.createChooser(intent, "Enviar email..."))
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(this, "No hay aplicaciones de correo instaladas.", Toast.LENGTH_SHORT).show()
        }
    }
}