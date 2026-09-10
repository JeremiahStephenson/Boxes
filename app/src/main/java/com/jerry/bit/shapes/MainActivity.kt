package com.jerry.bit.shapes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.jerry.bit.shapes.ui.theme.BoxesTheme
import com.jerry.bit.shapes.util.setEdgeToEdgeConfig
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import timber.log.Timber

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setEdgeToEdgeConfig()
        setContent {
            val appUpdateManager = koinInject<AppUpdateManager>()
            val viewModel = koinViewModel<MainViewModel>()
            val updateResultLauncher =
                rememberLauncherForActivityResult(
                    ActivityResultContracts.StartIntentSenderForResult(),
                ) { result ->
                    if (result.resultCode != RESULT_OK) {
                        Timber.i("In-app update flow ended with result code %d", result.resultCode)
                        if (result.resultCode == RESULT_CANCELED) {
                            viewModel.deferUpdatePrompt()
                        }
                    }
                }

            BoxesTheme {
                val snackbarHostState = remember { SnackbarHostState() }
                val scope = rememberCoroutineScope()
                LifecycleResumeEffect(Unit) {
                    val job =
                        scope.launch {
                            viewModel.updateInfoFlow.collect { updateFlow ->
                                when (updateFlow) {
                                    is Action.RequestUpdate -> {
                                        appUpdateManager.startUpdateFlowForResult(
                                            updateFlow.updateInfo,
                                            updateResultLauncher,
                                            AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE).build(),
                                        )
                                    }

                                    is Action.UpdateDownloaded -> {
                                        val result =
                                            snackbarHostState.showSnackbar(
                                                message = getString(R.string.update_downloaded),
                                                actionLabel = getString(R.string.restart),
                                                withDismissAction = true,
                                                duration = SnackbarDuration.Indefinite,
                                            )
                                        if (result == SnackbarResult.ActionPerformed) {
                                            appUpdateManager.completeUpdate()
                                        }
                                    }
                                }
                            }
                        }
                    onPauseOrDispose {
                        job.cancel()
                    }
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    MainContent(snackbarHostState = snackbarHostState) {
                        onBackPressedDispatcher.onBackPressed()
                    }
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
}
