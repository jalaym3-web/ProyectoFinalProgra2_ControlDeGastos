package com.example.aplicacioncontrolgastos

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.aplicacioncontrolgastos.ui.registro.RegistroActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(Intent(this, RegistroActivity::class.java))
        finish()
    }
}
