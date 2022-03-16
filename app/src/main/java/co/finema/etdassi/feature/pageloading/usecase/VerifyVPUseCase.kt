package co.finema.etdassi.feature.pageloading.usecase

import android.os.Parcelable
import co.finema.etdassi.common.base.SimpleCoroutineUseCase
import co.finema.etdassi.common.data.VCAttributeModel
import co.finema.etdassi.common.helper.KeyHelper
import co.finema.etdassi.common.repository.Api
import co.finema.etdassi.common.repository.CallApi
import co.finema.etdassi.common.utils.JWTUtils
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class VerifyVPUseCase(
    private val callApi: CallApi
): SimpleCoroutineUseCase<String, VerifyVPUseCase.QRVerifyResult>() {

    data class VPJWTResponse(
        @SerializedName("jwt") val vpJWT: String
    )

    @Parcelize
    data class QRVerifyJWTResponse(
        @SerializedName("verification_result") val verificationResult: Boolean,
        @SerializedName("id") val id: String,
        @SerializedName("audience") val audience: String,
        @SerializedName("issuer") val issuer: String,
        @SerializedName("issuance_date") val issuanceDate: String,
        @SerializedName("expiration_date") val expirationDate: String?,
        @SerializedName("vc") val vc: List<QRVerifyJWTResponseVC>,
    ): Parcelable {
        @Parcelize
        data class QRVerifyJWTResponseVC(
            @SerializedName("verification_result") val verificationResult: Boolean,
            @SerializedName("cid") val cid: String,
            @SerializedName("status") val status: String,
            @SerializedName("issuance_date") val issuanceDate: String,
            @SerializedName("type") val type: List<String>,
            @SerializedName("tags") val tags: List<String>?,
            @SerializedName("issuer") val issuer: String,
            @SerializedName("holder") val holder: String
        ): Parcelable
    }

    @Parcelize
    data class QRVerifyResult(
        val vcStatus: String,
        val vcType: String,
        val verifyStatus: Boolean,
        val description: List<VCAttributeModel>?
    ): Parcelable

    override suspend fun executes(param: String): QRVerifyResult {
        return withContext(Dispatchers.IO) {
            val jwtResponse = callApi.call().getJWTFromUrl(param).blockingGet()
            val response = callApi.call().qrVerifyJWT(Api.RequestMessage(jwtResponse.vpJWT)).blockingGet()
            val vpPayload = JWTUtils.decodedJWT(jwtResponse.vpJWT)
            val vpSchemaModel = vpPayload?.let { it->
                JWTUtils.jwtConvertToSchemaModel(it)
            }
            val vcJWT = vpSchemaModel?.vp?.verifiableCredential?.first()
            val vcPayload = JWTUtils.decodedJWT(vcJWT!!)
            val vcSchemaModel = vcPayload?.let {
                JWTUtils.jwtConvertToSchemaModel(it)
            }
            val credentialSubject = vcSchemaModel?.vc?.credentialSubject?.let {
                JWTUtils.credentialSubjectToAttributeModel(
                    it
                )
            }
            val vc = response.vc.first()
            QRVerifyResult(
                vcStatus = vc.status,
                vcType = vc.type.last(),
                verifyStatus = vc.verificationResult,
                description = credentialSubject
            )
        }
    }
}