package com.rummy.sulung.view.ui.diary

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.rummy.sulung.R
import com.rummy.sulung.view.ViewPagerAdapter
import jp.wasabeef.glide.transformations.RoundedCornersTransformation

class Detail_Diary_ViewPager(private val context: Context, private val images: List<String>) : RecyclerView.Adapter<Detail_Diary_ViewPager.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
        val progressBar: ProgressBar = view.findViewById(R.id.progressBar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.detail_diary_image_viewpager, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.progressBar.visibility = View.VISIBLE

        Glide.with(context)
            .asBitmap()
            .load(images[position])
            .centerCrop() // 이미지의 스케일 유형을 변경
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    val roundedBitmap = getRoundedCornerBitmap(resource, 8 * context.resources.displayMetrics.density)
                    holder.imageView.setImageBitmap(roundedBitmap)
                    holder.progressBar.visibility = View.GONE
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    holder.progressBar.visibility = View.GONE
                }
            })

        holder.imageView.setOnClickListener {
            val intent = Intent(context, FullScreenImageActivity::class.java)
            if (images.size == 1) {
                intent.putExtra("image", images[0])
            } else {
                intent.putStringArrayListExtra("images", images as ArrayList<String>)
                intent.putExtra("current_position", position) // 클릭한 이미지의 인덱스를 전달
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return images.size
    }

    private fun getRoundedCornerBitmap(bitmap: Bitmap, cornerRadius: Float): Bitmap {
        val output = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)

        val paint = Paint()
        val rect = Rect(0, 0, bitmap.width, bitmap.height)
        val rectF = RectF(rect)
        val roundPx = cornerRadius

        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = Color.BLACK
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint)

        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)

        return output
    }
}