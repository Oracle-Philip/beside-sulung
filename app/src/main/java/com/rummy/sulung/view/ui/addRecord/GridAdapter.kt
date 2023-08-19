package com.rummy.sulung.view.ui.addRecord

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.rummy.sulung.R

class GridAdapter(private val context: Context, private val itemList: List<Item>) : BaseAdapter() {

    private var selectedItem: Int = -1
    private var onItemClickListener: OnItemClickListener? = null

    override fun getCount(): Int {
        return itemList.size
    }

    override fun getItem(position: Int): Any {
        return itemList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var itemView = convertView
        val holder: ViewHolder

        if (itemView == null) {
            itemView = LayoutInflater.from(context).inflate(R.layout.item_alcohol_layout, parent, false)

            holder = ViewHolder()
            holder.imageView = itemView?.findViewById(R.id.alcohol_img)!!
            holder.textView = itemView?.findViewById(R.id.alcohol_name)!!
            holder.checkView = itemView?.findViewById(R.id.alcohol_check)!!
            itemView?.tag = holder
        } else {
            holder = itemView.tag as ViewHolder
        }

        val item = itemList[position]

        // 아이템뷰의 이미지와 텍스트를 동적으로 변경합니다.
        holder.imageView.setImageResource(item.imageResId)
        holder.textView.text = item.name

        // 아이템뷰의 클릭 이벤트를 처리합니다.
        itemView?.setOnClickListener {
            if (selectedItem == position) {
                // 선택된 상태에서 다시 클릭한 경우 선택 상태 해제
                selectedItem = -1
            } else {
                // 선택되지 않은 항목을 클릭한 경우 선택 상태로 변경
                selectedItem = position
            }

            onItemClickListener?.onItemClick(selectedItem, item)
            notifyDataSetChanged()
        }

        // 선택된 항목과 현재 항목이 같은 경우 선택된 상태 스타일을 적용하고, 아니면 일반 스타일을 적용합니다.
        if (selectedItem == position) {
            holder.imageView.setBackgroundResource(R.drawable.alcohol_selected_bg)
        } else {
            holder.imageView.setBackgroundResource(R.drawable.alcohol_selected_bg)
        }

        return itemView!!
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int, item: Item)
    }

    private class ViewHolder {
        lateinit var imageView: ImageView
        lateinit var textView: TextView
        lateinit var checkView: ImageView
    }
}
