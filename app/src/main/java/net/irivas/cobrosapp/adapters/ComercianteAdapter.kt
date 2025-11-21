package net.irivas.cobrosapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import net.irivas.cobrosapp.data.Comerciante
import net.irivas.cobrosapp.R

class ComercianteAdapter(
    private val listaMostrada: MutableList<Comerciante>,
    private val onGestionarPuestos: (Comerciante) -> Unit,
    private val onEdit : (Comerciante) -> Unit
) : RecyclerView.Adapter<ComercianteAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombre = view.findViewById<TextView>(R.id.txtNombreComerciante)
        val telefono = view.findViewById<TextView>(R.id.txtTelefono)
        val btnPuestos = view.findViewById<Button>(R.id.btnGestionarPuestos)
        val btnEditar = view.findViewById<Button>(R.id.btnEditarComerciante)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comerciante, parent, false)
        return ViewHolder(vista)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val com = listaMostrada[position]
        holder.nombre.text = "Nombre: ${com.nombre}"
        holder.telefono.text = "Telefono: ${com.telefono}"

        holder.btnPuestos.setOnClickListener {
            onGestionarPuestos(com)
        }

        holder.btnEditar.setOnClickListener {
            onEdit(com)
        }

        animarItem(holder.itemView)
    }

    override fun getItemCount(): Int = listaMostrada.size

    fun actualizarLista(nuevaLista: List<Comerciante>) {
        listaMostrada.clear()
        listaMostrada.addAll(nuevaLista)
        notifyDataSetChanged()
    }

    private fun animarItem(view: View) {
        view.alpha = 0f
        view.translationY = 30f
        view.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(200)
            .start()
    }
}

