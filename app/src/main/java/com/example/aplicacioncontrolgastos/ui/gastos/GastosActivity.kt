package com.example.aplicacioncontrolgastos.ui.gastos

import android.content.DialogInterface
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
import com.example.aplicacioncontrolgastos.data.repository.MovimientoRepositoryImpl
import com.example.aplicacioncontrolgastos.databinding.ActivityGastosBinding
import com.example.aplicacioncontrolgastos.databinding.DialogMovimientoBinding
import com.example.core.model.FiltroMovimientos
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
        GastosViewModel.GastosViewModelFactory(
            MovimientoRepositoryImpl(db.movimientoDao()),
            usuarioId
        )
    }

    private val adapter = MovimientoAdapter(
        onEditar = { mostrarFormulario(it) },
        onEliminar = { confirmarEliminar(it) }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (usuarioId == -1L) {
            finish()
            return
        }

        binding = ActivityGastosBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
                launch {
                    viewModel.error.collect {
                        Toast.makeText(this@GastosActivity, it, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun confirmarEliminar(movimiento: Movimiento) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar movimiento")
            .setMessage("¿Eliminar \"${movimiento.descripcion}\"?")
            .setPositiveButton("Eliminar") { _, _ -> viewModel.eliminar(movimiento.id) }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun mostrarFormulario(existente: Movimiento? = null) {
        val dialogBinding = DialogMovimientoBinding.inflate(layoutInflater)

        if (existente != null) {
            dialogBinding.etDescripcion.setText(existente.descripcion)
            dialogBinding.etMonto.setText(existente.monto.toString())
            if (existente.tipo == TipoMovimiento.INGRESO) dialogBinding.rbIngreso.isChecked = true
            else dialogBinding.rbGasto.isChecked = true   // ajusta el id al de tu XML
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle(if (existente == null) "Nuevo movimiento" else "Editar movimiento")
            .setView(dialogBinding.root)
            .setPositiveButton("Guardar", null)
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                val descripcion = dialogBinding.etDescripcion.text.toString().trim()
                val monto = dialogBinding.etMonto.text.toString().toDoubleOrNull()
                val tipo = if (dialogBinding.rbIngreso.isChecked) TipoMovimiento.INGRESO
                else TipoMovimiento.GASTO

                if (descripcion.isEmpty() || monto == null || monto <= 0) {
                    Toast.makeText(this, "Revisa la descripción y el monto", Toast.LENGTH_SHORT).show()
                } else {
                    if (existente == null) viewModel.agregar(descripcion, monto, tipo)
                    else viewModel.editar(existente, descripcion, monto, tipo)
                    dialog.dismiss()
                }
            }
        }
        dialog.show()
    }
}