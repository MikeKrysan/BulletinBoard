package com.MikeKrysan.myapplication.utils

import android.graphics.BitmapFactory
import android.util.Log
import androidx.exifinterface.media.ExifInterface

import java.io.File

object ImageManager {

    const val MAX_IMAGE_SIZE = 1000     //26.1Указываем максимальный размер картинки
    const val WIDTH = 0
    const val HEIGHT = 1

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

    fun imageResize(uris : List<String>) {  //26.2
        val tempList = ArrayList<List<Int>>()
        for(n in uris.indices) {
            val size = getImageSize(uris[n])
            Log.d("MyLog", "Width : ${size[WIDTH]} Height ${size[HEIGHT]}") //26.3

            val imageRatio = size[WIDTH].toFloat() / size[HEIGHT].toFloat()  //теперь мы знаем пропорцию нашей картинки

            //Если число больше 1, значит что ширина больше высоты. Уменьшаем ширину до 1000(максимальный размер, сами задаем его). Если ширина меньше одного, значит ширина меньше высоты, вертикальная ориентация. Уменьшаем высоту до 1000
            if(imageRatio > 1) {
                //Делаем еще одну проверку, если картинка не превышает максимального размера
                if(size[WIDTH] > MAX_IMAGE_SIZE) {
                    tempList.add(listOf(MAX_IMAGE_SIZE, (MAX_IMAGE_SIZE/ imageRatio).toInt()))
                } else {
                    tempList.add(listOf(size[WIDTH], size[HEIGHT]))
                }
            } else {
                if(size[HEIGHT] > MAX_IMAGE_SIZE) {
                    tempList.add(listOf((MAX_IMAGE_SIZE * imageRatio).toInt(), MAX_IMAGE_SIZE))
                } else {
                    tempList.add(listOf(size[WIDTH], size[HEIGHT]))
                }
            }


            Log.d("MyLog", "Width : ${tempList[n][WIDTH]} Height ${tempList[n][HEIGHT]}")   //26.3
        }


    }
}