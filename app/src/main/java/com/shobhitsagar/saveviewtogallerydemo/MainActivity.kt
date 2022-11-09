package com.shobhitsagar.saveviewtogallerydemo

import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.FileProvider
import java.io.File

class MainActivity : AppCompatActivity() {

    lateinit var imageUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val container = findViewById<LinearLayout>(R.id.container)
        val addBtn = findViewById<Button>(R.id.add_btn)
        val btn = findViewById<Button>(R.id.save_btn)
        val et = findViewById<EditText>(R.id.et)
        var textV = findViewById<TextView>(R.id.text)
        imageUri = createImageUri()

        btn.setOnClickListener {
            val bitmap = getBitmapFromView(container)
            storeBitmap(bitmap)
        }

        addBtn.setOnClickListener {
            textV.text = et.text.toString()
        }
    }

    private fun storeBitmap(bitmap: Bitmap) {
        val outputStream = applicationContext.contentResolver.openOutputStream(imageUri)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        outputStream!!.close()
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