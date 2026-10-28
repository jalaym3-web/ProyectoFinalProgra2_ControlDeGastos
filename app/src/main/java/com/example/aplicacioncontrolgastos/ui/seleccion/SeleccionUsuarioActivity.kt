package com.example.aplicacioncontrolgastos.ui.seleccion

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aplicacioncontrolgastos.data.local.AppDatabase
import com.example.aplicacioncontrolgastos.ui.gastos.GastosActivity
import com.example.aplicacioncontrolgastos.data.repository.UsuarioRepository
import com.example.aplicacioncontrolgastos.databinding.ActivitySeleccionUsuarioBinding
import com.example.aplicacioncontrolgastos.ui.registro.RegistroActivity
import kotlinx.coroutines.launch

class SeleccionUsuarioActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySeleccionUsuarioBinding

    private val viewModel: SeleccionUsuarioViewModel by viewModels {
        val db = AppDatabase.get(applicationContext)
        SeleccionUsuarioViewModelFactory(UsuarioRepository(db.usuarioDao()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySeleccionUsuarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = UsuarioAdapter { usuario ->
            startActivity(
                Intent(this, GastosActivity::class.java).putExtra("usuarioId", usuario.id)
            )
        }
        binding.rvUsuarios.layoutManager = LinearLayoutManager(this)
        binding.rvUsuarios.adapter = adapter

        binding.btnNuevoUsuario.setOnClickListener {
            startActivity(Intent(this, RegistroActivity::class.java))
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.usuarios.collect { adapter.submitList(it) }
            }
        }
    }
}