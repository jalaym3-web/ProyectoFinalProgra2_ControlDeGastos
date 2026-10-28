package com.example.aplicacioncontrolgastos.ui.seleccion


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.aplicacioncontrolgastos.databinding.ItemUsuarioBinding
import com.example.core.model.Usuario

class UsuarioAdapter(
    private val onClick: (Usuario) -> Unit
) : ListAdapter<Usuario, UsuarioAdapter.VH>(Diff) {

    class VH(val binding: ItemUsuarioBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(ItemUsuarioBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val usuario = getItem(position)
        holder.binding.tvNombre.text = "${usuario.nombre} ${usuario.apellido}"
        holder.binding.root.setOnClickListener { onClick(usuario) }
    }

    object Diff : DiffUtil.ItemCallback<Usuario>() {
        override fun areItemsTheSame(a: Usuario, b: Usuario) = a.id == b.id
        override fun areContentsTheSame(a: Usuario, b: Usuario) = a == b
    }
}