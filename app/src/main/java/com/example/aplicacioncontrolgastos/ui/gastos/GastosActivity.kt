package com.example.aplicacioncontrolgastos.ui.gastos

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aplicacioncontrolgastos.data.local.AppDatabase
import com.example.aplicacioncontrolgastos.data.repository.MovimientoRepository
import com.example.aplicacioncontrolgastos.databinding.ActivityGastosBinding
import com.example.aplicacioncontrolgastos.databinding.DialogMovimientoBinding
import com.example.aplicacioncontrolgastos.ui.theme.AplicacionControlGastosTheme
import com.example.core.model.TipoMovimiento
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.O)
class GastosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGastosBinding

    private val usuarioId: Long by lazy { intent.getLongExtra("usuarioId", -1L) }

    private val viewModel: GastosViewModel by viewModels {
        val db = AppDatabase.get(applicationContext)
        GastosViewModelFactory(MovimientoRepository(db.movimientoDao()), usuarioId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (usuarioId == -1L) {
            finish()
            return
        }

        binding = ActivityGastosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = MovimientoAdapter()
        binding.rvMovimientos.layoutManager = LinearLayoutManager(this)
        binding.rvMovimientos.adapter = adapter

        binding.btnAgregar.setOnClickListener { mostrarFormulario() }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { viewModel.movimientos.collect { adapter.submitList(it) } }
                launch {
                    viewModel.balance.collect {
                        binding.tvBalance.text = "Balance: ${"%.2f".format(it)}"
                    }
                }
            }
        }
    }

    private fun mostrarFormulario() {
        val dialogBinding = DialogMovimientoBinding.inflate(layoutInflater)

        AlertDialog.Builder(this)
            .setTitle("Nuevo movimiento")
            .setView(dialogBinding.root)
            .setPositiveButton("Guardar") { _, _ ->
                val descripcion = dialogBinding.etDescripcion.text.toString().trim()
                val monto = dialogBinding.etMonto.text.toString().toDoubleOrNull()
                val tipo = if (dialogBinding.rbIngreso.isChecked) TipoMovimiento.INGRESO
                else TipoMovimiento.GASTO

                if (descripcion.isEmpty() || monto == null || monto <= 0) {
                    Toast.makeText(this, "Revisa la descripción y el monto", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.agregar(descripcion, monto, tipo)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}