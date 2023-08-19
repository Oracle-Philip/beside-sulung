package com.rummy.sulung.view.ui.store

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.rummy.sulung.R
import com.rummy.sulung.view.ui.addRecord.Emotion_Image_Adapter

class Filter_Item_Adapter(private val items: MutableList<Triple<String, Boolean, String>>, private val itemWidth: Int) :
    RecyclerView.Adapter<Filter_Item_Adapter.ViewHolder>() {

    var listener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(values: Map<String, String>)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    private val selectedValues: MutableMap<String, String> = mutableMapOf()

    fun getSelectedValues(): Map<String, String> {
        return selectedValues
    }

    fun setSelectedValues(values: List<Triple<String, Boolean, String>>) {
        selectedValues.clear()
        for (value in values) {
            val index = items.indexOfFirst { it.third == value.third }
            if (index >= 0 && index < items.size) {
                if (items[index].first == "전체") {
                    when(items[index].second){
                        true -> {
                            selectedValues.clear()
                            val selectedAlcoholItemValueMap = items.map { it.first to it.third }.toMap()
                            selectedValues.putAll(selectedAlcoholItemValueMap)
                        }
                        false -> {
                            selectedValues.clear()
                        }
                    }
                }else{
                    if (items[index].second) {
                        selectedValues[value.first] = value.third
                    }
                }
            }
        }
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemName: TextView = itemView.findViewById(R.id.item_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.filter_item_layout, parent, false)
        val layoutParams = view.layoutParams
        layoutParams.width = itemWidth
        view.layoutParams = layoutParams
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (itemName, isChecked, itemValue) = items[position]
        holder.itemName.text = itemName

        holder.itemName.apply {
            textSize = 16f
            typeface = if (isChecked) ResourcesCompat.getFont(
                context,
                R.font.pretendard_bold
            ) else ResourcesCompat.getFont(context, R.font.pretendard_regular)
        }

        holder.itemView.isEnabled = true // Enable the item by default
        if (position == 0) {
            // Disable all other items if the first item is checked
            if (isChecked) {
                for (i in 1 until items.size) {
                    holder.itemView.rootView.post {
                        items[i] = Triple(items[i].first, false, items[i].third)
                    }
                }
            }
        } else {
            // Disable the item if the first item is checked
            if (items[0].second) {
                holder.itemView.isEnabled = false
            }
        }

        holder.itemView.setBackgroundResource(
            if (isChecked) R.drawable.edit_item_selected_bg
            else R.drawable.edit_item_unselected_bg
        )

        holder.itemView.setOnClickListener {
            if (position == 0) {
                // Toggle item's isChecked and third value
                val newValue = !isChecked
                items[position] = Triple(itemName, newValue, itemValue)
                if (newValue) {
                    selectedValues.clear()
                    selectedValues[itemName] = itemValue
                } else {
                    selectedValues.clear()
                }
                for (i in 1 until items.size) {
                    holder.itemView.isEnabled = false
                // Enable or disable the item accordingly
                }
            } else {
                // Toggle item's isChecked
                items[position] = Triple(itemName, !isChecked, itemValue)

                // Update selectedValues
                if (isChecked) {
                    selectedValues.remove(itemName)
                } else {
                    selectedValues[itemName] = itemValue
                    Log.e("selectedValues", selectedValues.toString())
                }

                holder.itemView.isEnabled =
                    !items[0].second // Enable or disable the item accordingly
            }


            listener?.onItemClick(selectedValues)

            // Notify adapter about the data change
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int = items.size
}


