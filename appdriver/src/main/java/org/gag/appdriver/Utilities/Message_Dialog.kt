package org.gag.appdriver.Utilities

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView
import org.gag.appdriver.R
import androidx.core.graphics.drawable.toDrawable

class Message_Dialog(instance : Context) {

    val context : Context = instance

    lateinit var poDialog : AlertDialog
    lateinit var icon_status : ShapeableImageView
    lateinit var mtv_message : MaterialTextView
    lateinit var btn_positive : MaterialButton
    lateinit var btn_negative : MaterialButton

    interface OnDialogClick{
        fun OnPositive(poDialog : AlertDialog)
        fun OnNegative(poDialog : AlertDialog)
    }

    fun InitDialog(){

        LayoutInflater.from(context).inflate(R.layout.layout_message, null).let {

            AlertDialog.Builder(context)
                .setCancelable(false)
                .setView(it).let {
                    poDialog = it.create().also { it.setCancelable(false) }
                }

            icon_status = it.findViewById(R.id.icon_status)
            mtv_message = it.findViewById(R.id.mtv_message)
            btn_positive = it.findViewById(R.id.btn_positive)
            btn_negative = it.findViewById(R.id.btn_negative)

        }
    }

    fun ShowMessage(fnStatus : Int, fsMessage : String, fsPosText : String, fsNegative : String, foCallback : OnDialogClick){
        if (!poDialog.isShowing){

            InitDialog()

            when{

                fnStatus < 2 -> { //Exclude confirmation, no need negative button
                    icon_status.setImageResource(R.drawable.baseline_error_24) //error by default
                    if (fnStatus < 1) icon_status.setImageResource(R.drawable.baseline_message) //change icon to message

                    btn_positive.visibility = View.VISIBLE
                    btn_negative.visibility = View.GONE
                }

                fnStatus > 2 -> { //confirmation only
                    icon_status.setImageResource(R.drawable.baseline_confirm) //change icon to confirmation

                    btn_positive.visibility = View.VISIBLE
                    btn_negative.visibility = View.VISIBLE

                }
            }

            btn_positive.text = fsPosText
            btn_negative.text = fsNegative
            mtv_message.text = fsMessage

            btn_positive.setOnClickListener { view -> foCallback.OnPositive(poDialog) }
            btn_negative.setOnClickListener { view -> foCallback.OnNegative(poDialog) }

            poDialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
            poDialog.window?.attributes?.windowAnimations = R.style.PopupAnimation

            poDialog.show()
            return
        }
    }
}