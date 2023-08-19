package com.rummy.sulung.view.ui.diary

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupWindow
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.rummy.sulung.R
import com.rummy.sulung.common.App
import com.rummy.sulung.view.ui.addRecord.ImageAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.*
import java.lang.Float
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

abstract class DiaryBaseActivity : AppCompatActivity() {

    var editingChip: Chip? = null // 현재 수정 중인 칩 버튼
    var imageBody = mutableListOf<MultipartBody.Part?>()
    var photoUri : Uri? = null

    val TAG = ModifyDiaryActivity::class.java.simpleName

    var currentPhotoPath: String? = null

    var selectdate : Long? = null

    var diaryDt : Long = -100L
    var id : Int = -100
    var drinkType = -100
    var emotion = -100

    var drinkCount = 1.5

    // 스피너에서 선택한 값 저장할 변수
    var selectedDrinkUnitValue: String = "병"

    val selectKeyword = LinkedHashSet<String>()

    var selectedPosition: Int = -1

    lateinit var imageAdapter: ImageAdapter

    val alchohol_list = mutableListOf(
        Triple("소주", false, 0),
        Triple("맥주", false, 1),
        Triple("막걸리", false, 2),

        Triple("칵테일", false, 4),
        Triple("사케", false, 6),
        Triple("하이볼", false, 8),

        Triple("샴페인", false, 7),
        Triple("양주", false, 3),
        Triple("와인", false, 5),
    )

    val emotion_list = mutableListOf(
        Triple("평온", false, 4),
        Triple("우울", false, 2),
        Triple("화남", false, 3),
        Triple("슬픔", false, 1),
        Triple("기쁨", false, 0),
        Triple("축하", false, 5),
        Triple("취함", false, 6)
    )

