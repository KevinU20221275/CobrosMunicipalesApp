package net.irivas.cobrosapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import net.irivas.cobrosapp.data.PanelItem
import net.irivas.cobrosapp.R

class PanelAdministracionAdapter(
    private val items: List<PanelItem>,
    private val onClick: (PanelItem) -> Unit
) : RecyclerView.Adapter<PanelAdministracionAdapter.ViewHolder>() {

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.imgIcon)
        val title: TextView = view.findViewById(R.id.txtTituloCard)
        val description: TextView = view.findViewById(R.id.txtDescripcionCard)

        init {
            view.setOnClickListener {
                onClick(items[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_panel, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val i = items[position]
        holder.icon.setImageResource(i.icon)
        holder.title.text = i.titulo
        holder.description.text = i.descripcion
    }

    override fun getItemCount() = items.size
}
