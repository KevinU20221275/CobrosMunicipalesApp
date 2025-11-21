package net.irivas.cobrosapp.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import net.irivas.cobrosapp.data.Puesto
import net.irivas.cobrosapp.R

class PuestoListAdapter(
    private var lista: MutableList<Puesto>,
    private val onEdit: (Puesto) -> Unit
) : RecyclerView.Adapter<PuestoListAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtNumero = view.findViewById<TextView>(R.id.txtNumeroPuesto)
        val txtTarifa = view.findViewById<TextView>(R.id.txtTarifa)
        val txtDisponible = view.findViewById<TextView>(R.id.txtDisponible)
        val btnEditar = view.findViewById<ImageButton>(R.id.btnEditarPuesto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_puesto, parent, false)
        return ViewHolder(vista)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val puesto = lista[position]
        holder.txtNumero.text = "Puesto #${puesto.numero}"
        holder.txtTarifa.text = "$" + String.format("%.2f", puesto.tarifa)

        if (puesto.disponible == 1) {
            holder.txtDisponible.text = "Disponible"
            holder.txtDisponible.setBackgroundResource(R.drawable.bg_status_disponible)
            holder.txtDisponible.setTextColor(Color.WHITE)
        } else {
            holder.txtDisponible.text = "Ocupado"
            holder.txtDisponible.setBackgroundResource(R.drawable.bg_status_ocupado)
            holder.txtDisponible.setTextColor(Color.WHITE)
        }

        holder.btnEditar.setOnClickListener { onEdit(puesto) }

        // animacion
        holder.itemView.apply {
            alpha = 0f
            translationY = 30f
            animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(200)
                .setStartDelay(position * 20L) // efecto cascada
                .start()
        }
    }

    override fun getItemCount(): Int = lista.size

    fun actualizarLista(nuevaLista: List<Puesto>) {
        lista = nuevaLista.toMutableList()
        notifyDataSetChanged()
    }
}

