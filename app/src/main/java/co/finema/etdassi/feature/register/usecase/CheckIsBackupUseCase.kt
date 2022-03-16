package co.finema.etdassi.feature.register.usecase

import co.finema.etdassi.common.base.SimpleCoroutineUseCase
import co.finema.etdassi.common.helper.KeyHelper
import co.finema.etdassi.common.helper.replaceDataNewLineJson
import co.finema.etdassi.common.repository.CallApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CheckIsBackupUseCase(
    private val callApi: CallApi
): SimpleCoroutineUseCase<String, Boolean>() {
    override suspend fun executes(param: String): Boolean {
        return withContext(Dispatchers.IO) {
            val didDoc = callApi.call().getDidDoc(param).blockingGet()
            didDoc.recoverer != null

        }
    }

}