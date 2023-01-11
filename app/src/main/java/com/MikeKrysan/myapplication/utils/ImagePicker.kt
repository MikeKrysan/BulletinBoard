package com.MikeKrysan.myapplication.utils

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import androidx.appcompat.app.AppCompatActivity
import com.MikeKrysan.myapplication.MainActivity
import com.fxn.pix.Options
import com.fxn.pix.Pix

object ImagePicker {    //16.1
    const val MAX_IMAGE_COUNT = 3   //21.7.1
    const val REQUEST_CODE_GET_IMAGES = 999 //если константу использовать в других классах, то студия не будет подчеркивать переменную, чтобы сделать ее приватной
    const val REQUEST_CODE_GET_SINGLEIMAGE = 998    //23.2
    fun getImages(context: AppCompatActivity, ImageCounter:Int, rCode : Int){  //EditAddsAct наследуется от AppCompatActivity, это все равно чтобы мы передавали AppCompatActivity     //16.2    //17.9 //23.2.1
        val options = Options.init()
//            .setRequestCode(REQUEST_CODE_GET_IMAGES)                    //Request code for activity results
            .setRequestCode(rCode)                                      //23.2.1
            .setCount(ImageCounter)                                     //Number of images to restrict selection count      //17.9.1
            .setFrontfacing(false)                                      //Front Facing camera on start
            .setMode(Options.Mode.Picture)                              //Option to select only pictures or video or both
//            .setVideoDurationLimitSeconds(30)                         //Duration for video recording
            .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)  //Orientation
            .setPath("pix/images")                                      //Custom Path For media Storage

        Pix.start(context, options)
    }
}