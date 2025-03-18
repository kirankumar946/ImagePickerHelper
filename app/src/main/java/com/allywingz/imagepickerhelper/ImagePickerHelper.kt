package com.allywingz.imagepickerhelper

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ImagePickerHelper(
    private val activity: Activity,
    private val registryOwner: ActivityResultRegistryOwner,
    private val listener: ImagePickerListener
) {

    private var cameraImageUri: Uri? = null

    // Register Camera Permission
    private val requestPermissionLauncher: ActivityResultLauncher<String> =
        registryOwner.activityResultRegistry.register(
            "camera_permission",
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                launchCamera()
            } else {
                Toast.makeText(activity, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    // Register Camera Capture
    private val captureImageLauncher: ActivityResultLauncher<Uri> =
        registryOwner.activityResultRegistry.register(
            "take_picture",
            ActivityResultContracts.TakePicture()
        ) { success ->
            if (success) {
                cameraImageUri?.let { listener.onImageSelected(it) }
            } else {
                Toast.makeText(activity, "Failed to capture image", Toast.LENGTH_SHORT).show()
            }
        }

    // Register Gallery Picker
    private val pickImageLauncher: ActivityResultLauncher<String> =
        registryOwner.activityResultRegistry.register(
            "pick_image",
            ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            uri?.let { listener.onImageSelected(it) }
        }

    // Function to Open Camera
    fun openCamera() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            launchCamera()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // Function to Open Gallery
    fun openGallery() {
        pickImageLauncher.launch("image/*")
    }

    // Function to Create Image URI for Camera Capture
    private fun launchCamera() {
        cameraImageUri = createImageUri()
        cameraImageUri?.let { captureImageLauncher.launch(it) }
            ?: Toast.makeText(activity, "Failed to create image URI", Toast.LENGTH_SHORT).show()
    }

    // Function to Generate a Unique Image URI
    private fun createImageUri(): Uri? {
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "IMG_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }
        return activity.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
    }

    // Interface to Handle Image Selection Result
    interface ImagePickerListener {
        fun onImageSelected(imageUri: Uri)
    }
}
