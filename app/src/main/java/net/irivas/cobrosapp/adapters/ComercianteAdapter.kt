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
    private val lista: List<Comerciante>,
    private val onGestionarPuestos: (Comerciante) -> Unit
) : RecyclerView.Adapter<ComercianteAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombre = view.findViewById<TextView>(R.id.txtNombreComerciante)
        val telefono = view.findViewById<TextView>(R.id.txtTelefono)
        val btnPuestos = view.findViewById<Button>(R.id.btnGestionarPuestos)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comerciante, parent, false)
        return ViewHolder(vista)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val com = lista[position]
        holder.nombre.text = "Nombre: ${com.nombre}"
        holder.telefono.text = "Telefono: ${com.telefono}"

        holder.btnPuestos.setOnClickListener {
            onGestionarPuestos(com)
        }
    }

    override fun getItemCount(): Int = lista.size
}

