package com.example.unitconverter

import android.R
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById<View?>(R.id.main),
            OnApplyWindowInsetsListener { v: View?, insets: WindowInsetsCompat? ->
                val systemBars = insets!!.getInsets(WindowInsetsCompat.Type.systemBars())
                v!!.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            })

        val editTextCelsius = findViewById<EditText?>(R.id.editTextCelsius)
        val buttonConvert = findViewById<Button?>(R.id.buttonConvert)

        buttonConvert.setOnClickListener(View.OnClickListener { v: View? ->
            val celsius = editTextCelsius.getText().toString().toDouble()
            val farenheit = celsius * 1.8 + 32

            val intent: Intent = Intent(this@MainActivity, ResultActivity::class.java)
            intent.putExtra("farenheit", farenheit)
            startActivity(intent)
        })
    }
}