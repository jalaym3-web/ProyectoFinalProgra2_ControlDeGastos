package com.example.aplicacioncontrolgastos.ui.gastos

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.aplicacioncontrolgastos.databinding.ItemMovimientoBinding
import com.example.core.model.Movimiento
import com.example.core.model.TipoMovimiento
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MovimientoAdapter : ListAdapter<Movimiento, MovimientoAdapter.VH>(Diff) {

    class VH(val binding: ItemMovimientoBinding) : RecyclerView.ViewHolder(binding.root)

    private val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(ItemMovimientoBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val mov = getItem(position)
        val signo = if (mov.tipo == TipoMovimiento.INGRESO) "+" else "-"

        holder.binding.tvDescripcion.text = mov.descripcion
        holder.binding.tvFecha.text = formato.format(Date(mov.fecha))
        holder.binding.tvMonto.text = "$signo ${"%.2f".format(mov.monto)}"
    }

    object Diff : DiffUtil.ItemCallback<Movimiento>() {
        override fun areItemsTheSame(a: Movimiento, b: Movimiento) = a.id == b.id
        override fun areContentsTheSame(a: Movimiento, b: Movimiento) = a == b
    }
}