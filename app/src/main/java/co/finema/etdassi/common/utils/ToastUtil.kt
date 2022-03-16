package co.finema.etdassi.common.utils

import android.app.Activity
import android.content.Context
import android.widget.Toast

fun Context.toastNormal(string: String) {
    (this as Activity).runOnUiThread {
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show()
    }
}