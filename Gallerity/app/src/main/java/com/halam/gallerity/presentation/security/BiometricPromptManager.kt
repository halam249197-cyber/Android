package com.halam.gallerity.presentation.security

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

class BiometricPromptManager(
    private val activity: FragmentActivity
) {
    fun showBiometricPrompt(
        title: String,
        description: String,
        onSuccess: () -> Unit,
        onFallbackToPin: () -> Unit,
        onError: (String) -> Unit
    ) {
        val executor = ContextCompat.getMainExecutor(activity)
        
        val biometricManager = BiometricManager.from(activity)
        val canAuthenticate = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK)
        
        if (canAuthenticate != BiometricManager.BIOMETRIC_SUCCESS) {
            // Hardware missing, unsupported or no fingerprints enrolled. Fallback to PIN immediately.
            onFallbackToPin()
            return
        }

        val biometricPrompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    // If user manually presses the Negative button, errorCode == ERROR_NEGATIVE_BUTTON
                    if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                        onFallbackToPin()
                    } else {
                        onError(errString.toString())
                    }
                }
                
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onSuccess()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    onError("Authentication failed. Try again or use PIN.")
                }
            }
        )
        
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setDescription(description)
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK)
            .setNegativeButtonText("Use PIN code")
            .build()
            
        biometricPrompt.authenticate(promptInfo)
    }
}
