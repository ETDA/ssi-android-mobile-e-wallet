package co.finema.etdassi.common.utils

import android.Manifest
import android.app.Activity
import android.app.KeyguardManager
import android.content.Context
import android.content.Context.KEYGUARD_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.biometrics.BiometricManager.Authenticators.BIOMETRIC_STRONG
import android.hardware.biometrics.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import android.os.CancellationSignal
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import co.finema.etdassi.R


class BiometricUtil  {

    private lateinit var biometricPrompt: androidx.biometric.BiometricPrompt
    private lateinit var promptInfo: androidx.biometric.BiometricPrompt.PromptInfo
    private lateinit var context: Context
    private lateinit var fragment: Fragment
    private lateinit var biometricListener: BiometricListener
    private lateinit var startForResult: ActivityResultLauncher<Intent>
    private var repeatCountFail = 0
    private var isLogin = false

    interface BiometricListener {
        fun onSuccess()
        fun onFail()
    }

    fun build(fragment: Fragment, repeatCountFail: Int = 0, startForResult: ActivityResultLauncher<Intent>, param: BiometricListener) {
        this.context = fragment.requireContext()
        this.fragment = fragment
        this.biometricListener = param
        this.startForResult = startForResult
        this.repeatCountFail = repeatCountFail
        this.isLogin = repeatCountFail == 112
        if (isFeatureAvailableScan()) {
//            usePassCodeHardWare()
            createFingerPrintDialog()
        } else {
//            openPermissionCheck(requestCameraPermission)
            usePassCodeHardWare()
        }
    }

    private fun isFeatureAvailableScan(): Boolean {
        Log.e("Permission ScanFace", "${isCheckBiometric()}")
        return isCheckBiometric()
    }

