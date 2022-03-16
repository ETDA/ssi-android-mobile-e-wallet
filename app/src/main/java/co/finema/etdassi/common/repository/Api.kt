package co.finema.etdassi.common.repository

import co.finema.etdassi.common.utils.BuildJson
import co.finema.etdassi.feature.getvc.usecase.GetVCUseCase
import co.finema.etdassi.feature.mysign.vcsign.VCSignedUseCase
import co.finema.etdassi.feature.myvc.myvctab.qrcodedetail.usecase.VCDetailQRUseCase
import co.finema.etdassi.feature.myvc.myvctab.usecase.MyVCCheckStatusUseCase
import co.finema.etdassi.feature.pageloading.usecase.DIDRecoveryUseCase
import co.finema.etdassi.feature.pageloading.usecase.GetBackupVCUseCase
import co.finema.etdassi.feature.pageloading.usecase.ResetDIDUseCase
import co.finema.etdassi.feature.pageloading.usecase.VerifyVPUseCase
import co.finema.etdassi.feature.qr_reader.usecase.QRReaderUseCase
import co.finema.etdassi.feature.register.usecase.*
import co.finema.etdassi.feature.scan_qr.usecase.CreateVPUseCase
import co.finema.etdassi.feature.sign.usecase.SignVCUseCase
import co.finema.etdassi.feature.userprofile.usecase.UserInformationUseCase
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import io.reactivex.Single
import retrofit2.http.*

interface Api {

    /**
     * REGISTER
     */
    @POST("api/mobile/users")
    fun registerUser(
        @Body register: RegisterUserModel.RequestRegisterUser
                    ):Single<RegisterUserModel.ResponseRegisterUser>

    @POST("api/mobile/users/{id}/otp/resend")
    fun resendEmailVerify(
        @Path("id") userId: String
    ): Single<ResendEmailUseCase.Response>


    @POST("api/mobile/users/{id}/otp/confirm")
    fun confirmOTP(
        @Path("id") userId: String,
        @Body requestBody: RegisterCheckEmailStatusUseCase.Request
    ): Single<RegisterCheckEmailStatusUseCase.ConfirmOTPResponse>

    @POST("did")
    fun registerDID(
        @Body register: RequestMessage
    ):Single<ResponseRegisterDID>

    @PUT("api/mobile/users/{id}")
    fun updateUserDID(
        @Path("id") userId: String,
        @Body requestBody: RequestMessage?
    ): Single<RegisterUpdateUserUseCase.Response>

    @PUT("api/mobile/devices/{id}/token")
    fun updateDeviceToken(
        @Path("id") userId: String,
        @Body requestBody: UpdateFirebaseTokenUseCase.Request
    ): Single<UpdateFirebaseTokenUseCase.Response>

    /**
     * BACKUP_WALLET
     */

    @GET("api/mobile/did_address")
    fun registerDIDRecovery(): Single<BackupWalletUseCase.RegisterDIDRecoveryResponse>

    @GET("did/{did}/nonce")
    fun getNonce(
        @Path("did") did: String
    ): Single<BackupWalletUseCase.NonceResponse>

    @POST("did/{did}/recoverer/register")
    fun addRecovererId(
        @Path("did") did: String,
        @Body requestBody: RequestMessage
    ): Single<ResponseRegisterDID>

    @POST ("api/wallet")
    fun createBackupWallet(
        @Body register: RequestMessage
    ):Single<ResponseRegisterVCWallet>

    /**
     * CHANGE_DEVICE
     */

    @POST("api/mobile/users/{id}/otp/resend")
    fun requestToSendEmail(
        @Path("id") id: String
    ): Single<RequestSendEmailUseCase.Response>
    //@Body requestBody: RequestSendEmailUseCase.Request

    @PUT("api/mobile/otp/{id}")
    fun resendOTPEmail(
        @Path("id") id: String,
        @Body requestBody: RequestSendEmailUseCase.Request
    ): Single<ResendEmailUseCase.Response>

    @POST("api/mobile/users/{did}/recovery")
    fun didRecovery(
        @Path("did") did: String,
        @Body requestBody: DIDRecoveryUseCase.RequestRecoveryUser
    ): Single<DIDRecoveryUseCase.Response>

