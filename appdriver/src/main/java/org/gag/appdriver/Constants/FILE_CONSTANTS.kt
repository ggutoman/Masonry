package org.gag.appdriver.Constants

import android.os.Environment
import java.io.File

enum class FILE_CONSTANTS(val fileDir : File){

    EXTERNAL_STORAGE_DCIM_SCREENSHOT(
        File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Screenshots")
    )
}