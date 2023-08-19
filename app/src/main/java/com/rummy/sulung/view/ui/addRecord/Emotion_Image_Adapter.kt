package com.rummy.sulung.view.ui.addRecord

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.rummy.sulung.R

class Emotion_Image_Adapter(private val images: MutableList<Quadruple<Int, String, Boolean, Int>>, private val itemWidth: Int) :
    RecyclerView.Adapter<Emotion_Image_Adapter.ViewHolder>() {

    var listener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(value: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    private var selectedPosition: Int = -1

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val emotion_img: ImageView = itemView.findViewById(R.id.emotion_img)
        val emotion_name: TextView = itemView.findViewById(R.id.emotion_name)
        val emotion_check: ImageView = itemView.findViewById(R.id.emotion_check)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_emotion_layout, parent, false)
        val layoutParams = view.layoutParams
        layoutParams.width = itemWidth
        view.layoutParams = layoutParams
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val (imgResId, imgName, isChecked, imgValue) = images[position]
        holder.emotion_img.setImageResource(imgResId)
        holder.emotion_name.text = imgName

        holder.emotion_name.apply {
            textSize = if (isChecked) 15f else 15f
            typeface = if (isChecked) ResourcesCompat.getFont(context, R.font.pretendard_bold) else ResourcesCompat.getFont(context, R.font.pretendard_regular)
        }

        holder.itemView.setBackgroundResource(
            if (isChecked) R.drawable.alcohol_selected_bg
            else R.drawable.alcohol_unselected_bg
        )

        holder.emotion_check.visibility = if(isChecked) View.VISIBLE else View.GONE

        holder.itemView.setOnClickListener {
            // Set all isChecked to false
            for (i in images.indices) {
                images[i] = Quadruple(images[i].first, images[i].second, false, images[i].fourth)
            }

            // Set selected item's isChecked to true
            images[position] = Quadruple(imgResId, imgName, true, imgValue)

            // Update selectedPosition
            selectedPosition = position

            // Notify adapter about the data change
            notifyDataSetChanged()

            listener?.onItemClick(imgValue)
        }
    }

    override fun getItemCount(): Int = images.size
}

