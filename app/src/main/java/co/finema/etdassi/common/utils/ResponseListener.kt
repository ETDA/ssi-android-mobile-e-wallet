package co.finema.etdassi.common.utils

import com.google.gson.JsonObject

interface ResponseListener {
    fun success(s: JsonObject) {
        println("s => $s.=j8i")
//                param.onSuccess()
    }

    fun fail(throwable: Throwable) {
//                param.onFail(throwable.handleError())
    }
}