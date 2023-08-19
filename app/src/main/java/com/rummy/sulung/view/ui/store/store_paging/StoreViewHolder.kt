package com.rummy.sulung.view.ui.store.store_paging

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rummy.sulung.common.DiaryImage
import com.rummy.sulung.databinding.DiaryListItemBinding
import com.rummy.sulung.databinding.StoreListItemBinding
import com.rummy.sulung.network.response.DateSeparator
import com.rummy.sulung.network.response.Diary
import com.rummy.sulung.network.response.DisplayableItem
import com.rummy.sulung.network.response.Item
import com.rummy.sulung.view.ui.diary.Detail_Diary_Activity
import com.rummy.sulung.view.ui.diary.Detail_Diary_Activity.Companion.RECORD_ID

class StoreViewHolder(binding: StoreListItemBinding) : RecyclerView.ViewHolder(binding.root) {
    var view: StoreListItemBinding
    init{
        view = binding
    }

    fun bind(storeDiary: DisplayableItem?, position: Int){
        if (storeDiary != null) {
            if (storeDiary is Diary) {
                view.position.text = position.toString()
                view.id.text = storeDiary.id.toString()
                view.drinkImg.setImageResource(
                    DiaryImage.setDrinkImg(
                        storeDiary.emotion!!,
                        storeDiary.drinkType!!
                    )
                )
                /**
                 *  @author ysp
                 *  @date 04 22 2023
                 *  @desc 뷰홀더 클릭리스너
                 */
                itemView.setOnClickListener {
                    val intent = Intent(itemView.context, Detail_Diary_Activity::class.java)
                    intent.putExtra(RECORD_ID, storeDiary.id)
                    if (itemView.context is Activity) {
                        (itemView.context as Activity).startActivityForResult(intent, 100)
                    } else {
                        itemView.context.startActivity(intent)
                    }
                }
            }
        }
    }

    companion object{
        fun create(parent: ViewGroup): StoreViewHolder {
            val view = StoreListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return StoreViewHolder(view)
        }
    }
}