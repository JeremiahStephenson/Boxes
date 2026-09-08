package com.jerry.bit.shapes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.jerry.bit.shapes.ui.theme.BoxesTheme
import com.jerry.bit.shapes.util.setEdgeToEdgeConfig
import timber.log.Timber

class MainActivity : ComponentActivity() {
    private lateinit var appUpdateManager: AppUpdateManager
    private var updateDownloaded by mutableStateOf(false)
    private var updateRequestMade = false

    private val updateStateListener =
        InstallStateUpdatedListener { state ->
            if (state.installStatus() == InstallStatus.DOWNLOADED) {
                updateDownloaded = true
            }
        }

    private val updateResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode != RESULT_OK) {
                Timber.i("In-app update flow ended with result code %d", result.resultCode)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        updateRequestMade = savedInstanceState?.getBoolean(UPDATE_REQUEST_MADE) == true
        appUpdateManager = AppUpdateManagerFactory.create(this)
        appUpdateManager.registerListener(updateStateListener)

        setEdgeToEdgeConfig()
        setContent {
            BoxesTheme {
                val snackbarHostState = remember { SnackbarHostState() }
                val updateMessage = getString(R.string.update_downloaded)
                val restartLabel = getString(R.string.restart)

                LaunchedEffect(updateDownloaded) {
                    if (updateDownloaded) {
                        val result =
                            snackbarHostState.showSnackbar(
                                message = updateMessage,
                                actionLabel = restartLabel,
                                withDismissAction = false,
                            )
                        if (result == SnackbarResult.ActionPerformed) {
                            appUpdateManager.completeUpdate()
                        }
                    }
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    MainContent { onBackPressedDispatcher.onBackPressed() }
                    SnackbarHost(
                        hostState = snackbarHostState,
                        modifier =
                            Modifier
                                .align(Alignment.BottomCenter)
                                .navigationBarsPadding(),
                    )
                }
            }
        }
        onBackPressedDispatcher.addCallback(
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    moveTaskToBack(true)
                }
            },
        )
    }

    override fun onResume() {
        super.onResume()
        checkForUpdate()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(UPDATE_REQUEST_MADE, updateRequestMade)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        appUpdateManager.unregisterListener(updateStateListener)
        super.onDestroy()
    }

    private fun checkForUpdate() {
        appUpdateManager.appUpdateInfo
            .addOnSuccessListener { updateInfo ->
                when {
                    updateInfo.installStatus() == InstallStatus.DOWNLOADED -> {
                        updateDownloaded = true
                    }

                    !updateRequestMade &&
                        updateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                        updateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE) -> {
                        updateRequestMade = true
                        appUpdateManager.startUpdateFlowForResult(
                            updateInfo,
                            updateResultLauncher,
                            AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE).build(),
                        )
                    }
                }
            }.addOnFailureListener { error ->
                Timber.w(error, "Unable to check for an in-app update")
            }
    }

    private companion object {
        const val UPDATE_REQUEST_MADE = "update_request_made"
    }
}
