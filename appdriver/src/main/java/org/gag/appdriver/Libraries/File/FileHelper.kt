package org.gag.appdriver.Libraries.File

import android.R
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.core.graphics.createBitmap
import org.gag.appdriver.Constants.FILE_CONSTANTS
import java.io.File
import java.io.FileOutputStream

class FileHelper(foInstance : Context) {

    private val loInstance = foInstance
    private lateinit var message : String

    fun GetMessage() : String = message

    fun CreateFileFromView(layoutView: View, fileNme : String) : FileResult{

        return FILE_CONSTANTS.EXTERNAL_STORAGE_DCIM_SCREENSHOT.fileDir.let {

            if (!it.exists()) it.mkdirs()
            val file = File(it, fileNme)

            try {

                val bitmap = createBitmap(layoutView.width, layoutView.height)
                val canvas = Canvas(bitmap)

                layoutView.draw(canvas)


                val outputStream = FileOutputStream(file)

                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                outputStream.flush()
                outputStream.close()

                Log.d("Image Location", file.path);

            }catch (ex : Exception){
                message = ex.message.toString()
            }
            FileResult(true, file)
        }
    }

    class FileResult(
        val isSuccess : Boolean,
        val file : File
    )
}