package co.finema.etdassi.common.utils

interface ApiListener {
    fun onSuccess()
    fun onFail(errorMessage: String)
}