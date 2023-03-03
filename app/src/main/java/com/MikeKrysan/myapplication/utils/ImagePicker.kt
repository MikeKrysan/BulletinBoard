package com.MikeKrysan.myapplication.utils

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.MikeKrysan.myapplication.R
import com.MikeKrysan.myapplication.act.EditAdsAct
import io.ak1.pix.helpers.PixEventCallback
import io.ak1.pix.helpers.addPixToActivity
import io.ak1.pix.models.Mode
import io.ak1.pix.models.Options
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object ImagePicker {    //16.1
    const val MAX_IMAGE_COUNT = 3   //21.7.1
    const val REQUEST_CODE_GET_IMAGES = 999 //если константу использовать в других классах, то студия не будет подчеркивать переменную, чтобы сделать ее приватной
    const val REQUEST_CODE_GET_SINGLE_IMAGE = 998    //23.2

    private fun getOptions(imageCounter: Int): Options {  //EditAddsAct наследуется от AppCompatActivity, это все равно чтобы мы передавали AppCompatActivity     //16.2    //17.9 //23.2.1
        val options = Options().apply {
            count = imageCounter
            isFrontFacing = false
            mode = Mode.Picture
            path = "pix/images"
        }
        return options
    }
////            .setRequestCode(REQUEST_CODE_GET_IMAGES)                    //Request code for activity results
////            .setRequestCode(rCode)                                      //23.2.1
//            .setCount(imageCounter)                                     //Number of images to restrict selection count      //17.9.1
//            .setFrontfacing(false)                                      //Front Facing camera on start
//            .setMode(Options.Mode.Picture)                              //Option to select only pictures or video or both
//            .setVideoDurationLimitSeconds(30)                         //Duration for video recording
//            .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)  //Orientation
//            .setPath("pix/images")                                      //Custom Path For media Storage
//
//        return options
//        Pix.start(context, options)
//    }

    fun launcher(edAct: EditAdsAct, imageCounter: Int) {
        edAct.addPixToActivity(R.id.place_holder, getOptions(imageCounter)) {   result ->
            when (result.status) {
                PixEventCallback.Status.SUCCESS -> {
                    getMultiSelectedImages(edAct, result.data)
                    closePixFrag(edAct)
                }
                else -> {}
            }
        }
    }

    private fun closePixFrag(edAct: EditAdsAct) {
        val fList = edAct.supportFragmentManager.fragments
        fList.forEach {
            if(it.isVisible) edAct.supportFragmentManager.beginTransaction().remove(it).commit()
        }
    }

    fun getMultiSelectedImages(edAct: EditAdsAct, uris: List<Uri>) {

        if (uris.size > 1 && edAct.chooseImageFrag == null) {
            edAct.openChooseImageFrag(uris as ArrayList<Uri>)
        } else if (edAct.chooseImageFrag != null) {
            edAct.chooseImageFrag?.updateAdapter(uris as ArrayList<Uri>)
        } else if (uris.size == 1 && edAct.chooseImageFrag == null) {
            CoroutineScope(Dispatchers.Main).launch {
                edAct.rootElement.pBarLoad.visibility = View.VISIBLE
                val bitMapArray = ImageManager.imageResize(uris as ArrayList<Uri>, edAct) as ArrayList<Bitmap>
                edAct.rootElement.pBarLoad.visibility = View.GONE
                edAct.imageAdapter.update(bitMapArray)
            }

        }
    }

    fun getLauncherForSingleImage(edAct: EditAdsAct): ActivityResultLauncher<Intent> {
        return edAct.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
           /* if (result.resultCode == AppCompatActivity.RESULT_OK) {
                if (result.data != null) {
                    val uris = result.data?.getStringArrayListExtra(Pix.IMAGE_RESULTS)
                    edAct.chooseImageFrag?.setSingleImage(uris?.get(0)!!, edAct.editImagePos)
                }
            } */
        }
    }
}

//trying to commit