    @GET("vc/wallet/{did}")
    fun getBackupVCWallet(
        @Path("did") did: String
    ): Single<JsonObject>
//    ): Single<GetBackupVCUseCase.Response>

    @POST("did/{did}/keys/reset")
    fun resetDIDRecovery(
        @Body requestBody: RequestMessage
    ): Single<ResetDIDUseCase.Response>

    /**
     * USER_PROFILE
     */

    @GET("api/mobile/users/{did}")
    fun getUserInformation(
            @Path("did") did: String
    ): Single<UserInformationUseCase.Response>


    @POST
    fun registerDevice(
        @Url url: String,
        @Body register: RequestMessage
    ):Single<ResponseRegisterDevice>

    /**
     * GET VC
     */
    @GET
    fun getVC(
        @HeaderMap headers: Map<String, String>,
        @Url url: String?
    ): Single<GetVCUseCase.JWTResponse>

    /**
     * GET VC Verify
     */

    @POST("vc/verify")
    fun verifyVC(
        @Body jwtRequestModel: GetVCUseCase.VCVerifyRequest
    ): Single<GetVCUseCase.VCVerifyResponse>

    /**
     * Requesting VP
     */

    @GET
    fun getVPRequestDocument(
        @Url url: String?
    ): Single<QRReaderUseCase.RequestingVPDocResponse>

    @GET("/did/{did}/document/latest")
    fun getDidDoc(
        @Path("did") did: String
    ): Single<CreateVPUseCase.DidDocResponse>

    @POST
    fun sendVP(
        @Url endpoint: String,
        @Body body: CreateVPUseCase.Request
    ): Single<CreateVPUseCase.Response>

    /**
     * Sign vc
     */

    @POST("/vc")
    fun registerVC(
        @Body message: RequestMessage
    ): Single<SignVCUseCase.RegisterVCResponse>

    @POST("/vc/status")
    fun signVCActive(
        @Body body: RequestMessage
    ): Single<SignVCUseCase.SignVCResponse>

    @POST
    fun vcApprove(
        @Url url: String?,
        @Body body: RequestMessage
    ): Single<SignVCUseCase.ApproveResponse>

    @POST
    fun rejectVC(
        @Url url: String?,
        @Body body: RequestMessage
    ): Single<SignVCUseCase.ApproveResponse>

    @PUT("vc/status/{id}")
    fun revokeSigned(
        @Path("id") userId: String,
        @Body body: RequestMessage
    ): Single<VCSignedUseCase.RevokeVCResponse>
    /**
     *
     * verify VC Doc
     *
     */

    @POST("/api/mobile/verify")
    fun createQRForVerify(
        @Body body: RequestMessage
    ): Single<VCDetailQRUseCase.Response>

    @GET
    fun getJWTFromUrl(
        @Url url: String
    ): Single<VerifyVPUseCase.VPJWTResponse>

    @POST("/vp/verify")
    fun qrVerifyJWT(
        @Body body: RequestMessage
    ): Single<VerifyVPUseCase.QRVerifyJWTResponse>

    /**
     * Backup
     */

    @GET("/api/wallet/{did}/vcs/{cid}")
    fun checkCidIsExist(
        @Path("did") did: String,
        @Path("cid") cid: String
    ): Single<BackupCheckIsBackup>

    @POST("/api/wallet/{did}/vcs")
    fun backupVCToWallet(
        @Path("did") did: String,
        @Body body: RequestMessage
    ): Single<ResponseResult>

    @GET("/api/wallet/{did}/vcs")
    fun getVCSignedBackup(
        @Path("did") did: String,
        @Query("page") page: Int? = 1
    ): Single<GetBackupVCUseCase.VCBackupResponse>

    @GET("/api/wallet/{did}")
    fun checkIsEWallet(
        @Path("did") did: String
    ): Single<BackupCheckIsBackup>


    /**
     * Check status
     */

    @GET("/vc/status")
    fun cidCheckStatus(
        @Query("cid") cidStrip: String
    ): Single<MyVCCheckStatusUseCase.CIDStatusResponse>












