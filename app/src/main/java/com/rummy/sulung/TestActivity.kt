package com.rummy.sulung

import android.Manifest.permission.*
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Build.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import java.io.ByteArrayOutputStream

import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody

import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.bumptech.glide.load.resource.bitmap.TransformationUtils.rotateImage

import java.io.File
import java.io.FileOutputStream
import java.lang.Float.max
import java.lang.Float.min
import java.util.concurrent.ExecutorService
import java.util.concurrent.TimeUnit

import java.util.concurrent.Executors

class TestActivity : AppCompatActivity() {

    private lateinit var imageCapture: ImageCapture
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var croppedImageView: ImageView
    private lateinit var cropView: View
    private lateinit var viewFinder: PreviewView
    private lateinit var takePhotoButton : Button
    private lateinit var cropButton : Button
    private lateinit var scaleDetector: ScaleGestureDetector

    private var xDelta: Float = 0f
    private var yDelta: Float = 0f
    private var currentX: Float = 0f
    private var currentY: Float = 0f
    private var startRawX: Float = 0f
    private var startRawY: Float = 0f
    private var screenWidth: Int = 0
    private var screenHeight: Int = 0
    private var cropSize: Int = 0

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA)
        private const val TAG = "CameraXExample"
        private const val REQUEST_IMAGE_CROP = 11
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        //requestPermissions()

        viewFinder = findViewById(R.id.view_finder)

        // CameraExecutor 초기화
        cameraExecutor = Executors.newSingleThreadExecutor()

        // 카메라 권한 요청
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        // 사진 촬영 버튼 및 이벤트 핸들러 등록
        takePhotoButton = findViewById(R.id.take_photo_button)
        takePhotoButton.setOnClickListener {
            takePhoto()
        }
        scaleDetector = ScaleGestureDetector(this, ScaleListener())
    }

    override fun onResume() {
        super.onResume()
        setStatusBarBlackAndWhiteText()
    }

    private fun cropImage() {
        val bitmap = viewFinder.bitmap
        if (bitmap != null) {
            val croppedBitmap = Bitmap.createBitmap(
                bitmap,
                cropView.left,
                cropView.top,
                cropView.width,
                cropView.height
            )
            croppedImageView.setImageBitmap(croppedBitmap)
        }
    }

    private fun takePhoto() {
        val file = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "${System.currentTimeMillis()}.jpg")

        // 이미지를 캡처하고 성공 또는 실패 콜백을 수신합니다.
        val imageCaptureOutputFileOptions = ImageCapture.OutputFileOptions.Builder(file).build()
        imageCapture.takePicture(imageCaptureOutputFileOptions, ContextCompat.getMainExecutor(this), object : ImageCapture.OnImageSavedCallback {
            override fun onError(exc: ImageCaptureException) {
                Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
            }
            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                saveImageToApi(file)
            }
        })
    }

    private fun rotateImageIfRequired(context: Context, uri: Uri): Bitmap {
        val inputStream = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        val ei = inputStream?.use { stream ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                ExifInterface(stream)
            } else {
                uri.path?.let { ExifInterface(it) }
            }
        } ?: return bitmap

        val orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270f)
            else -> rotateImage(bitmap, 90f)
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun setStatusBarBlackAndWhiteText() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
            window.statusBarColor = Color.BLACK
            window.navigationBarColor = Color.TRANSPARENT
        }
    }

   /* private fun saveImageToApi(bitmap: Bitmap) {
        // 이미지를 저장하고 결과를 인텐트에 추가합니다.
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        val intent = Intent()
        intent.putExtra("image", byteArray)

        // 결과값을 반환합니다.
        setResult(Activity.RESULT_OK, intent)
        finish()
    }*/

    private fun saveImageToApi(imageFile: File) {
        // 인텐트에 파일 객체를 추가합니다.
        val intent = Intent()
        intent.putExtra("imageFile", imageFile)

        // 결과값을 반환합니다.
        setResult(Activity.RESULT_OK, intent)
        finish()
    }


    // 카메라 초기화 및 실행
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            // 카메라 제공자에서 제공된 프리뷰, 이미지 캡처 및 분석용 카메라를 사용하여 카메라를 바인딩합니다.
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(viewFinder.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder().build()
            val imageAnalyzer = ImageAnalysis.Builder().build().also {
                it.setAnalyzer(cameraExecutor, LuminosityAnalyzer())
            }
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try {
                // 카메라를 바인딩하고 프리뷰를 시작합니다.
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture, imageAnalyzer)
            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun rotateImage(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    // 카메라 권한 확인 및 요청
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, CAMERA) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, arrayOf(WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE, CAMERA), 12)
        }
    }

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            var newWidth = cropView.width * detector.scaleFactor
            var newHeight = cropView.height * detector.scaleFactor

            // 최소 크기 제한
            newWidth = max(50f, newWidth)
            newHeight = max(50f, newHeight)

            // 최대 크기 제한
            newWidth = min(screenWidth.toFloat(), newWidth)
            newHeight = min(screenHeight.toFloat(), newHeight)

            // 크기 변경
            val layoutParams = cropView.layoutParams
            layoutParams.width = newWidth.toInt()
            layoutParams.height = newHeight.toInt()
            cropView.layoutParams = layoutParams

            return true
        }
    }

    private class LuminosityAnalyzer : ImageAnalysis.Analyzer {
        private var lastAnalyzedTimestamp = 0L

        override fun analyze(image: ImageProxy) {
            val currentTimestamp = System.currentTimeMillis()

            // 초당 분석 제한을 적용합니다.
            if (currentTimestamp - lastAnalyzedTimestamp >= TimeUnit.SECONDS.toMillis(1)) {
                // 이미지를 분석합니다.
                val buffer = image.planes[0].buffer
                val data = ByteArray(buffer.remaining())
                buffer.get(data)
                val pixels = data.map { byte -> byte.toInt() and 0xFF }
                val luma = pixels.average()

                // 분석 결과를 처리합니다.
                Log.d(TAG, "Average luminosity: $luma")
                lastAnalyzedTimestamp = currentTimestamp
            }

            // 이미지 처리 완료를 알립니다.
            image.close()
        }
    }
}