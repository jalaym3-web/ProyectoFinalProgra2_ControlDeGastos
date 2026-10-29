package com.example.aplicacioncontrolgastos.ui.gastos

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aplicacioncontrolgastos.data.local.AppDatabase
import com.example.aplicacioncontrolgastos.data.repository.MovimientoRepository
import com.example.aplicacioncontrolgastos.databinding.ActivityGastosBinding
import com.example.aplicacioncontrolgastos.databinding.DialogMovimientoBinding
import com.example.core.model.Movimiento
import com.example.core.model.TipoMovimiento
import kotlinx.coroutines.launch
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.flow.combine



@RequiresApi(Build.VERSION_CODES.O)
class GastosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGastosBinding

    private val usuarioId: Long by lazy { intent.getLongExtra("usuarioId", -1L) }

    private val viewModel: GastosViewModel by viewModels {
        val db = AppDatabase.get(applicationContext)
        GastosViewModelFactory(MovimientoRepository(db.movimientoDao()), usuarioId)
    }

    private fun confirmarEliminar(movimiento: Movimiento) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar movimiento")
            .setMessage("¿Eliminar \"${movimiento.descripcion}\"?")
            .setPositiveButton("Eliminar") { _, _ -> viewModel.eliminar(movimiento) }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    val adapter = MovimientoAdapter { movimiento -> confirmarEliminar(movimiento) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (usuarioId == -1L) {
            finish()
            return
        }

        binding = ActivityGastosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = MovimientoAdapter { movimiento -> confirmarEliminar(movimiento) }
        binding.rvMovimientos.layoutManager = LinearLayoutManager(this)
        binding.rvMovimientos.adapter = adapter

        binding.btnAgregar.setOnClickListener { mostrarFormulario() }

        binding.tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewModel.filtrar(
                    when (tab.position) {
                        1 -> FiltroMovimientos.GASTOS
                        2 -> FiltroMovimientos.INGRESOS
                        else -> FiltroMovimientos.TODOS
                    }
                )
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { viewModel.movimientos.collect { adapter.submitList(it) } }
                launch {
                    viewModel.balance.collect {
                        binding.tvBalance.text = "Balance: ${"%.2f".format(it)}"
                    }
                }
                launch {
                    combine(viewModel.totalIngresos, viewModel.totalGastos) { i, g -> i to g }
                        .collect { (ingresos, gastos) ->
                            binding.tvTotales.text =
                                "Ingresos: ${"%.2f".format(ingresos)}  |  Gastos: ${"%.2f".format(gastos)}"
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