package com.allywingz.imagepickerhelper

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val image = findViewById<ShapeableImageView>(R.id.image)
        val galleyBtn = findViewById<MaterialButton>(R.id.choose_from_gallery)
        val cameraBtn = findViewById<MaterialButton>(R.id.take_a_new_photo)

        val imagePickerHelper = ImagePickerHelper(this@MainActivity, this, object : ImagePickerHelper.ImagePickerListener {
            override fun onImageSelected(imageUri: Uri) {
                image.setImageURI(imageUri)
            }
        })

        cameraBtn.setOnClickListener {
            imagePickerHelper.openCamera()
        }

        galleyBtn.setOnClickListener {
            imagePickerHelper.openGallery()
        }
    }
}