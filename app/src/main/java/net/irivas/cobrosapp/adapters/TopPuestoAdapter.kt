package net.irivas.cobrosapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import net.irivas.cobrosapp.data.CobroPorPuesto
import net.irivas.cobrosapp.R

class TopPuestoAdapter(
    private var lista: List<CobroPorPuesto>
) : RecyclerView.Adapter<TopPuestoAdapter.ViewHolder>() {

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val puesto = v.findViewById<TextView>(R.id.txtPuestoNumero)
        val cantidad = v.findViewById<TextView>(R.id.txtCantidadCobros)
        val monto = v.findViewById<TextView>(R.id.txtMontoTotal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_top_puesto, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount() = lista.size

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        val item = lista[pos]

        holder.puesto.text = "Puesto ${item.numeroPuesto}"
        holder.cantidad.text = "${item.totalCobros} cobros"
        holder.monto.text = String.format("$%,.2f", item.totalMonto)
    }

    fun actualizar(nuevaLista: List<CobroPorPuesto>) {
        lista = nuevaLista
        notifyDataSetChanged()
    }
}
