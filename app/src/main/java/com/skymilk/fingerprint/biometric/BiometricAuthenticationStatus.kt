package com.skymilk.fingerprint.biometric

//생체인식 상태 정보
enum class BiometricAuthenticationStatus(val id: Int) {
    READY(1),
    NOT_AVAILABLE(-1),
    TEMPORARY_NOT_AVAILABLE(-2),
    AVAILABLE_BUT_NOT_ENROLLED(-3)
}