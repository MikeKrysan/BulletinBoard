package com.MikeKrysan.myapplication.dialogHelper

import android.app.Activity
import android.app.AlertDialog
import com.MikeKrysan.myapplication.databinding.ProgressDialogLayoutBinding

object ProgressDialog {

    fun createProgressDialog(actDH: Activity): AlertDialog {

        val builder = AlertDialog.Builder(actDH)
        val rootDialogElement = ProgressDialogLayoutBinding.inflate(actDH.layoutInflater)
        val view = rootDialogElement.root
        builder.setView(view)
        val dialog = builder.create()
        dialog.setCancelable(false)
        dialog.show()
        return dialog

    }

}