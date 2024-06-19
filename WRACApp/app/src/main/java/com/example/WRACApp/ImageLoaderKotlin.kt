package com.example.wolseytechhr
import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide

/**
 * ImageLoader class for loading images using the Glide library in Android.
 * @param context The context in which the ImageLoader is used.
 */
class ImageLoader(private val context: Context) {

    /**
     * Loads an image from the given URL into the specified ImageView using Glide.
     * @param url The URL of the image to be loaded.
     * @param imageView The ImageView where the image will be displayed.
     */
    fun loadImage(url: String, imageView: ImageView) {
        Glide.with(context)
            .load(url)
            .into(imageView)
    }
}
