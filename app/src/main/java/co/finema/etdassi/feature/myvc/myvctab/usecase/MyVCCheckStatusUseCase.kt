package co.finema.etdassi.feature.myvc.myvctab.usecase

import co.finema.etdassi.common.base.SimpleCoroutineUseCase
import co.finema.etdassi.common.data.VCDocumentRepository
import co.finema.etdassi.common.repository.CallApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MyVCCheckStatusUseCase(
    private val callApi: CallApi,
    private val vcDocumentRepository: VCDocumentRepository
): SimpleCoroutineUseCase<List<String>, Boolean>() {

    class CIDStatusResponse : ArrayList<CIDStatusResponseItem>()

    data class CIDStatusResponseItem(
        val activated_at: String?,
        val cid: String,
        val created_at: String,
        val did_address: String,
        val expired_at: Any?,
        val revoked_at: Any?,
        val status: String?,
        val tags: List<String>?,
        val updated_at: String,
        val vc_hash: String
    )

    override suspend fun executes(param: List<String>): Boolean {
        return withContext(Dispatchers.IO) {
            val cidListString = param.joinToString().replace(" ", "")
            val statusResponse = callApi.call().cidCheckStatus(cidListString).blockingGet()
            statusResponse?.let { cidStatusResponse ->
                cidStatusResponse.forEach {
                    vcDocumentRepository.updateVCSDocumentStatus(
                        cid = it.cid,
                        status = it.status ?: "",
                        tags = it.tags?.joinToString()
                    )
                }
            }
            true
        }
    }
}