package co.finema.etdassi.feature.pageloading.usecase

import co.finema.etdassi.common.base.SimpleCoroutineUseCase
import co.finema.etdassi.common.data.VCDocument
import co.finema.etdassi.common.data.VCDocumentRepository
import co.finema.etdassi.common.helper.KeyHelper
import co.finema.etdassi.common.helper.replaceDataNewLineJson
import co.finema.etdassi.common.repository.CallApi
import co.finema.etdassi.common.utils.DateConvertUtil.toShortDateTime
import co.finema.etdassi.common.utils.JWTUtils
import co.finema.etdassi.feature.getvc.usecase.GetVCUseCase
import co.finema.etdassi.feature.mysign.data.VCSigned
import co.finema.etdassi.feature.mysign.data.VCSignedRepository
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetBackupVCUseCase(
    private val callApi: CallApi,
    private val keyHelper: KeyHelper,
    private val vcSignedRepository: VCSignedRepository,
    private val vcDocumentRepository: VCDocumentRepository
): SimpleCoroutineUseCase<String, GetBackupVCUseCase.ItemRestore>(){

    data class VCBackupResponse(
        @SerializedName("page") val page: Int,
        @SerializedName("total") val total: Int,
        @SerializedName("limit") val limit: Int,
        @SerializedName("count") val count: Int,
        @SerializedName("items") val vcItems: List<VCBackup>
    )

    data class VCBackup(
        @SerializedName("id") val id: String,
        @SerializedName("cid") val cid: String,
        @SerializedName("schema_type") val schemaType: String,
        @SerializedName("issuance_date") val issuanceDate: String,
        @SerializedName("jwt") val jwt: String,
        @SerializedName("issuer") val issuer: String,
        @SerializedName("holder") val holder: String,
        @SerializedName("status") val status: String
    )

    open class ItemRestore(
        val vc: VCRestore,
        val vcSigned: VCRestore
    ) {
        data class VCRestore(
            val success: Int,
            val error: Int
        )
    }

    override suspend fun executes(param: String): ItemRestore {
        return withContext(Dispatchers.IO) {
            val didSigned = replaceDataNewLineJson(keyHelper.sign(param))
            val responseResult = callApi.call(didSigned).getVCSignedBackup(param, 1).blockingGet()
            var maxPage = responseResult.total.toDouble()/responseResult.limit.toDouble()
            val vcItemList = ArrayList<VCBackup>()
            vcItemList.addAll(responseResult.vcItems)
            if (maxPage > 1) {
                if (maxPage>maxPage.toInt()) maxPage += 1
                for (i in 2..maxPage.toInt()) {
                    val vcItemListLoop = callApi.call(didSigned).getVCSignedBackup(param, i).blockingGet()
                    vcItemList.addAll(vcItemListLoop.vcItems)
                }
            }
            var vcSuccess = 0
            var vcError = 0
            var vcSignedSuccess = 0
            var vcSignedError = 0
            vcItemList.forEach { vcBackup ->

                /**
                 *
                 * START
                 */
                val jwtDecoded = JWTUtils.decodedJWT(vcBackup.jwt)
                val schemaModel = jwtDecoded?.let { it->
                    JWTUtils.jwtConvertToSchemaModel(it)
                }
                if (schemaModel?.issuer == param && schemaModel.holder == param) {

                    val verifyResponse = callApi.call()
                        .verifyVC(GetVCUseCase.VCVerifyRequest(vcBackup.jwt))
                        .blockingGet()

                    if (verifyResponse.verificationResult && verifyResponse.sStatus?.lowercase() == "active") {

                        val body = VCSigned(
                            id = vcBackup.cid,
                            name = schemaModel?.vc?.type?.lastOrNull() ?:"",
                            signDate = schemaModel?.nbf?.toShortDateTime() ?:"",
                            isRead = false,
                            notificationId = null,
                            issuer = param,
                            status = vcBackup.status,
                            tags = null,
                            revokedAt = null,
                            jwt = vcBackup.jwt,
                            backupStatus = true,
                            vcHash = null
                        )
                        vcSignedRepository.createVCSigned(body)
                        vcSignedSuccess += 1
                        //VC DOC
                        val credentialSubject = schemaModel.vc?.credentialSubject?.let {
                            JWTUtils.credentialSubjectToAttributeModel(
                                it
                            )
                        }

                        val vcDocument = VCDocument(
                            cid = vcBackup.cid,
                            status = vcBackup.status,
                            issuanceDate = schemaModel?.nbf?.toShortDateTime() ?:"",
                            expirationDate = null,
                            type = schemaModel?.vc?.type?.lastOrNull() ?:"",
                            issuer = vcBackup.issuer,
                            holder = vcBackup.holder,
                            credentialSubject = credentialSubject,
                            jwt = vcBackup.jwt,
                            backupStatus = true,
                            tags = null
                        )
                        vcDocumentRepository.insertVCDocument(vcDocument)
                        vcSuccess += 1
                    } else {
                        vcSignedError += 1
                        vcError += 1
                    }


                } else if (schemaModel?.holder == param) {

                    val verifyResponse = callApi.call()
                        .verifyVC(GetVCUseCase.VCVerifyRequest(vcBackup.jwt))
                        .blockingGet()

                    if (verifyResponse.verificationResult && verifyResponse.sStatus?.lowercase() == "active") {
                        val credentialSubject = schemaModel?.vc?.credentialSubject?.let {
                            JWTUtils.credentialSubjectToAttributeModel(
                                it
                            )
                        }
                        val body = VCDocument(
                            cid = vcBackup.cid,
                            status = vcBackup.status,
                            issuanceDate = schemaModel?.nbf?.toShortDateTime() ?:"",
                            expirationDate = null,
                            type = schemaModel?.vc?.type?.lastOrNull() ?:"",
                            issuer = vcBackup.issuer,
                            holder = vcBackup.holder,
                            credentialSubject = credentialSubject,
                            jwt = vcBackup.jwt,
                            backupStatus = true,
                            tags = null
                        )
                        vcDocumentRepository.insertVCDocument(body)
                        vcSuccess += 1
                    } else {
                        vcError += 1
                    }


                }

                /**
                 *
                 * END
                 */

//                val verifyResponse = callApi.call().verifyVC(GetVCUseCase.VCVerifyRequest(vcBackup.jwt)).blockingGet()
//                    if (verifyResponse.verificationResult && verifyResponse.sStatus == "active") {
//                        if (verifyResponse.issuer == param && verifyResponse.holder == param) {
//                            val jwtDecoded = JWTUtils.decodedJWT(vcBackup.jwt)
//                            val schemaModel = jwtDecoded?.let { it->
//                                JWTUtils.jwtConvertToSchemaModel(it)
//                            }
//
//                            val body = VCSigned(
//                                id = vcBackup.cid,
//                                name = schemaModel?.vc?.type?.lastOrNull() ?:"",
//                                signDate = schemaModel?.nbf?.toShortDateTime() ?:"",
//                                isRead = false,
//                                notificationId = null,
//                                issuer = param,
//                                status = verifyResponse.sStatus,
//                                tags = null,
//                                revokedAt = null,
//                                jwt = vcBackup.jwt,
//                                backupStatus = true,
//                                vcHash = null
//                            )
//                            vcSignedRepository.createVCSigned(body)
//                            vcSignedSuccess += 1
//                        } else if (verifyResponse.holder == param) {
//                            val jwtDecoded = JWTUtils.decodedJWT(vcBackup.jwt)
//                            val schemaModel = jwtDecoded?.let { it->
//                                JWTUtils.jwtConvertToSchemaModel(it)
//                            }
//                            val credentialSubject = schemaModel?.vc?.credentialSubject?.let {
//                                JWTUtils.credentialSubjectToAttributeModel(
//                                    it
//                                )
//                            }
//                            val body = VCDocument(
//                                cid = vcBackup.cid,
//                                status = verifyResponse.sStatus,
//                                issuanceDate = schemaModel?.nbf?.toShortDateTime() ?:"",
//                                expirationDate = null,
//                                type = schemaModel?.vc?.type?.lastOrNull() ?:"",
//                                issuer = vcBackup.issuer,
//                                holder = vcBackup.holder,
//                                credentialSubject = credentialSubject,
//                                jwt = vcBackup.jwt,
//                                backupStatus = true
//                            )
//                            vcDocumentRepository.insertVCDocument(body)
//
//                            vcSuccess += 1
//                        }
//                    } else {
//                        if (verifyResponse.holder == param && verifyResponse.holder == param) {
//                            println("mySign")
//                            vcSignedError += 1
//                        } else if (verifyResponse.issuer == param) {
//                            println("myVC")
//                            vcError += 1
//                        }
//                    }
            }
            ItemRestore(
                vc = ItemRestore.VCRestore(success = vcSuccess, error = vcError),
                vcSigned = ItemRestore.VCRestore(success = vcSignedSuccess, error = vcSignedError)
            )
        }
    }

//    private fun vcBackupResponse(didSigned: String, param: String, page: Int, mCount: Int): ItemRestore {
//
//

//
//
//
//
//
//
//
//
//
//    }
}