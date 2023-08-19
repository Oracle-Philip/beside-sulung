package com.rummy.sulung.view.ui.addRecord

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rummy.sulung.R
import com.rummy.sulung.view.ui.diary.ModifyDiaryActivity
import com.rummy.sulung.view.ui.diary.WritingDiaryActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

class ImageAdapter(val images: MutableList<Any>) : RecyclerView.Adapter<ImageAdapter.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_ADD_IMAGE = 0
        private const val VIEW_TYPE_IMAGE = 1
    }

    interface OnImageDeleteListener {
        fun onImageDeleted(position: Int)
    }

    private var onImageDeleteListener: OnImageDeleteListener? = null
    private var onImageAddListener: ((position: Int) -> Unit)? = null

    fun setOnImageDeleteListener(listener: OnImageDeleteListener) {
        this.onImageDeleteListener = listener
    }

    fun setOnImageAddListener(listener: (position: Int) -> Unit) {
        this.onImageAddListener = listener
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView? = itemView.findViewById(R.id.item_image_view)
        val deleteIcon: ImageView? = itemView.findViewById(R.id.item_delete_icon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = if (viewType == VIEW_TYPE_ADD_IMAGE) {
            LayoutInflater.from(parent.context).inflate(R.layout.item_add_image, parent, false)
        } else {
            LayoutInflater.from(parent.context).inflate(R.layout.select_picture_layout, parent, false)
        }
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (getItemViewType(position) == VIEW_TYPE_ADD_IMAGE) {
            holder.imageView?.setOnClickListener {
                onImageAddListener?.invoke(position)
            }
        } else {
            val image = images[position]

            when (image) {
                is Uri -> Glide.with(holder.imageView!!.context).load(image).into(holder.imageView!!)
                is String -> Glide.with(holder.imageView!!.context).load(image).into(holder.imageView!!)
            }

            holder.imageView?.setOnClickListener {
                onImageAddListener?.invoke(position)
            }

            holder.deleteIcon?.setOnClickListener {
                onImageDeleteListener?.onImageDeleted(position)
            }
        }
    }

    override fun getItemCount(): Int = images.size + 1

    override fun getItemViewType(position: Int): Int {
        return if (position < images.size) VIEW_TYPE_IMAGE else VIEW_TYPE_ADD_IMAGE
    }

    fun updateImage(position: Int, newImage: Any) {
        if (position >= 0 && position < images.size) {
            images[position] = newImage
            notifyItemChanged(position)
        }
    }

    fun addImage(image: Any) {
        images.add(image)
        notifyDataSetChanged()
        updateAddImageVisibility()
    }

    fun deleteImage(position: Int) {
        images.removeAt(position)
        notifyDataSetChanged()
        updateAddImageVisibility()
    }

    private fun updateAddImageVisibility() {
        if (images.size >= 3) {
            notifyItemRemoved(images.size) // add_picture 이미지의 위치를 알림
        } else {
            notifyItemInserted(images.size) // add_picture 이미지의 위치를 알림
        }
    }

    fun addImagesFromUrls(imageUrls: List<String?>) {
        for (imageUrl in imageUrls) {
            if (imageUrl != null) {
                images.add(imageUrl)
            }
        }
        notifyDataSetChanged()
        updateAddImageVisibility()
    }

    suspend fun downloadImage(url: String): Bitmap? = withContext(Dispatchers.IO) {
        return@withContext try {
            val inputStream = URL(url).openStream() // URL로부터 InputStream을 가져옵니다.
            BitmapFactory.decodeStream(inputStream) // InputStream으로부터 Bitmap을 생성합니다.
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