    @POST
    fun addNewKeyDID(
        @Url url: String,
        @Body addnewKey: RequestMessage
    ):Single<ResponseAddNewKeyDID>

    @POST
    fun revokeKeyDID(
        @Url url: String,
        @Body revoke: RequestMessage
    ):Single<ResponseRevokeKeyDID>

    @GET
    fun getDocumentDID(
        @Url url: String
    ):Single<ResponseDocumentDID>

    @GET
    fun getDocumentHistoryDID(
        @Url url: String
    ):Single<ResponseDocumentHistoryDID>

    @GET
    fun getDocumentVersionDID(
        @Url url: String
    ):Single<ResponseDocumentVersionDID>

    data class RequestMessage(
        @SerializedName("message") val message: String
    )

    data class ResponseRegisterVCWallet(
        @SerializedName("result") val result: String
    )

    data class ResponseResult(
        @SerializedName("result") val result: String
    )

    data class ResponseRegisterDevice(
        @SerializedName("result") val result: String
    )

    data class BackupCheckIsBackup(
        @SerializedName("is_exists") val isExists: Boolean
    )

    data class BackupRequestBody(
        @SerializedName("operation") val operation: String = "WALLET_VC_ADD",
        @SerializedName("did_address") val didAddress: String,
        @SerializedName("JWT") val jwt: String
    ): BuildJson

    data class ResponseRegisterDID(
        @SerializedName("@context") val context: String,
        @SerializedName("id") val did: String,
        @SerializedName("controller") val controller: String,
        @SerializedName("VerificationMethod") val verificationMethod: List<ResponsePublicKey>?,
        @SerializedName("versionId") val versionId: String
    )

    data class ResponsePublicKey(
        @SerializedName("id") val id: String,
        @SerializedName("type") val type: String,
        @SerializedName("controller") val controller: String,
        @SerializedName("publicKeyPem") val publicKeyPem: String,
    )

    data class RequestRegisterUserModel(
        @SerializedName("first_name") val firstName: String,
        @SerializedName("last_name") val lastName: String,
        @SerializedName("id_card_no") val idCard: String,
        @SerializedName("date_of_birth") val dateOfBirth: String,
        @SerializedName("laser_no") val laserId: String
                                       )

    data class ResponseRegisterEKYC(
        @SerializedName("first_name") val firstName: String,
        @SerializedName("last_name") val lastName: String,
        @SerializedName("id_card_no") val idCard: String,
        @SerializedName("date_of_birth") val dateOfBirth: String,
        @SerializedName("laser_no") val laserId: String
    )

    data class ResponseAddNewKeyDID(
        @SerializedName("@context") val context: String,
        @SerializedName("id") val did: String,
        @SerializedName("publicKey") val publicKeyResponse: List<ResponsePublicKey>,
        @SerializedName("versionId") val versionId: String
    )

    data class ResponseRevokeKeyDID(
        @SerializedName("@context") val context: String,
        @SerializedName("id") val did: String,
        @SerializedName("publicKey") val publicKeyResponse: List<ResponsePublicKey>,
        @SerializedName("versionId") val versionId: String
    )

    data class ResponseDocumentDID(
        @SerializedName("@context") val context: String,
        @SerializedName("id") val did: String,
        @SerializedName("publicKey") val publicKeyResponse: List<ResponsePublicKey>,
        @SerializedName("versionId") val versionId: String
    )

    data class ResponseDocumentHistoryDID(
        @SerializedName("@context") val context: String,
        @SerializedName("id") val did: String,
        @SerializedName("publicKey") val publicKeyResponse: List<ResponsePublicKey>,
        @SerializedName("versionId") val versionId: String
    )

    data class ResponseDocumentVersionDID(
        @SerializedName("@context") val context: String,
        @SerializedName("id") val did: String,
        @SerializedName("publicKey") val publicKeyResponse: List<ResponsePublicKey>,
        @SerializedName("versionId") val versionId: String
    )

    data class ResponseRegisterVC(
        @SerializedName("operation") val operation: String,
        @SerializedName("cid") val cid: String,
        @SerializedName("did_address") val did: String,
    )
}