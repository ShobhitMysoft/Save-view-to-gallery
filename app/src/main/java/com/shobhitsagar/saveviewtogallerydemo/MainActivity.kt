package com.shobhitsagar.saveviewtogallerydemo

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class MainActivity : AppCompatActivity() {

    lateinit var imageUri: Uri
    lateinit var imageView: ImageView
    private val contract = registerForActivityResult(ActivityResultContracts.GetContent()) {
        imageView.setImageURI(it)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val container = findViewById<ConstraintLayout>(R.id.container)
        val addBtn = findViewById<Button>(R.id.add_btn)
        val btn = findViewById<Button>(R.id.save_btn)
        val et = findViewById<EditText>(R.id.et)
        var textV = findViewById<TextView>(R.id.text)
        imageView = findViewById<ImageView>(R.id.image_view)
        imageUri = createImageUri()

        btn.setOnClickListener {
            val bitmap = getBitmapFromView(container)
            storeBitmap(bitmap)
        }

        addBtn.setOnClickListener {
            textV.text = et.text.toString()
        }

        findViewById<Button>(R.id.change_img_btn).setOnClickListener {
            contract.launch("image/*", )
        }

        findViewById<TextView>(R.id.black_btn).setOnClickListener {
            textV.setTextColor(Color.parseColor("#FF000000"))
        }

        findViewById<TextView>(R.id.white_btn).setOnClickListener {
            textV.setTextColor(Color.parseColor("#FFFFFFFF"))
        }
    }

    private fun storeBitmap(bitmap: Bitmap) {
        val imageName = "99bit_${System.currentTimeMillis()}.jpg"
        var fos: OutputStream? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            this.contentResolver?.also { resolver ->
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, imageName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }
                val mImageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                fos = mImageUri?.let {
                    resolver.openOutputStream(it)
                }
            }
        } else {
            val imagesDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDirectory, imageName)
            fos = FileOutputStream(image)
        }

        fos?.use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createImageUri(): Uri {
        val image = File(applicationContext.filesDir, "camera_photo.png")
        return FileProvider.getUriForFile(
            applicationContext,
            "com.shobhitsagar.saveviewtogallerydemo.fileProvider",
            image
        )
    }

    private fun getBitmapFromView(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val bg = view.background
        bg.draw(canvas)
        view.draw(canvas)
        return bitmap
    }
}