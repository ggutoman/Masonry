package org.gag.appdriver.Libraries.File

import android.R
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Environment
import android.util.Base64
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

    fun CreateFileFromView(layoutView: View, fsParentFile : File, fileNme : String) : FileResult{

        return fsParentFile.let {

            if (!it.exists()) it.mkdirs()
            val file = File(it, fileNme)

            try {

                val bitmap = createBitmap(layoutView.width, layoutView.height)
                val canvas = Canvas(bitmap)

                layoutView.draw(canvas)

                val outputStream = FileOutputStream(file)

                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                outputStream.flush()
                outputStream.close()

            }catch (ex : Exception){
                message = ex.message.toString()
            }
            FileResult(file.exists(), file)
        }
    }

    fun GenerateFile(fsParentFile : File, fsFileNme : String) : File{

        return fsParentFile.let {

            if (!it.exists()) it.mkdirs()
            File(it, fsFileNme)
        }
    }

    fun CreateFileFromByteString(bytes : String, fsFilePath : String) : FileResult{

        val fileBytes = Base64.decode(bytes, Base64.NO_WRAP)
        val outputFile = File(fsFilePath)

        FileOutputStream(outputFile).use { fos ->
            fos.write(fileBytes)
            fos.flush()
        }
        return FileResult(outputFile.exists() && outputFile.isFile, outputFile)
    }

    class FileResult(
        val isSuccess : Boolean,
        val file : File
    )
}