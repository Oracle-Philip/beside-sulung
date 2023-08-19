package com.rummy.sulung.view.ui.diary

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.media.ExifInterface
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.github.chrisbanes.photoview.PhotoView
import com.rummy.sulung.R
import com.rummy.sulung.databinding.ActivityFullscreenImageBinding
import com.rummy.sulung.databinding.FullscreenImageItemBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.security.MessageDigest

class FullScreenImageActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var images: List<String>
    private var currentPosition: Int = 0

    lateinit var toolbar: Toolbar
    lateinit var binding: ActivityFullscreenImageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFullscreenImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        images = if (intent.hasExtra("images")) {
            intent.getStringArrayListExtra("images") ?: emptyList()
        } else {
            listOf(intent.getStringExtra("image") ?: "")
        }
        currentPosition = intent.getIntExtra("current_position", 0)

        toolbar = binding.toolbar.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbar.toolbarBack.setOnClickListener {
            onBackPressed()
        }

        viewPager = binding.imageViewPager
        viewPager.adapter = FullScreenImageAdapter(this, images)
        viewPager.setCurrentItem(currentPosition, false)
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                currentPosition = position
                updateToolbarTitle()
            }
        })

        updateToolbarTitle()
    }

    override fun onResume() {
        super.onResume()
        setStatusBarBlackAndWhiteText()
    }

    override fun onPause() {
        super.onPause()
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun setStatusBarBlackAndWhiteText() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
            window.statusBarColor = Color.BLACK
            window.navigationBarColor = Color.TRANSPARENT
        }
    }

    private fun updateToolbarTitle() {
        binding.toolbar.imagePosition.setText("${currentPosition + 1} / ${images.size}")
    }

    inner class FullScreenImageAdapter(
        private val context: Context,
        private val images: List<String>
    ) : RecyclerView.Adapter<FullScreenImageAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val imageView: PhotoView = view.findViewById(R.id.fullScreenImageView)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fullscreen_image_item, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val image = images[position]
            Glide.with(context)
                .load(image)
                //.transform(RotateTransformation(context, orientation))
                .addListener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.e("FullScreenImageAdapter", "Image loading failed", e)
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }
                })
                .into(holder.imageView)
            /*lifecycleScope.launch(Dispatchers.Main) {
                val file = downloadAndSaveImage(image)
                if (file != null) {
                    val orientation = getOrientation(file)
                    Glide.with(context)
                        .load(file)
                        //.transform(RotateTransformation(context, orientation))
                        .addListener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                Log.e("FullScreenImageAdapter", "Image loading failed", e)
                                return false
                            }

                            override fun onResourceReady(
                                resource: Drawable,
                                model: Any?,
                                target: Target<Drawable>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                return false
                            }
                        })
                        .into(holder.imageView)
                } else {
                    Log.e("FullScreenImageAdapter", "Failed to download image from $image")
                }
            }*/
        }

        override fun getItemCount(): Int {
            return images.size
        }
    }

    private suspend fun downloadAndSaveImage(uri: String): File? = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder().url(uri).build()
            val response = OkHttpClient().newCall(request).execute()
            if (!response.isSuccessful) return@withContext null

            val inputStream = response.body()?.byteStream() ?: return@withContext null
            val file = File.createTempFile("image", ".png")
            val outputStream = FileOutputStream(file)

            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }

            return@withContext file
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        }
    }

    private fun getOrientation(file: File): Int {
        val exif = ExifInterface(file.absolutePath)
        return exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL).let { orientation ->
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90
                ExifInterface.ORIENTATION_ROTATE_180 -> 180
                ExifInterface.ORIENTATION_ROTATE_270 -> 270
                else -> 0
            }
        }
    }

    class RotateTransformation(private val context: Context, private val orientation: Int) : BitmapTransformation() {

        override fun transform(
            pool: BitmapPool,
            toTransform: Bitmap,
            outWidth: Int,
            outHeight: Int
        ): Bitmap {
            val matrix = Matrix()
            matrix.postRotate(orientation.toFloat())
            return Bitmap.createBitmap(
                toTransform, 0, 0, toTransform.width, toTransform.height, matrix, true
            )
        }

        override fun updateDiskCacheKey(messageDigest: MessageDigest) {
            messageDigest.update(("rotate" + orientation).toByteArray())
        }

        override fun equals(other: Any?): Boolean {
            return other is RotateTransformation && other.orientation == orientation
        }

        override fun hashCode(): Int {
            return ("rotate$orientation").hashCode()
        }
    }
}



