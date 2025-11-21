package net.irivas.cobrosapp.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import net.irivas.cobrosapp.R
import net.irivas.cobrosapp.data.CobroDTO
import net.irivas.cobrosapp.data.CobrosDBHelper

class CobrosAdapter(
    private var listaCobros: MutableList<CobroDTO>,
    private val onEdit: (CobroDTO) -> Unit,
    private val db: CobrosDBHelper
) : RecyclerView.Adapter<CobrosAdapter.CobroViewHolder>() {

    inner class CobroViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtNombre: TextView = itemView.findViewById(R.id.txtNombre)
        val txtPuesto: TextView = itemView.findViewById(R.id.txtPuesto)
        val txtFecha: TextView = itemView.findViewById(R.id.txtFecha)
        val txtCobrado: TextView = itemView.findViewById(R.id.txtCobrado)
        val txtRecibido: TextView = itemView.findViewById(R.id.txtRecibido)
        val txtVuelto: TextView = itemView.findViewById(R.id.txtVuelto)
        val btnEditar = itemView.findViewById<ImageView>(R.id.btnEdit)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CobroViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cobro, parent, false)
        return CobroViewHolder(view)
    }

    override fun onBindViewHolder(holder: CobroViewHolder, position: Int) {
        val cobro = listaCobros[position]

        holder.txtNombre.text = cobro.nombreComerciante
        holder.txtPuesto.text = "# ${cobro.numeroPuesto}"
        holder.txtFecha.text = cobro.fecha
        holder.txtCobrado.text = "$${cobro.monto}"
        holder.txtRecibido.text = "$${cobro.recibido}"
        holder.txtVuelto.text = "$" + String.format("%.2f", cobro.vuelto)

        holder.btnEditar.setOnClickListener { onEdit(cobro) }

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

    override fun getItemCount(): Int = listaCobros.size

    fun actualizarLista(nuevaLista: List<CobroDTO>) {
        listaCobros.clear()
        listaCobros.addAll(nuevaLista)
        notifyDataSetChanged()
    }
}