package net.irivas.cobrosapp.adapters

import androidx.recyclerview.widget.RecyclerView
import net.irivas.cobrosapp.data.Pago
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import net.irivas.cobrosapp.R

class PagosAdapter(
    private var listaPagos: MutableList<Pago>
) : RecyclerView.Adapter<PagosAdapter.PagoViewHolder>() {

    inner class PagoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtNombre: TextView = itemView.findViewById(R.id.txtNombre)
        val txtPuesto: TextView = itemView.findViewById(R.id.txtPuesto)
        val txtFecha: TextView = itemView.findViewById(R.id.txtFecha)
        val txtCobrado: TextView = itemView.findViewById(R.id.txtCobrado)
        val txtRecibido: TextView = itemView.findViewById(R.id.txtRecibido)
        val txtVuelto: TextView = itemView.findViewById(R.id.txtVuelto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pago, parent, false)
        return PagoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PagoViewHolder, position: Int) {
        val pago = listaPagos[position]

        holder.txtNombre.text = pago.nombre
        holder.txtPuesto.text = pago.numeroPuesto
        holder.txtFecha.text = pago.fecha
        holder.txtCobrado.text = "$${pago.monto}"
        holder.txtRecibido.text = "$${pago.recibido}"
        holder.txtVuelto.text = "$${pago.vuelto}"
    }

    override fun getItemCount(): Int = listaPagos.size

    fun actualizarDatos(nuevaLista: List<Pago>) {
        listaPagos.clear()
        listaPagos.addAll(nuevaLista)
        notifyDataSetChanged()
    }
}