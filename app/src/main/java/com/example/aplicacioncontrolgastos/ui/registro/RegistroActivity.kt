package com.example.aplicacioncontrolgastos.ui.registro


import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.aplicacioncontrolgastos.data.local.AppDatabase
import com.example.aplicacioncontrolgastos.data.repository.UsuarioRepository
import kotlinx.coroutines.launch
import com.example.aplicacioncontrolgastos.databinding.ActivityRegistroBinding
import com.example.aplicacioncontrolgastos.ui.gastos.GastosActivity


class RegistroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistroBinding
    private val viewModel: RegistroViewModel by viewModels {
        val db = AppDatabase.get(applicationContext)
        RegistroViewModelFactory(UsuarioRepository(db.usuarioDao()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.etNombre.doAfterTextChanged { viewModel.onNombreChange(it.toString()) }
        binding.etApellido.doAfterTextChanged { viewModel.onApellidoChange(it.toString()) }
        binding.etCorreo.doAfterTextChanged { viewModel.onCorreoChange(it.toString()) }

        binding.btnRegistrar.setOnClickListener { viewModel.registrar() }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.root.visibility = if (state.verificando) View.INVISIBLE else View.VISIBLE

                    binding.tilNombre.error = state.errorNombre
                    binding.tilApellido.error = state.errorApellido
                    binding.tilCorreo.error = state.errorCorreo

                    if (state.registrado) {
                        startActivity(Intent(this@RegistroActivity, GastosActivity::class.java))
                        finish()
                    }
                }
            }
        }
    }
}