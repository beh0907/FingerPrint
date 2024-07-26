package com.skymilk.fingerprint

import android.os.Bundle
import android.util.Log
import android.widget.Space
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.skymilk.fingerprint.biometric.BiometricAuthenticationStatus
import com.skymilk.fingerprint.biometric.BiometricAuthenticator
import com.skymilk.fingerprint.ui.theme.FingerPrintTheme

class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FingerPrintTheme {

                val biometricAuthenticator = BiometricAuthenticator(this)

                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    val activity = LocalContext.current as FragmentActivity
                    var message by remember { mutableStateOf("") }

                    TextButton(onClick = {
                        biometricAuthenticator.promptBiometricAuth(
                            title = "로그인",
                            subTitle = "당신의 생체 정보를 사용합니다.",
                            negativeButtonText = "취소",
                            fragmentActivity = activity,
                            onSuccess = { result ->
                                message = "인증 성공하였습니다."
                            },
                            onFailed = {
                                message = "잘못된 생체 정보입니다."
                            },
                            onError = { _, errorMessage ->
                                message = errorMessage
                            }
                        )
                    }) {
                        Text(text = "지문 혹은 얼굴 인식 로그인")
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    
                    Text(text = message)
                }
            }
        }
    }
}