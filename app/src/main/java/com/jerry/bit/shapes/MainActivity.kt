package com.jerry.bit.shapes

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.appupdate.testing.FakeAppUpdateManager
import com.google.android.play.core.install.model.AppUpdateType
import com.jerry.bit.shapes.ui.theme.BoxesTheme
import com.jerry.bit.shapes.util.setEdgeToEdgeConfig
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import timber.log.Timber

class MainActivity : ComponentActivity() {

    private val updateResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode != RESULT_OK) {
                Timber.i("In-app update flow ended with result code %d", result.resultCode)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setEdgeToEdgeConfig()
        setContent {
            val appUpdateManager = koinInject<AppUpdateManager>()
            val viewModel = koinViewModel<MainViewModel>()

            BoxesTheme {
                val snackbarHostState = remember { SnackbarHostState() }
                val updateMessage = getString(R.string.update_downloaded)
                val restartLabel = getString(R.string.restart)
                val scope = rememberCoroutineScope()

                LifecycleResumeEffect(Unit) {
                    val job = scope.launch {
                        viewModel.updateInfoFlow.collect { updateFlow ->
                            when (updateFlow) {
                                is Action.RequestUpdate -> {
                                    Log.d("UpdateTest", "Request update")
                                    appUpdateManager.startUpdateFlowForResult(
                                        appUpdateManager.appUpdateInfo.result,
                                        updateResultLauncher,
                                        AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE).build(),
                                    )
                                    (appUpdateManager as? FakeAppUpdateManager)?.run {
                                        userAcceptsUpdate()
                                        downloadCompletes()
                                    }
                                }

                                is Action.UpdateDownloaded -> {
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
