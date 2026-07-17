package org.gag.appdriver.Constants

import android.app.Application
import android.content.Context
import android.os.Environment
import androidx.paging.ContiguousPagedList
import java.io.File

enum class FILE_CONSTANTS(val fileDir : File){

    EXTERNAL_STORAGE_DCIM_SCREENSHOT(
        File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Masonry/Screenshots")
    ),

    EXTERNAL_STORAGE_DCIM_DOCUMENTS(
        File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Masonry/Documents/")
    ),

    EXTERNAL_STORAGE_DCIM_REPORTS(
        File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "MasonryReports/")
    )
}