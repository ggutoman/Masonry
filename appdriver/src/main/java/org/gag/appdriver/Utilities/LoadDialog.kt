package org.gag.appdriver.Utilities

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView
import org.gag.appdriver.R
import androidx.core.graphics.drawable.toDrawable
import com.google.android.material.progressindicator.CircularProgressIndicator

class LoadDialog(instance : Context) {

    val context : Context = instance

    lateinit var poDialog : AlertDialog
    lateinit var icon_progress : CircularProgressIndicator
    lateinit var mtv_message : MaterialTextView

    interface OnDialogClick{
        fun OnPositive(poDialog : AlertDialog)
        fun OnNegative(poDialog : AlertDialog)
    }

    @SuppressLint("InflateParams")
    fun InitDialog(){

        LayoutInflater.from(context).inflate(R.layout.layout_loading, null).also {

            AlertDialog.Builder(context)
                .setCancelable(false)
                .setView(it).also {
                    poDialog = it.create().also { it.setCancelable(false) }
                }

            icon_progress = it.findViewById(R.id.icon_progress)
            mtv_message = it.findViewById(R.id.mtv_message)

        }
    }

    fun ShowDialog(fsMessage : String){
        if (!poDialog.isShowing){

            mtv_message.text = fsMessage

            poDialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
            poDialog.window?.attributes?.windowAnimations = R.style.PopupAnimation

            poDialog.show()
        }
    }

    fun DismissDialog(){
        if (poDialog.isShowing) poDialog.dismiss()
    }
}