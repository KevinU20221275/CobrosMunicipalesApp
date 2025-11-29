package net.irivas.cobrosapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import net.irivas.cobrosapp.data.TopComerciante
import net.irivas.cobrosapp.R

class TopComerciantesAdapter(
    private var lista: List<TopComerciante>
) : RecyclerView.Adapter<TopComerciantesAdapter.ViewHolder>() {

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val nombre = v.findViewById<TextView>(R.id.txtNombre)
        val cobros = v.findViewById<TextView>(R.id.txtCobros)
        val monto = v.findViewById<TextView>(R.id.txtMonto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_top_comerciante, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount() = lista.size

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        val item = lista[pos]

        holder.nombre.text = item.nombre
        holder.cobros.text = "${item.totalCobros} cobros"
        holder.monto.text = String.format("$%,.2f", item.totalPagado)
    }

    fun actualizar(nuevaLista: List<TopComerciante>) {
        lista = nuevaLista
        notifyDataSetChanged()
    }
}
