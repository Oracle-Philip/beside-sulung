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
import kotlin.math.roundToInt

class Alcohol_Image_Adapter(private val images: MutableList<Quadruple<Int, String, Boolean, Int>>, private val itemWidth: Int) :
    RecyclerView.Adapter<Alcohol_Image_Adapter.ViewHolder>()  {

    var listener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    private var selectedPosition: Int = -1

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val alcohol_img: ImageView = itemView.findViewById(R.id.alcohol_img)
        val alcohol_name: TextView = itemView.findViewById(R.id.alcohol_name)
        val alcohol_check: ImageView = itemView.findViewById(R.id.alcohol_check)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Alcohol_Image_Adapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_alcohol_layout, parent, false)
        val layoutParams = view.layoutParams
        layoutParams.width = itemWidth
        view.layoutParams = layoutParams
        return ViewHolder(view)
    }
    /*override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_alcohol_layout, parent, false)
        val layoutParams = view.layoutParams

        // 여기에서 현재 아이템의 column 위치를 계산하여 적절한 extraSpacing 값을 적용하도록 합니다.
        val column = viewType % itemCountPerRow

        // itemWidth를 반올림한 후 Int로 변환합니다.
        val itemWidthInt = itemWidth.roundToInt()

        layoutParams.width = itemWidthInt + if (column < extraSpacing.size) extraSpacing[column] else 0
        view.layoutParams = layoutParams
        return ViewHolder(view)
    }*/

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val (imgResId, imgName, isChecked, imgValue) = images[position]
        holder.alcohol_img.setImageResource(imgResId)
        holder.alcohol_name.text = imgName

        holder.alcohol_name.apply {
            textSize = if (isChecked) 15f else 15f
            typeface = if (isChecked) ResourcesCompat.getFont(context, R.font.pretendard_bold) else ResourcesCompat.getFont(context, R.font.pretendard_regular)
        }

        holder.itemView.setBackgroundResource(
            if (isChecked) R.drawable.alcohol_selected_bg
            else R.drawable.alcohol_unselected_bg
        )

        holder.alcohol_check.visibility = if(isChecked) View.VISIBLE else View.GONE

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

