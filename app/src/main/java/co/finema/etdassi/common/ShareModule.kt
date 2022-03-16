package co.finema.etdassi.common

import co.finema.etdassi.common.base.TLSSocketFactory
import co.finema.etdassi.common.data.VCDocumentRepository
import co.finema.etdassi.common.data.VPDocumentRepository
import co.finema.etdassi.common.helper.AppDatabase
import co.finema.etdassi.common.helper.KeyHelper
import co.finema.etdassi.common.helper.KeyManager
import co.finema.etdassi.common.helper.UserHelper
import co.finema.etdassi.common.repository.CallApi
import co.finema.etdassi.feature.MainViewModel
import co.finema.etdassi.feature.bioauth.BioAuthViewModel
import co.finema.etdassi.feature.createreq.CreateRqVCViewModel
import co.finema.etdassi.feature.createreq.listofvc.ListOfVCViewModel
import co.finema.etdassi.feature.createreq.requestlist.ReqStepListViewModel
import co.finema.etdassi.feature.createreq.requestqr.ReqQrCodeSummaryViewModel
import co.finema.etdassi.feature.createreq.requestsum.ReqSummaryViewModel
import co.finema.etdassi.feature.enablelockscreen.EnableLockScreenViewModel
import co.finema.etdassi.feature.fcm.usecase.UpdateFirebaseTokenFCMUseCase
import co.finema.etdassi.feature.getvc.GetVCLoadingViewModel
import co.finema.etdassi.feature.getvc.usecase.GetVCUseCase
import co.finema.etdassi.feature.home.HomeViewModel
import co.finema.etdassi.feature.mainpager.MainPagerViewModel
import co.finema.etdassi.feature.mysign.MySignViewModel
import co.finema.etdassi.feature.mysign.data.VCSignedRepository
import co.finema.etdassi.feature.mysign.data.MyRejectRepository
import co.finema.etdassi.feature.sign.SignViewModel
import co.finema.etdassi.feature.mysign.signrequest.SignRequestViewModel
import co.finema.etdassi.feature.mysign.vcsign.VCSignedViewModel
import co.finema.etdassi.feature.myvc.myvptab.MyVPViewModel
import co.finema.etdassi.feature.myvc.myvctab.MyVCListViewModel
import co.finema.etdassi.feature.myvc.MyVCViewModel
import co.finema.etdassi.feature.mysign.myreject.MyRejectViewModel
import co.finema.etdassi.feature.mysign.myreject.detail.MyRejectDetailViewModel
import co.finema.etdassi.feature.mysign.data.SignRequestRepository
import co.finema.etdassi.feature.mysign.vcsign.VCSignedDetailViewModel
import co.finema.etdassi.feature.mysign.vcsign.VCSignedUseCase
import co.finema.etdassi.feature.myvc.myvctab.detail.VCDetailViewModel
import co.finema.etdassi.feature.myvc.myvctab.qrcodedetail.VCDetailQRViewModel
import co.finema.etdassi.feature.myvc.myvctab.qrcodedetail.usecase.VCDetailQRUseCase
import co.finema.etdassi.feature.myvc.myvctab.usecase.MyVCCheckStatusUseCase
import co.finema.etdassi.feature.myvc.myvptab.detail.MyVPDetailViewModel
import co.finema.etdassi.feature.notification.NotificationListViewModel
import co.finema.etdassi.feature.notification.data.NotificationRepository
import co.finema.etdassi.feature.pageloading.LoadingViewModel
import co.finema.etdassi.feature.pageloading.usecase.DIDRecoveryUseCase
import co.finema.etdassi.feature.pageloading.usecase.GetBackupVCUseCase
import co.finema.etdassi.feature.pageloading.usecase.VerifyVPUseCase
import co.finema.etdassi.feature.passcode.PinCodeViewModel
import co.finema.etdassi.feature.qr_reader.QRReaderViewModel
import co.finema.etdassi.feature.qr_reader.usecase.QRReaderUseCase
import co.finema.etdassi.feature.register.RegisterViewModel
import co.finema.etdassi.feature.register.detail.RegisterDetailViewModel
import co.finema.etdassi.feature.register.ewallet.ProveIdentViewModel
import co.finema.etdassi.feature.register.reset_did.ResetDidViewModel
import co.finema.etdassi.feature.register.usecase.*
import co.finema.etdassi.feature.scan_qr.RequestListViewModel
import co.finema.etdassi.feature.scan_qr.usecase.CreateVPUseCase
import co.finema.etdassi.feature.sign.usecase.RejectVCUseCase
import co.finema.etdassi.feature.sign.usecase.SignVCUseCase
import co.finema.etdassi.feature.splash.SplashViewModel
import co.finema.etdassi.feature.userprofile.UserProfileViewModel
import co.finema.etdassi.feature.userprofile.information.UserInfoViewModel
import co.finema.etdassi.feature.userprofile.settingpasscode.SettingPasscodeViewModel
import co.finema.etdassi.feature.userprofile.usecase.UserInformationUseCase
import co.finema.etdassi.feature.verifyvc.VerifyResultViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val sharedModule = module {
    factory { TLSSocketFactory() }
    single {
        val appDatabase = AppDatabase.getInstance(get())
        appDatabase.signRequestDao()
    }
    single { AppDatabase.getInstance(get()).myRejectDao() }
    single { AppDatabase.getInstance(get()).vcSignedDao() }
    single { AppDatabase.getInstance(get()).notificationDao() }
    single { AppDatabase.getInstance(get()).vcDocumentDao() }
    single { AppDatabase.getInstance(get()).vpDocumentDao() }
    single { UserHelper(androidContext()) }
    single { KeyManager(get()) }
    single { CallApi() }
    single { KeyHelper() }
    single { UpdateFirebaseTokenFCMUseCase(get(), get()) }
    single { RegisterDIDUseCase(get()) }
    single { BackupWalletUseCase(get()) }
    single { ResendEmailUseCase(get()) }
    single { RegisterCheckEmailStatusUseCase(get(), get()) }
    single { RegisterUpdateUserUseCase(get(), get(), get()) }
    single { UpdateFirebaseTokenUseCase(get()) }
    single { RegisterUserUseCase(get(), get()) }
    single { RequestSendEmailUseCase(get(), get()) }
    single { UserInformationUseCase(get(), get()) }
    single { DIDRecoveryUseCase(get(), get(), get())}
    single { GetBackupVCUseCase(get(), get(), get(), get())}


    single { SignRequestRepository(get()) }
    single { MyRejectRepository(get()) }
    single { VCSignedRepository(get()) }
    single { NotificationRepository(get()) }
    single { VCSignedUseCase(get(),get(),get(),get(),get(), get())}
    single { CheckIsBackupUseCase(get()) }


    viewModel { PinCodeViewModel(get()) }
    viewModel { RegisterDetailViewModel() }

//    viewModel { BaseViewModel(get()) }
    viewModel { MainPagerViewModel(get()) }
    viewModel { HomeViewModel(get(), get(), get(), get()) }
    viewModel { LoadingViewModel(get(), get(), get(), get(), get(), get(), get(), get(),get(),get(), get()) }
    viewModel { UserProfileViewModel(get()) }
    viewModel { UserInfoViewModel(get(), get(), get()) }
    viewModel { ProveIdentViewModel(get(), get(), get()) }
    viewModel { ResetDidViewModel(get(), get(), get(), get()) }


    viewModel { SplashViewModel(get()) }
    viewModel { SettingPasscodeViewModel() }
    viewModel { MainViewModel(get(), get(), get()) }
    viewModel { BioAuthViewModel() }
    viewModel { RegisterViewModel(get()) }
    viewModel { MyVCViewModel() }
    viewModel { VCDetailViewModel(get()) }
    viewModel { VCDetailQRViewModel(get()) }
    viewModel { MySignViewModel(get(), get(), get()) }
    viewModel { VCSignedViewModel(get()) }
    viewModel { SignRequestViewModel(get()) }
    viewModel { EnableLockScreenViewModel() }
    viewModel { CreateRqVCViewModel() }
    viewModel { ListOfVCViewModel() }
    viewModel { ReqStepListViewModel() }
    viewModel { ReqSummaryViewModel() }
    viewModel { ReqQrCodeSummaryViewModel() }
    viewModel { RequestListViewModel(get(), get()) }
    viewModel { VerifyResultViewModel() }
    viewModel { NotificationListViewModel(get()) }

    viewModel { MyRejectViewModel(get()) }

    /**
     * MY VC VP PAGE
     */
    viewModel { MyVPDetailViewModel(get(), get()) }
    viewModel { MyRejectDetailViewModel(get()) }
    viewModel { VCSignedDetailViewModel(get(),get()) }
    viewModel { MyVCListViewModel(get(), get()) }
    viewModel { MyVPViewModel(get()) }

    /**
     * GET VC
     */
    viewModel { GetVCLoadingViewModel(get()) }
    single { GetVCUseCase(get(),get(), get(), get()) }
    single { VCDocumentRepository(get()) }

    /**
     * SCAN QR
     */
    viewModel { QRReaderViewModel(get()) }
    single { QRReaderUseCase(get()) }


    /**
     * CREATE VP
     */
    single { VPDocumentRepository(get()) }
    single { CreateVPUseCase(get(), get(), get(), get(), get()) }


    /**
     * SIGN VC
     */
    viewModel { SignViewModel(get(), get(), get(),get()) }
    single { SignVCUseCase(get(), get(), get(), get(), get()) }
    single { RejectVCUseCase(get(), get(), get(), get()) }

    /**
     * CREATE QR
     */

    single { VCDetailQRUseCase(get(), get(), get(), get()) }
    single { VerifyVPUseCase(get()) }

    /**
     * Check VC Status
     */
    single { MyVCCheckStatusUseCase(get(), get()) }







//    //api & network
//    single(named("loggerInterceptor")) {
//        val logger = HttpLoggingInterceptor()
//        logger.level = HttpLoggingInterceptor.Level.BODY
//        logger
//    }
//    single(named("apiInterceptor")) { ApiClientInterceptor(get()) }
//    single<OkHttpClient>(named("apiClient")) {
//        val tlsFactory: TLSSocketFactory = get()
//        val httpClient = OkHttpClient.Builder()
//        try {
//            httpClient.sslSocketFactory(tlsFactory, tlsFactory.trustManager!!)
//        } catch (e: Exception) {
//        }
//        httpClient.callTimeout(30, TimeUnit.SECONDS)
//        httpClient.readTimeout(30, TimeUnit.SECONDS)
//        httpClient.addInterceptor(get(named("apiInterceptor")))
//        httpClient.addInterceptor(get(named("loggerInterceptor")))
//        httpClient.build()
//    }
//
//    single<Api> {
//        val retrofit = Retrofit.Builder()
//            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//            .addConverterFactory(GsonConverterFactory.create())
//            .client(get(named("apiClient")))
//            .build()
//
//        retrofit.create(Api::class.java)
//    }



}
