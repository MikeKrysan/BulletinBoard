package com.MikeKrysan.myapplication.utils

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import androidx.appcompat.app.AppCompatActivity
import com.MikeKrysan.myapplication.MainActivity
import com.fxn.pix.Options
import com.fxn.pix.Pix

object ImagePicker {    //16.1
    const val REQUEST_CODE_GET_IMAGES = 999 //если константу использовать в других классах, то студия не будет подчеркивать переменную, чтобы сделать ее приватной
    fun getImages(context: AppCompatActivity){  //EditAddsAct наследуется от AppCompatActivity, это все равно чтобы мы передавали AppCompatActivity     //16.2
        val options = Options.init()
            .setRequestCode(REQUEST_CODE_GET_IMAGES)                    //Request code for activity results
            .setCount(3)                                                //Number of images to restrict selection count
            .setFrontfacing(false)                                      //Front Facing camera on start
            .setMode(Options.Mode.Picture)                              //Option to select only pictures or video or both
//            .setVideoDurationLimitSeconds(30)                         //Duration for video recording
            .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)  //Orientation
            .setPath("pix/images")                                      //Custom Path For media Storage

        Pix.start(context, options)
    }
}