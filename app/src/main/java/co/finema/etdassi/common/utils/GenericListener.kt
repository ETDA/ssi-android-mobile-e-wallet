package co.finema.etdassi.common.utils

interface GenericListener<in T> {
    fun onSuccess(response : T)
    fun onFail(errorMessage: String)
}