package net.irivas.cobrosapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import net.irivas.cobrosapp.data.Puesto
import net.irivas.cobrosapp.R
import kotlin.text.clear

class PuestoAdapter(
    private val lista: MutableList<Puesto>,
    private val onSelect: (Puesto) -> Unit
) : RecyclerView.Adapter<PuestoAdapter.ViewHolder>() {

    // Guardamos IDs, no posiciones
    private val seleccionados = mutableSetOf<Int>()

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val num = view.findViewById<TextView>(R.id.txtNumPuesto)
        val tarifa = view.findViewById<TextView>(R.id.txtTarifaPuesto)

        init {
            view.setOnClickListener {
                val puesto = lista[adapterPosition]

                if (seleccionados.contains(puesto.id)) {
                    seleccionados.remove(puesto.id)
                } else {
                    seleccionados.add(puesto.id)
                }

                notifyItemChanged(adapterPosition)
                onSelect(puesto)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_puesto_seleccion, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val p = lista[position]
        holder.num.text = "Puesto #${p.numero}"
        holder.tarifa.text = "Tarifa: $${p.tarifa}"

        val isSelected = seleccionados.contains(p.id)

        holder.itemView.setBackgroundResource(
            if (isSelected) R.drawable.card_selected
            else R.drawable.card_background
        )
    }

    override fun getItemCount() = lista.size

    fun obtenerSeleccionados(): List<Int> = seleccionados.toList()

    fun actualizarLista(nuevaLista: List<Puesto>) {
        lista.clear()
        lista.addAll(nuevaLista)
        seleccionados.clear()
        notifyDataSetChanged()
    }

    fun limpiarSeleccion() {
        seleccionados.clear()
        notifyDataSetChanged()
    }
}