    var launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK && selectedPosition != -1) {
            val imagePath = result.data?.data ?: photoUri

            if (selectedPosition < imageAdapter.itemCount - 1) {
                val file = File(absolutelyPath(imagePath, this))
                val compressedFile = createCompressedImageFile(file.path, 1000, 1000, 80, this)
                val requestFile = RequestBody.create(MediaType.parse("image/*"), compressedFile)
                val part = MultipartBody.Part.createFormData("imageFiles", file.name, requestFile)

                imageBody[selectedPosition] = part // 기존 이미지를 업데이트
                imageAdapter.images[selectedPosition] = imagePath!!
            } else {
                val file = File(absolutelyPath(imagePath, this))
                val compressedFile = createCompressedImageFile(file.path, 1000, 1000, 80, this)
                val requestFile = RequestBody.create(MediaType.parse("image/*"), compressedFile)
                val part = MultipartBody.Part.createFormData("imageFiles", file.name, requestFile)

                imageBody.add(part) // 새 이미지를 추가
                imageAdapter.addImage(imagePath!!)
            }
            imageAdapter.notifyDataSetChanged()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

/*    protected fun dpToPx(dp: Int): Int {
        val density = Resources.getSystem().displayMetrics.density
        return (dp * density).roundToInt()
    }*/

    /*protected fun requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, arrayOf(
                Manifest.permission.CAMERA
            ), 12)
        }
    }*/
    protected fun requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(
                Manifest.permission.CAMERA
            ), 12)
        } else {
            val chooserIntent = createImageSelectionOrCameraIntent()
            launcher.launch(chooserIntent)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 12) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val chooserIntent = createImageSelectionOrCameraIntent()
                launcher.launch(chooserIntent)
            } else {
                // 권한이 거부된 경우 처리
            }
        }
    }

    protected fun createImageSelectionOrCameraIntent(): Intent {
        val imageSelectionIntent = createImageSelectionIntent()
        val cameraIntent = createCameraIntent()

        val chooserIntent = Intent.createChooser(imageSelectionIntent, "사용할 앱을 선택해주세요.")
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(cameraIntent))

        return chooserIntent
    }

    protected fun createImageSelectionIntent(): Intent {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        return intent
    }

    protected fun createCameraIntent(): Intent {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            photoUri = createTempImageUri()
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        }
        return intent
    }

    protected fun createTempImageUri(): Uri {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "temp_image_$timeStamp.jpg"
        val storageDir: File = cacheDir
        val tempFile = File.createTempFile(imageFileName, ".jpg", storageDir)
        return FileProvider.getUriForFile(this, "${packageName}.fileprovider", tempFile)
    }

    @SuppressLint("IntentReset")
    protected fun getProFileImage() {
        Log.e(App.TAG, "사진변경 호출")
        val chooserIntent = Intent(Intent.ACTION_CHOOSER)
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        chooserIntent.putExtra(Intent.EXTRA_INTENT, intent)
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "사용할 앱을 선택해주세요.")
        launcher.launch(chooserIntent)
    }

    // 절대경로 변환
    @SuppressLint("Recycle")
    protected fun absolutelyPath(uri: Uri?, context: Context): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            uri?.let { getRealPathFromUri(context, it) } ?: ""
        } else {
            val proj: Array<String> = arrayOf(MediaStore.Images.Media.DATA)
            val c: Cursor? = context.contentResolver.query(uri!!, proj, null, null, null)
            val index = c?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            c?.moveToFirst()
            val result = c?.getString(index!!)
            result!!
        }
    }

    protected fun createTempFile(context: Context): File? {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"

        // 이미지 파일 생성
        val storageDir: File = context.cacheDir
        val imageFile = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",          /* suffix */
            storageDir       /* directory */
        )

        return imageFile
    }

    protected fun getRealPathFromUri(context: Context, contentUri: Uri): String {
        var inputStream: InputStream? = null
        var filePath = ""

        try {
            inputStream = context.contentResolver.openInputStream(contentUri)
            val photoFile: File? = createTempFile(context)
            filePath = photoFile?.path ?: ""

            val outputStream = FileOutputStream(photoFile)
            inputStream?.copyTo(outputStream)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            inputStream?.close()
        }
        return filePath
    }

    //load image
    protected fun loadImageAsBitmap(imagePath: String, context: Context): Bitmap? {
        return BitmapFactory.decodeFile(imagePath)
    }

    //resige image
    protected fun resizeBitmap(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val scaleWidth = maxWidth.toFloat() / width
        val scaleHeight = maxHeight.toFloat() / height
        val scaleFactor = Float.min(scaleWidth, scaleHeight)

        val matrix = Matrix()
        matrix.postScale(scaleFactor, scaleFactor)

        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)
    }

    //compressAndSaveBitmap
    protected fun compressAndSaveBitmap(bitmap: Bitmap, quality: Int, context: Context): File? {
        val compressedFile = createTempFile(context)
        val outputStream = FileOutputStream(compressedFile)

        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        outputStream.close()

        return compressedFile
    }

    /*//createCompressedImageFile
    protected fun createCompressedImageFile(imagePath: String, maxWidth: Int, maxHeight: Int, quality: Int, context: Context): File {
        val originalBitmap = loadImageAsBitmap(imagePath, context)
        val resizedBitmap = originalBitmap?.let { resizeBitmap(it, maxWidth, maxHeight) }
        return resizedBitmap?.let { compressAndSaveBitmap(it, quality, context) } ?: File(imagePath)
    }*/

    fun fixRotation(imagePath: String, bitmap: Bitmap): Bitmap {
        val exif = ExifInterface(imagePath)
        val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)

        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270f)
            else -> bitmap
        }
    }

    fun rotateImage(source: Bitmap, angle: kotlin.Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    /*protected fun createCompressedImageFile(imagePath: String, maxWidth: Int, maxHeight: Int, quality: Int, context: Context): File? {
        val originalBitmap = loadImageAsBitmap(imagePath, context)
        val fixedBitmap = fixRotation(imagePath, originalBitmap!!) // 회전된 이미지를 수정
        val resizedBitmap = resizeBitmap(fixedBitmap, maxWidth, maxHeight) // 수정된 이미지를 리사이즈
        return compressAndSaveBitmap(resizedBitmap, quality, context) // 리사이즈된 이미지를 압축 및 저장
    }*/
    protected fun createCompressedImageFile(imagePath: String, maxWidth: Int, maxHeight: Int, quality: Int, context: Context): File? {
        val originalBitmap = loadImageAsBitmap(imagePath, context)
        val fixedBitmap = fixRotation(imagePath, originalBitmap!!) // 회전된 이미지를 수정
        val resizedBitmap =
            fixedBitmap?.let { resizeBitmap(it, maxWidth, maxHeight) } // 수정된 이미지를 리사이즈
        val compressedFile =
            resizedBitmap?.let { compressAndSaveBitmap(it, quality, context) } // 리사이즈된 이미지를 압축 및 저장

        // 압축 및 저장한 이미지 파일에 원본 이미지의 EXIF 데이터를 복사
        /*if (compressedFile != null) {
            keepExifData(imagePath, compressedFile.path)
        }*/

        return compressedFile
    }

    fun keepExifData(originalImagePath: String, newImagePath: String) {
        val oldExif = ExifInterface(originalImagePath)

        val newExif = ExifInterface(newImagePath)
        val orientation = oldExif.getAttribute(ExifInterface.TAG_ORIENTATION)
        if (orientation != null) {
            newExif.setAttribute(ExifInterface.TAG_ORIENTATION, orientation)
        }

        newExif.saveAttributes()
    }

    protected suspend fun downloadImage(url: String): Bitmap? = withContext(Dispatchers.IO) {
        return@withContext try {
            val inputStream = URL(url).openStream() // URL로부터 InputStream을 가져옵니다.
            BitmapFactory.decodeStream(inputStream) // InputStream으로부터 Bitmap을 생성합니다.
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    protected fun showPopup(parent : View) {
        val popupView = LayoutInflater.from(this).inflate(R.layout.modify_complete_popup, null)
        val popupWindow = PopupWindow(popupView, 300.dpToPx(), 220.dpToPx())
        //val popupWindow = PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        // 팝업의 배경 색상과 외곽선 설정
        popupWindow.setBackgroundDrawable(resources.getDrawable(R.drawable.bg_rounded_border3, null))
        popupWindow.elevation = 10F

        // 팝업의 크기와 위치를 조정
        popupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0)

        // 팝업 외부 영역 터치 불가능 설정
        popupWindow.setOutsideTouchable(false)

        // 팝업 뷰 내부의 위젯에 이벤트 핸들러 등록
        with(popupView){
            findViewById<ImageView>(R.id.exit).setOnClickListener {
                popupWindow.dismiss()
                finish()
            }
            findViewById<AppCompatButton>(R.id.confirm).setOnClickListener {
                popupWindow.dismiss()
                finish()
            }
        }
    }

/*    protected fun setViewMarginTopInPx(view: View, marginTopInPx: Int) {
        val layoutParams = view.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.setMargins(layoutParams.leftMargin, marginTopInPx, layoutParams.rightMargin, layoutParams.bottomMargin)
        view.layoutParams = layoutParams
    }

    protected fun setViewMarginBottomInPx(view: View, marginBottomInPx: Int) {
        val layoutParams = view.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.setMargins(layoutParams.leftMargin, layoutParams.topMargin, layoutParams.rightMargin, marginBottomInPx)
        view.layoutParams = layoutParams
    }*/
    protected fun View.setMarginTopBottomInDp(marginTopInPx: Int, marginBottomInPx: Int) {
        val layoutParams = this.layoutParams as ViewGroup.MarginLayoutParams
        val marginTopInDp = marginTopInPx.pxToDp()
        val marginBottomInDp = marginBottomInPx.pxToDp()

        Log.d("Margins", "Before - Top: ${layoutParams.topMargin}, Bottom: ${layoutParams.bottomMargin}")
        layoutParams.setMargins(layoutParams.leftMargin, marginTopInDp, layoutParams.rightMargin, marginBottomInDp)
        this.layoutParams = layoutParams
        Log.d("Margins", "After - Top: ${layoutParams.topMargin}, Bottom: ${layoutParams.bottomMargin}")
    }
    protected fun View.setMarginTopInPx(marginTopInPx: Int) {
        val layoutParams = this.layoutParams as ViewGroup.MarginLayoutParams
        val marginTopInDp = marginTopInPx.pxToDp()

        layoutParams.setMargins(layoutParams.leftMargin, marginTopInDp, layoutParams.rightMargin, layoutParams.bottomMargin)
        this.layoutParams = layoutParams
    }

    protected fun View.setMarginBottomInPx(marginBottomInPx: Int) {
        val layoutParams = this.layoutParams as ViewGroup.MarginLayoutParams
        val marginBottomInDp = marginBottomInPx.pxToDp()

        layoutParams.setMargins(layoutParams.leftMargin, layoutParams.topMargin, layoutParams.rightMargin, marginBottomInDp)
        this.layoutParams = layoutParams
    }

    protected fun Int.pxToDp(): Int {
        //return (this / Resources.getSystem().displayMetrics.density).toInt()
        return (this * Resources.getSystem().displayMetrics.density).toInt()
    }

    protected fun Int.dpToPx(): Int {
        return (this * Resources.getSystem().displayMetrics.density).toInt()
    }

    class GridSpacingItemDecoration(private val spanCount: Int, private val spacing: Int, private val includeEdge: Boolean) :
        RecyclerView.ItemDecoration() {

        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            val position = parent.getChildAdapterPosition(view)
            val column = position % spanCount

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount
                outRect.right = (column + 1) * spacing / spanCount

                if (position < spanCount) {
                    outRect.top = spacing
                }
                outRect.bottom = spacing
            } else {
                outRect.left = column * spacing / spanCount
                outRect.right = spacing - (column + 1) * spacing / spanCount
                if (position >= spanCount) {
                    outRect.top = spacing
                }
            }
        }
    }
}