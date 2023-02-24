package com.MikeKrysan.myapplication.utils

import android.content.Intent
import android.graphics.Bitmap
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.MikeKrysan.myapplication.act.EditAddsAct
import com.fxn.pix.Options
import com.fxn.pix.Pix
import com.fxn.utility.PermUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object ImagePicker {    //16.1
    const val MAX_IMAGE_COUNT = 3   //21.7.1
    const val REQUEST_CODE_GET_IMAGES = 999 //если константу использовать в других классах, то студия не будет подчеркивать переменную, чтобы сделать ее приватной
    const val REQUEST_CODE_GET_SINGLE_IMAGE = 998    //23.2

    private fun getOptions(ImageCounter: Int): Options {  //EditAddsAct наследуется от AppCompatActivity, это все равно чтобы мы передавали AppCompatActivity     //16.2    //17.9 //23.2.1
        val options = Options.init()
//            .setRequestCode(REQUEST_CODE_GET_IMAGES)                    //Request code for activity results
//            .setRequestCode(rCode)                                      //23.2.1
            .setCount(ImageCounter)                                     //Number of images to restrict selection count      //17.9.1
            .setFrontfacing(false)                                      //Front Facing camera on start
            .setMode(Options.Mode.Picture)                              //Option to select only pictures or video or both
//            .setVideoDurationLimitSeconds(30)                         //Duration for video recording
            .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)  //Orientation
            .setPath("pix/images")                                      //Custom Path For media Storage

        return options
//        Pix.start(context, options)
    }

    fun launcher(edAct: EditAddsAct, launcher: ActivityResultLauncher<Intent>?, imageCounter: Int) {
        //Проверяем по аналогии из библиотеки Pix функция start() - 316строка
        PermUtil.checkForCamaraWritePermissions(edAct) {
            val intent = Intent(edAct, Pix::class.java).apply {
                putExtra("options", getOptions(imageCounter))     //Здесь главное не ошибиться. Библиотека Pix ждет ключ по значению "options"
            }
            launcher?.launch(intent)
        }

    }

    fun getLauncherForMultiSelectImages(edAct: EditAddsAct): ActivityResultLauncher<Intent> {
        return edAct.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                if (result.data != null) {
                    val returnValues = result.data?.getStringArrayListExtra(Pix.IMAGE_RESULTS)
                    if (returnValues?.size!! > 1 && edAct.chooseImageFrag == null) {
                        edAct.openChooseImageFrag(returnValues)
                    } else if (edAct.chooseImageFrag != null) {
                        edAct.chooseImageFrag?.updateAdapter(returnValues)
                    } else if (returnValues.size == 1 && edAct.chooseImageFrag == null) {
                        CoroutineScope(Dispatchers.Main).launch {
                            edAct.rootElement.pBarLoad.visibility = View.VISIBLE
                            val bitMapArray = ImageManager.imageResize(returnValues) as ArrayList<Bitmap>
                            edAct.rootElement.pBarLoad.visibility = View.GONE
                            edAct.imageAdapter.update(bitMapArray)
                        }
                    }
                }
            }
        }
    }

    fun getLauncherForSingleImage(edAct: EditAddsAct): ActivityResultLauncher<Intent> {
        return edAct.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                if (result.data != null) {
                    val uris = result.data?.getStringArrayListExtra(Pix.IMAGE_RESULTS)
                    edAct.chooseImageFrag?.setSingleImage(uris?.get(0)!!, edAct.editImagePos)
                }
            }
        }
    }
}