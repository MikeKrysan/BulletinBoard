package com.MikeKrysan.myapplication.utils

import android.graphics.BitmapFactory
import androidx.exifinterface.media.ExifInterface

import java.io.File

object ImageManager {

    fun getImageSize(uri : String) : List<Int>  { //25.2

        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true  //получаем не всю картинку, а только края, чтобы узнать размеры картинки
        }
        BitmapFactory.decodeFile(uri, options)

        return if(imageRotation(uri) == 90)    //25.5
            listOf(options.outHeight, options.outWidth)
        else listOf(options.outWidth, options.outHeight)

    }

    private fun imageRotation(uri : String) : Int {   //25.4
        val rotation: Int
        val imageFile = File(uri)
        val exif  = ExifInterface(imageFile.absolutePath)
        val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        rotation = if(orientation == ExifInterface.ORIENTATION_ROTATE_90 || orientation == ExifInterface.ORIENTATION_ROTATE_270) {
            90
        } else {
            0
        }
        return rotation
    }
}