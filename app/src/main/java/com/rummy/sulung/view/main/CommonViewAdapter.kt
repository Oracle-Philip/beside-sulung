package com.rummy.sulung.view.main
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rummy.sulung.databinding.ActivityMainBinding
import com.rummy.sulung.databinding.ActivityMainEmptyDataBinding

class CommonViewAdapter(
    private val binding: ActivityMainEmptyDataBinding,
    private val dataSet: ArrayList<CommonItem>
) : RecyclerView.Adapter<CommonViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommonViewHolder {
        return CommonViewHolderFactory.createViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: CommonViewHolder, position: Int) {
//        Log.e(TAG, position.toString())
//        if(position == 2){
//            binding.recordAddMain.visibility = View.INVISIBLE
//        } else{
//            binding.recordAddMain.visibility = View.VISIBLE
//        }
        holder.bind(dataSet[position])
    }

    override fun getItemCount(): Int = dataSet.size

    override fun getItemViewType(position: Int): Int {
        return ViewType.valueOf(dataSet[position].viewType).ordinal
    }
}