    private fun isCheckBiometric(): Boolean {
        val biometricManager = BiometricManager.from(context)//BiometricManager.from(context)
        var checkBiometric = false
        Log.e("isCheckBiometric", "${biometricManager.canAuthenticate()}")
        when (biometricManager.canAuthenticate()) {
            BiometricManager.BIOMETRIC_SUCCESS -> checkBiometric = true
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                context.toastNormal("BIOMETRIC_ERROR_NONE_ENROLLED")
                Log.e("isCheckBiometric", "BIOMETRIC_ERROR_NONE_ENROLLED")
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                context.toastNormal("BIOMETRIC_ERROR_NO_HARDWARE")
                Log.e("isCheckBiometric", "BIOMETRIC_ERROR_NO_HARDWARE")
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                context.toastNormal("BIOMETRIC_ERROR_NO_HARDWARE")
                try {
                    if (Build.VERSION.SDK_INT == 28) initScannerP()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            else -> usePassCodeHardWare()
        }
        return checkBiometric
//        val TAG = "isCheckBiometric"
//        Log.d(TAG, "checkForBiometrics started")
//        var canAuthenticate = true
//        if (Build.VERSION.SDK_INT < 29) {
//            val keyguardManager : KeyguardManager = context.getSystemService(KEYGUARD_SERVICE) as KeyguardManager
//            val packageManager : PackageManager   = context.packageManager
//            if(!packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)) {
//                Log.w(TAG, "checkForBiometrics, Fingerprint Sensor not supported")
////                if (packageManager.hasSystemFeature(PackageManager.FEATURE_FACE)) {
////                    Log.w(TAG, "checkForBiometrics, Face Authentication supported")
////                } else {
////                    canAuthenticate = false
////                }
//
//                try {
//                    BiometricPrompt.Builder(context)
//                        .setTitle("For using finger print")
//                        .setDescription("Place your finger on finger print scanner")
//                        .setNegativeButton("Cancel", {
//                            it.run()
//                        }, { dialog, _ ->
//                            dialog.dismiss()
//                        })
//                        .build()
//                        .authenticate(cancellationSignal, {
//                            it.run()
//                        }, callback)
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                    canAuthenticate = true
//                }
//
//            }
//            if (!keyguardManager.isKeyguardSecure) {
//                Log.w(TAG, "checkForBiometrics, Lock screen security not enabled in Settings")
//                canAuthenticate = false
//            }
//        } else {
//            val biometricManager : BiometricManager = context.getSystemService(BiometricManager::class.java)
//            if(biometricManager.canAuthenticate() != BiometricManager.BIOMETRIC_SUCCESS){
//                Log.w(TAG, "checkForBiometrics, biometrics not supported")
//                canAuthenticate = false
//            }
//        }
//        Log.d(TAG, "checkForBiometrics ended, canAuthenticate=$canAuthenticate ")
//        return canAuthenticate
    }

    private fun usePassCodeHardWare(){
        Log.e("usePassCodeHardWare", "usePassCodeHardWare")
        val km: KeyguardManager = (context as Activity).getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        if (km.isKeyguardSecure) {
            val i = km.createConfirmDeviceCredentialIntent("Authentication required", "password")
            startForResult.launch(i)
        } else {
            Toast.makeText(
                context,
                "No any security setup done by user(pattern or password or pin or fingerprint",
                Toast.LENGTH_SHORT
                          ).show()
        }
    }

    fun openPermissionCheck(requestCameraPermission : Int) {
        Log.e("CheckPermission", "openPermission")
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_BIOMETRIC) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(context as Activity, arrayOf(Manifest.permission.USE_BIOMETRIC, Manifest.permission.USE_FINGERPRINT), requestCameraPermission)
        }
    }

    fun createFingerPrintDialog() {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> initScannerQ() //initScannerQ()
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.P -> initScannerP() //initScannerP()
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun initScannerP() {
        Log.e("Permission_Version 28+", "initScannerP")

        val callback = object : BiometricPrompt.AuthenticationCallback() {

            override fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence?) {
                super.onAuthenticationHelp(helpCode, helpString)
                Log.e("FingerPrint", "onAuthenticationHelp ($helpCode) : $helpString")
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
                super.onAuthenticationError(errorCode, errString)
                repeatCountFail += 1
                if (repeatCountFail >2 && !isLogin) usePassCodeHardWare()
                Toast.makeText(context, errString, Toast.LENGTH_SHORT).show()
                Log.e("FingerPrint", "onAuthenticationError ($errorCode) : $errString")
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Log.d("FingerPrint", "onAuthenticationFailed")
//                Snackbar.make(
//                    view!!,
//                    "Authentication fail, please try again",
//                    Snackbar.LENGTH_SHORT
//                ).show()
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
                super.onAuthenticationSucceeded(result)
                biometricListener.onSuccess()
//            onNavigateToRegisterDID()

            }

        }
        val cancellationSignal = CancellationSignal()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Log.e("Permission_Version 29+", "initScannerP")
            BiometricPrompt.Builder(context)
                .setDeviceCredentialAllowed(true)
                .setTitle("For using finger print")
                .setDescription("Place your finger on finger print scanner")
                .setNegativeButton("Cancel", {
                    it.run()
                }, { dialog, _ ->
                    dialog.dismiss()
                })
                .build()
                .authenticate(cancellationSignal, {
                    it.run()
                }, callback)
        } else {
            BiometricPrompt.Builder(context)
                .setTitle("For using finger print")
                .setDescription("Place your finger on finger print scanner")
                .setNegativeButton("Cancel", {
                    it.run()
                }, { dialog, _ ->
                    dialog.dismiss()
                })
                .build()
                .authenticate(cancellationSignal, {
                    it.run()
                }, callback)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun initScannerQ() {
        Log.e("Permission_Version 29+", "initScannerQ")

        val executor = ContextCompat.getMainExecutor(context)

        biometricPrompt = androidx.biometric.BiometricPrompt(fragment, executor,
            object : androidx.biometric.BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(
                    result: androidx.biometric.BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)
                    biometricListener.onSuccess()
//                    onNavigateToRegisterDID()
                }

                override fun onAuthenticationError(
                    errorCode: Int, errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    repeatCountFail += 1
                    if (repeatCountFail >2 && !isLogin) usePassCodeHardWare()
                    Toast.makeText(context, errString, Toast.LENGTH_SHORT).show()
                    Log.e("Face_Finger", "onAuthenticationError ($errorCode) : $errString")
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    repeatCountFail += 2
                    Log.e("Face_Finger", "onAuthenticationFailed")
//                    Snackbar.make(
//                        view!!,
//                        "Authentication fail, please try again",
//                        Snackbar.LENGTH_SHORT
//                    ).show()
                }
            })

        promptInfo = androidx.biometric.BiometricPrompt.PromptInfo.Builder()
            .setTitle("Face ID & Finger Print")
            .setNegativeButtonText(context.getString(R.string.cancel))
            .setDeviceCredentialAllowed(false)
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

}