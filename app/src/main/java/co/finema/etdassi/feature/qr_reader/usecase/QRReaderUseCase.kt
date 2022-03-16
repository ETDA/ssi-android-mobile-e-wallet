package co.finema.etdassi.feature.qr_reader.usecase

import android.os.Parcelable
import co.finema.etdassi.common.base.SimpleCoroutineUseCase
import co.finema.etdassi.common.repository.CallApi
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class QRReaderUseCase(
    private val callApi: CallApi
): SimpleCoroutineUseCase<QRReaderUseCase.Param, QRReaderUseCase.RequestingVPDocResponse>(){

    data class Param(
        val url: String?
    )

    @Parcelize
    data class RequestingVPDocResponse(
        @SerializedName("id") val requestId: String?,
        @SerializedName("name") val name: String?,
        @SerializedName("status") val status: String?,
        @SerializedName("schema_count") val schemaCount: Int?,
        @SerializedName("created_at") val createAt: String?,
        @SerializedName("updated_at") val updatedAt: String?,
        @SerializedName("schema_list") val vpRequest: List<VpRequest>?,
        @SerializedName("verifier_did") val verifierDid: String?,
        @SerializedName("endpoint") val endpoint: String?,
        val verifier: String
        ): Parcelable

    @Parcelize
    data class VpRequest(
        @SerializedName("id") val id: String?,
        @SerializedName("requested_vp_id") val requestVPId: String?,
        @SerializedName("schema_type") val type: String?,
        @SerializedName("is_required") val isRequired: Boolean = false,
        @SerializedName("noted") val noted: String?,
        @SerializedName("created_at") val createAt: String?,
        @SerializedName("updated_at") val updatedAt: String?
        ): Parcelable

    private fun getHeaderMap(accessKey: String): Map<String, String> {
        val headerMap = mutableMapOf<String, String>()
        headerMap["Authorization"] = accessKey
        return headerMap
    }

    override suspend fun executes(param: Param): RequestingVPDocResponse {
        return withContext(Dispatchers.IO) {
            println("param ${param.url}")
            callApi.call().getVPRequestDocument(url = param.url).blockingGet()
        }
    }


}