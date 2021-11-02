package com.sg.ar1

import android.app.Activity
import android.os.Environment
import java.text.SimpleDateFormat
import java.util.*

class OhotoSaver(
    private var activity:Activity
) {
    private fun generateFilename():String?{
        val date=SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())
        return  Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)?.absolutePath+
                "/TruOutFurnitue/${date}_screenshot.ipg"
    }
}