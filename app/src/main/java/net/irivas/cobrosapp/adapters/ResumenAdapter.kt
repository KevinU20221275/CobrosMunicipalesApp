package net.irivas.cobrosapp.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import net.irivas.cobrosapp.data.CobroResumen
import net.irivas.cobrosapp.R

class ResumenAdapter(
    private var lista: List<CobroResumen>
) : RecyclerView.Adapter<ResumenAdapter.ViewHolder>() {

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val fecha = v.findViewById<TextView>(R.id.txtFecha)
        val comerciante = v.findViewById<TextView>(R.id.txtComerciante)
        val puesto = v.findViewById<TextView>(R.id.txtPuesto)
        val monto = v.findViewById<TextView>(R.id.txtMonto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_resumen, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount() = lista.size

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        val item = lista[pos]

        Log.d("BIND_DEBUG", item.comerciante)

        holder.fecha.text = item.fecha
        holder.comerciante.text = item.comerciante
        holder.puesto.text = item.puesto
        holder.monto.text = String.format("$%,.2f", item.monto)
    }

    fun actualizar(nuevaLista: List<CobroResumen>) {
        lista = nuevaLista
        notifyDataSetChanged()
    }
}
