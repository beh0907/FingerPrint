package com.skymilk.fingerprint.biometric

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.fragment.app.FragmentActivity

//생체인식 인증 관리
class BiometricAuthenticator(
    private val context: Context
) {

    private lateinit var promptInfo: PromptInfo
    private val biometricManager = BiometricManager.from(context)
    private lateinit var biometricPrompt: BiometricPrompt

    //생체 인증 유효 체크
    private fun isBiometricAuthAvailable(): BiometricAuthenticationStatus {
        // BIOMETRIC_STRONG 은 안드로이드 11 에서 정의한 클래스 3 생체 인식을 사용하는 인증 - 암호화된 키 필요
        // BIOMETRIC_WEAK 은 안드로이드 11 에서 정의한 클래스 2 생체 인식을 사용하는 인증 - 암호화된 키까지는 불필요
        // DEVICE_CREDENTIAL 은 화면 잠금 사용자 인증 정보를 사용하는 인증 - 사용자의 PIN, 패턴 또는 비밀번호
        return when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> BiometricAuthenticationStatus.READY
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> BiometricAuthenticationStatus.NOT_AVAILABLE
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> BiometricAuthenticationStatus.TEMPORARY_NOT_AVAILABLE
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> BiometricAuthenticationStatus.AVAILABLE_BUT_NOT_ENROLLED
            else -> BiometricAuthenticationStatus.NOT_AVAILABLE
        }
    }

    //생체 인식 인증
    fun promptBiometricAuth(
        title: String, //인증 타이틀
        subTitle: String, //인증 부제
        negativeButtonText: String, // 취소 텍스트
        fragmentActivity: FragmentActivity,
        onSuccess: (result: BiometricPrompt.AuthenticationResult) -> Unit, //인증 시도 성공 콜백
        onFailed: () -> Unit, //인증 시도 실패 콜백
        onError: (errorCode: Int, errorString: String) -> Unit //인증 오류 콜백
    ) {
        when (isBiometricAuthAvailable()) {
            BiometricAuthenticationStatus.NOT_AVAILABLE -> {
                onError(BiometricAuthenticationStatus.NOT_AVAILABLE.id, "생체 인식이 지원되지 않는 단말기입니다.")
                return
            }

            BiometricAuthenticationStatus.TEMPORARY_NOT_AVAILABLE -> {
                onError(
                    BiometricAuthenticationStatus.TEMPORARY_NOT_AVAILABLE.id,
                    "현재 생체 인식 기능을 사용할 수 없습니다"
                )
                return
            }

            BiometricAuthenticationStatus.AVAILABLE_BUT_NOT_ENROLLED -> {
                onError(
                    BiometricAuthenticationStatus.AVAILABLE_BUT_NOT_ENROLLED.id,
                    "지문 혹은 얼굴 정보를 먼저 등록해야 합니다."
                )
                return
            }

            else -> Unit
        }

        //생제 인식 인증 처리
        biometricPrompt = BiometricPrompt(
            fragmentActivity,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)

                    onSuccess(result)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()

                    onFailed()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)

                    onError(errorCode, errString.toString())
                }
            }
        )

        //생체 인증 요구 알림창 정보
        promptInfo = PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subTitle)
            .setNegativeButtonText(negativeButtonText)
            .build()
        biometricPrompt.authenticate(promptInfo)
    }
}