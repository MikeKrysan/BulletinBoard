package com.MikeKrysan.myapplication.utils

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.support.media.ExifInterface
import android.widget.ImageView
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

import java.io.File
import java.io.InputStream

object ImageManager {

    const val MAX_IMAGE_SIZE = 1000     //26.1Указываем максимальный размер картинки
    private const val WIDTH = 0
    private const val HEIGHT = 1

    fun getImageSize(uri : Uri, act: Activity) : List<Int>  { //25.2
        val inStream = act.contentResolver.openInputStream(uri)
//        val fTemp = File(act.cacheDir, "temp.tmp")
//        if (inStream != null) {
//            fTemp.copyInStreamToFile(inStream)
//        }
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true  //получаем не всю картинку, а только края, чтобы узнать размеры картинки
        }
        BitmapFactory.decodeStream(inStream, null, options)
//        BitmapFactory.decodeFile(fTemp.path, options)

        return listOf(options.outWidth, options.outHeight)
//        return if(imageRotation(fTemp) == 90)    //25.5
//            listOf(options.outHeight, options.outWidth)
//        else listOf(options.outWidth, options.outHeight)

    }

//    private fun File.copyInStreamToFile(inStream: InputStream) {
//        this.outputStream().use{
//            out -> inStream.copyTo(out)
//        }
//    }

//    private fun imageRotation(imageFile: File) : Int {   //25.4
//        val rotation: Int
//        val exif  = ExifInterface(imageFile.absolutePath)
//        val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
//        rotation = if(orientation == ExifInterface.ORIENTATION_ROTATE_90 || orientation == ExifInterface.ORIENTATION_ROTATE_270) {
//            90
//        } else {
//            0
//        }
//        return rotation
//    }

    fun chooseScaleType(im: ImageView, bitMap : Bitmap) {
        if(bitMap.width > bitMap.height) {
            im.scaleType = ImageView.ScaleType.CENTER_CROP
        } else {
            im.scaleType = ImageView.ScaleType.CENTER_INSIDE
        }
    }

    suspend fun imageResize(uris: ArrayList<Uri>, act: Activity): List<Bitmap> = withContext(Dispatchers.IO) {  //26.2  //27.3    //27.7
        val tempList = ArrayList<List<Int>>()
        val bitmapList = ArrayList<Bitmap>()
        for(n in uris.indices) {
            val size = getImageSize(uris[n], act)
//            Log.d("MyLog", "Width : ${size[WIDTH]} Height ${size[HEIGHT]}") //26.3

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
//            Log.d("MyLog", "Width : ${tempList[n][WIDTH]} Height ${tempList[n][HEIGHT]}")   //26.3

        }
        for(i in uris.indices) {

        kotlin.runCatching {
                bitmapList.add(Picasso.get().load((uris[i])).resize(tempList[i][WIDTH], tempList[i][HEIGHT]).get())
            }

        }

        return@withContext bitmapList   //27.7

    }
}