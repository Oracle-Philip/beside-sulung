package com.rummy.sulung.view.main

import android.content.Intent
import com.rummy.sulung.common.App
import com.rummy.sulung.databinding.ActivityMain3Binding
import com.rummy.sulung.view.ui.addRecord.RecordActivity
import com.rummy.sulung.view.ui.addRecord.RecordActivity_replace
import com.rummy.sulung.view.ui.addRecord.RecordActivity_replace.Companion.FIRST_RECORD

class MainViewHolder3(
    private val binding: ActivityMain3Binding
) : CommonViewHolder(binding){
    override fun bind(item: CommonItem) {
        binding.mainBtn.setOnClickListener {
            val intent = Intent(binding.root.context, RecordActivity_replace::class.java)
            intent.putExtra(FIRST_RECORD, true)
            binding.root.context.startActivity(intent)
        }
    }
}