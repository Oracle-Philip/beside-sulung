package com.rummy.sulung.view.ui.diary

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.rummy.sulung.R

class Editing_Item_Adapter(private val items: MutableList<Triple<String, Boolean, Int>>, private val itemWidth: Int) :
    RecyclerView.Adapter<Editing_Item_Adapter.ViewHolder>() {

    var listener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    private var selectedPosition: Int = -1

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemName: TextView = itemView.findViewById(R.id.item_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_editing_layout, parent, false)
        val layoutParams = view.layoutParams
        layoutParams.width = itemWidth
        view.layoutParams = layoutParams
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val (itemName, isChecked, itemValue) = items[position]
        holder.itemName.text = itemName

        holder.itemName.apply {
            textSize = if (isChecked) 16f else 16f
            typeface = if (isChecked) ResourcesCompat.getFont(context, R.font.pretendard_bold) else ResourcesCompat.getFont(context, R.font.pretendard_regular)
        }

        holder.itemView.setBackgroundResource(
            if (isChecked) R.drawable.edit_item_selected_bg
            else R.drawable.edit_item_unselected_bg
        )

        holder.itemView.setOnClickListener {
            // Set all isChecked to false
            for (i in items.indices) {
                items[i] = Triple(items[i].first, false, items[i].third)
            }

            // Set selected item's isChecked to true
            items[position] = Triple(itemName, true, itemValue)

            // Update selectedPosition
            selectedPosition = position

            // Notify adapter about the data change
            notifyDataSetChanged()

            listener?.onItemClick(itemValue)
        }
    }

    override fun getItemCount(): Int = items.size
}

