package com.rummy.sulung.view.ui.diary

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Outline
import android.graphics.drawable.Drawable

import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.bitmap.TransformationUtils
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.rummy.sulung.R
import com.rummy.sulung.common.DiaryImage.setDrinkImg
import com.rummy.sulung.common.TimestampConverter
import com.rummy.sulung.databinding.DiaryListItemBinding
import com.rummy.sulung.network.response.Diary
import com.rummy.sulung.network.response.DisplayableItem
import com.rummy.sulung.view.ui.diary.Detail_Diary_Activity.Companion.RECORD_ID
import java.security.MessageDigest

class DiaryViewHolder(binding: DiaryListItemBinding) : RecyclerView.ViewHolder(binding.root) {
    var view: DiaryListItemBinding
    init{
        view = binding
    }

    fun bind(diaryData: DisplayableItem?){
        if (diaryData != null) {
            diaryData as Diary

            setImageAndHeight(diaryData, itemView)

            view.id.text = diaryData.id.toString()

            view.drinkImg.setImageResource(
                setDrinkImg(
                    diaryData.emotion!!,
                    diaryData.drinkType!!
                )
            )
            view.date.text = TimestampConverter.MillToDate(diaryData?.diaryDt!!)

            /**
             * @date QA 0527
             * @desc content가 null일 경우 기본 디폴트 문구 나오게 처리
             */
            view.content.text = run {
                val content = diaryData.content
                setContentText(if (content == "null" || content.isNullOrBlank() || content.isNullOrEmpty()) "${view.root.resources.getStringArray(R.array.emotion_array)[diaryData.emotion ?: 0]} 기분으로 " +
                        "${view.root.resources.getStringArray(R.array.drink_array)[diaryData.drinkType ?: 0]} " +
                        "마셨어요" else content)
            }

            /**
             * @date QA 0522
             * @desc 감정,술 기본태그 적용
             */
            val tag = diaryData?.tag
            val tagList = tag?.split(',')?.distinct()?.toList() ?: emptyList()
            if (tagList.isNotEmpty()) {
                view.hashTag.post {
                    view.hashTag.removeAllViews()
                    var availableWidth = view.hashTag.width
                    val layoutParams = view.hashTag.layoutParams as ViewGroup.MarginLayoutParams
                    availableWidth -= (layoutParams.leftMargin + layoutParams.rightMargin)

                    var currentChipGroup = createChipGroup(view.root.context)
                    view.hashTag.addView(currentChipGroup)

                    var lineBreakCount = 0 // 추가: 줄 개행 횟수 추적
                    // 부모 뷰의 가용 너비 계산
                    val parentWidth = (view.contentLayout.width - view.contentLayout.paddingStart - view.contentLayout.paddingEnd)
                    var index = 0

                    run loop@{
                        for (tag in tagList) {
                            if (tag.isNotEmpty()) {
                                index ++
                                val chip = createChip(view.root.context, tag.trim('"'), diaryData.emotion)
                                chip.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)

                                // 현재 Chip을 추가할 수 있는지 확인
                                val canAddCurrentChip = chip.measuredWidth <= availableWidth

                                if (canAddCurrentChip) {
                                    currentChipGroup.addView(chip)
                                    availableWidth -= (chip.measuredWidth + 1)
                                } else {
                                    // 개행이 필요한 경우
                                    lineBreakCount += 1 // 개행 횟수 증가

                                    if (lineBreakCount >= 1 || chip.measuredWidth > parentWidth) {
                                        // 이미 2번째 줄 개행이 일어날 경우 또는 칩이 부모 뷰를 침범하거나 잘릴 경우 작업 중지
                                        val moreChip = createMoreChip(view.root.context, diaryData.emotion)
                                        currentChipGroup.addView(moreChip)
                                        break // 여기서 break를 사용하여 전체 loop를 종료합니다.
                                    }

                                    // 새로운 ChipGroup 생성 및 추가
                                    currentChipGroup = createChipGroup(view.root.context)
                                    view.hashTag.addView(currentChipGroup)

                                    // 줄 개행이 일어날 때만 hashTag의 marginTop을 24dp가 아닌 10dp로 설정
                                    val topMargin = if (lineBreakCount == 1) {
                                        if (diaryData.imageUrls.size > 0) 0
                                        else 0
                                    } else {
                                        20
                                    }
                                    layoutParams.topMargin = topMargin.dpToPx(view.root.context)
                                    view.hashTag.layoutParams = layoutParams

                                    availableWidth = parentWidth

                                    // 새로운 ChipGroup에 현재 Chip 추가
                                    currentChipGroup.addView(chip)
                                    availableWidth -= (chip.measuredWidth + 1)
                                }
                            }
                        }
                    }
                }
            }

            /**
             *  @author ysp
             *  @date 04 22 2023
             *  @desc 뷰홀더 클릭리스너
             */
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, Detail_Diary_Activity::class.java)
                intent.putExtra(RECORD_ID, diaryData.id)
                if (itemView.context is Activity) {
                    (itemView.context as Activity).startActivityForResult(intent, 100)
                } else {
                    itemView.context.startActivity(intent)
                }
            }
        }
    }

    private fun createChip(context: Context, tag: String, emotion: Int): Chip {
        val chip = LayoutInflater.from(context).inflate(R.layout.chip_item_detail_diary_store_list, view.hashTag, false) as Chip
        chip.text = "#$tag"

        val widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        val heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        chip.measure(widthMeasureSpec, heightMeasureSpec)

        // 추가: 마진 설정
        val layoutParams = chip.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.setMargins(0, 0, 0, 0) // 기존 마진 제거
        chip.layoutParams = layoutParams

        chip.setTextColor(ContextCompat.getColor(context, when (emotion) {
            0 -> R.color.happy_primary
            1 -> R.color.sad_primary
            2 -> R.color.gloom_primary
            3 -> R.color.angry_primary
            4 -> R.color.calm_primary
            5 -> R.color.congratulate_primary
            6 -> R.color.drunk_primary
            else -> R.color.happy_primary // 기본값
        }))
        chip.setChipBackgroundColorResource(when (emotion) {
            0 -> R.color.happy_light
            1 -> R.color.sad_light
            2 -> R.color.gloom_light
            3 -> R.color.angry_light
            4 -> R.color.calm_light
            5 -> R.color.congratulate_light
            6 -> R.color.drunk_light
            else -> R.color.happy_light // 기본값
        })
        return chip
    }

    private fun createMoreChip(context: Context, emotion: Int): Chip {
        val chip = LayoutInflater.from(context).inflate(R.layout.chip_item_detail_diary_store_list, view.hashTag, false) as Chip
        chip.text = "..."

        val widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        val heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        chip.measure(widthMeasureSpec, heightMeasureSpec)

        // 추가: 마진 설정
        val layoutParams = chip.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.setMargins(0, 0, 0, 0) // 기존 마진 제거
        chip.layoutParams = layoutParams

        chip.setTextColor(ContextCompat.getColor(context, when (emotion) {
            0 -> R.color.happy_primary
            1 -> R.color.sad_primary
            2 -> R.color.gloom_primary
            3 -> R.color.angry_primary
            4 -> R.color.calm_primary
            5 -> R.color.congratulate_primary
            6 -> R.color.drunk_primary
            else -> R.color.happy_primary // 기본값
        }))
        chip.setChipBackgroundColorResource(when (emotion) {
            0 -> R.color.happy_light
            1 -> R.color.sad_light
            2 -> R.color.gloom_light
            3 -> R.color.angry_light
            4 -> R.color.calm_light
            5 -> R.color.congratulate_light
            6 -> R.color.drunk_light
            else -> R.color.happy_light // 기본값
        })
        return chip
    }

    fun Int.dpToPx(context: Context): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), context.resources.displayMetrics).toInt()
    }

    private fun createChipGroup(context: Context, marginTop: Int = 4): ChipGroup {
        val chipGroup = ChipGroup(context)
        val layoutParams = ViewGroup.MarginLayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(0, marginTop.dpToPx(context), 0, 0)
        chipGroup.layoutParams = layoutParams
        chipGroup.chipSpacingVertical = 4.dpToPx(context) // 칩 사이의 수직 간격 설정
        return chipGroup
    }
    fun setContentText(content: String): String {
        val maxLength = 45
        val maxLines = 2
        val trimmedContent = if (content.length > maxLength) {
            content.substring(0, maxLength)
        } else {
            content
        }

        val lines = trimmedContent.split("\n")
        return if (lines.size > maxLines) {
            val truncatedLines = lines.subList(0, maxLines)
            val firstLine = truncatedLines[0]
            val secondLine = truncatedLines[1]
            if (secondLine.length > maxLength / maxLines - 5) {
                firstLine + "\n" + secondLine.substring(0, maxLength / maxLines - 5) + "..."
            } else {
                firstLine + "\n" + secondLine
            }
        } else {
            trimmedContent
        }
    }
    fun setImageAndHeight(diaryData: Diary, itemView: View) {
        val layoutParams = itemView.layoutParams
        val hasImages = diaryData.imageUrls.isNotEmpty()

        // Update divider height and margins
        val divider = itemView.findViewById<View>(R.id.divider)
        val dividerLayoutParams = divider.layoutParams as LinearLayout.LayoutParams

        // Update drinkImg layout
        val drinkImg = itemView.findViewById<ImageView>(R.id.drinkImg)
        val drinkImgLayoutParams = drinkImg.layoutParams as LinearLayout.LayoutParams

        // Update myImg layout
        val myImg = itemView.findViewById<ImageView>(R.id.myImg)
        val myImgLayoutParams = myImg.layoutParams as LinearLayout.LayoutParams

        val hashTag = itemView.findViewById<LinearLayout>(R.id.hashTag)
        val myHashTagLayoutParams = hashTag.layoutParams as LinearLayout.LayoutParams

        if (hasImages) {
            layoutParams.height = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                278f,
                itemView.resources.displayMetrics
            ).toInt()

            // Update drinkImg layout
            drinkImgLayoutParams.gravity = Gravity.TOP
            drinkImgLayoutParams.topMargin = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                29f,
                itemView.resources.displayMetrics
            ).toInt()

            // Update myImg layout
            myImgLayoutParams.gravity = Gravity.START
            myImg.scaleType = ImageView.ScaleType.CENTER_CROP
            myImgLayoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT
            myImgLayoutParams.topMargin = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                16f,
                itemView.resources.displayMetrics
            ).toInt()

            /*val imageUrl = diaryData.imageUrls.firstOrNull()
            if (imageUrl != null) {
                myImg.visibility = View.VISIBLE
                val displayMetrics = itemView.context.resources.displayMetrics
                val density = displayMetrics.density
                val screenWidth = displayMetrics.widthPixels / displayMetrics.density
                val relativeRoundedCornersSize = (screenWidth * 0.01).toInt() // Adjust this value as needed
                Glide.with(itemView.context)
                    .load(imageUrl)
                    .transform(CenterCrop(), RoundedCorners(relativeRoundedCornersSize))
                    .into(object : CustomTarget<Drawable>() {
                        override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                            myImg.setImageDrawable(resource)
                            myImg.scaleType = ImageView.ScaleType.CENTER_CROP
                            // Update myImg layout
                            val myImgLayoutParams = myImg.layoutParams as LinearLayout.LayoutParams
                            val newHeight = 110 * density
                            myImgLayoutParams.height = newHeight.toInt()
                            myImg.layoutParams = myImgLayoutParams
                        }
                        override fun onLoadCleared(placeholder: Drawable?) {
                        }
                    })*/

            val imageUrl = diaryData.imageUrls.firstOrNull()
            if (imageUrl != null) {
                myImg.visibility = View.VISIBLE
                val density = itemView.context.resources.displayMetrics.density
                val cornerRadius = (4 * density).toInt()

                Glide.with(itemView.context)
                    .load(imageUrl)
                    .transform(CenterCrop(), RoundedCorners(cornerRadius))
                    .into(object : CustomTarget<Drawable>() {
                        override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                            myImg.setImageDrawable(resource)
                            myImg.scaleType = ImageView.ScaleType.CENTER_CROP

                            // Update myImg layout
                            val myImgLayoutParams = myImg.layoutParams as LinearLayout.LayoutParams
                            val newHeight = (110 * density).toInt()
                            myImgLayoutParams.height = newHeight
                            myImg.layoutParams = myImgLayoutParams

                            // Apply rounded corners programmatically
                            val outlineProvider = object : ViewOutlineProvider() {
                                override fun getOutline(view: View, outline: Outline) {
                                    outline.setRoundRect(0, 0, view.width, view.height, cornerRadius.toFloat())
                                }
                            }
                            myImg.outlineProvider = outlineProvider
                            myImg.clipToOutline = true
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                        }
                    })
            /*val imageUrl = diaryData.imageUrls.firstOrNull()
            if (imageUrl != null) {
                myImg.visibility = View.VISIBLE
                val density = itemView.context.resources.displayMetrics.density
                Glide.with(itemView.context)
                    .load(imageUrl)
                    .transform(CenterCrop(), RoundedCorners(4 * density.toInt()))
                    .into(object : CustomTarget<Drawable>() {
                        override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                            myImg.setImageDrawable(resource)
                            myImg.scaleType = ImageView.ScaleType.CENTER_CROP
                            // Update myImg layout
                            val myImgLayoutParams = myImg.layoutParams as LinearLayout.LayoutParams
                            val newHeight = 110 * itemView.context.resources.displayMetrics.density
                            myImgLayoutParams.height = newHeight.toInt()
                            myImg.layoutParams = myImgLayoutParams
                        }
                        override fun onLoadCleared(placeholder: Drawable?) {
                        }
                    })*/

                // Set divider height and margins
                dividerLayoutParams.gravity = Gravity.CENTER
                dividerLayoutParams.height = layoutParams.height - TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    40f,
                    itemView.resources.displayMetrics
                ).toInt()
            }
        } else {
            layoutParams.height = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                148f,
                itemView.resources.displayMetrics
            ).toInt()

            // Update drinkImg layout
            drinkImgLayoutParams.gravity = Gravity.CENTER
            drinkImgLayoutParams.topMargin = 0

            // Update myImg layout
            myImgLayoutParams.height = 0
            myImg.visibility = View.GONE

            // Set divider height and margins
            dividerLayoutParams.gravity = Gravity.CENTER
            dividerLayoutParams.height = layoutParams.height - TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                40f,
                itemView.resources.displayMetrics
            ).toInt()
        }

        // Apply changes
        itemView.layoutParams = layoutParams
        divider.layoutParams = dividerLayoutParams
        drinkImg.layoutParams = drinkImgLayoutParams
        myImg.layoutParams = myImgLayoutParams
        hashTag.layoutParams = myHashTagLayoutParams

        // Request layout update
        itemView.requestLayout()
    }

    companion object{
        fun create(parent: ViewGroup): DiaryViewHolder {
            val view = DiaryListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return DiaryViewHolder(view)
        }
    }
}