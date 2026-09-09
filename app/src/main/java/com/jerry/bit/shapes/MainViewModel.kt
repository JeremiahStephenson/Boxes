package com.jerry.bit.shapes

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.jerry.bit.shapes.util.SavedHandle
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import timber.log.Timber

sealed interface Action {
    data object RequestUpdate : Action
    data object UpdateDownloaded : Action
}

class MainViewModel(
    private val appUpdateManager: AppUpdateManager,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private var updateRequestState by SavedHandle(
        savedStateHandle,
        UPDATE_REQUEST_MADE,
        false,
    )

    val updateInfoFlow = callbackFlow {
        val listener = InstallStateUpdatedListener { state ->
            if (state.installStatus() == InstallStatus.DOWNLOADED) {
                Log.d("UpdateTest", "Update downloaded B")
                trySend(Action.UpdateDownloaded)
            }
        }
        Log.d("UpdateTest", "starting")
        appUpdateManager.registerListener(listener)
        appUpdateManager.appUpdateInfo
            .addOnSuccessListener { updateInfo ->
                Log.d("UpdateTest", "updateInfo: ${updateInfo.installStatus()}, ${updateInfo.updateAvailability()}, ${updateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)}")
                when {
                    updateInfo.installStatus() == InstallStatus.DOWNLOADED -> {
                        Log.d("UpdateTest", "Update downloaded A")
                        trySend(Action.UpdateDownloaded)
                    }

                    !(updateRequestState ?: false) &&
                            updateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                            updateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE) -> {
                        Log.d("UpdateTest", "Trigger update")
                        updateRequestState = true
                        trySend(Action.RequestUpdate)
                    }
                }
            }.addOnFailureListener { error ->
                Log.d("UpdateTest", "Unable to check for an in-app update: $error")
                // Fail silently
                Timber.w(error, "Unable to check for an in-app update")
            }

        awaitClose {
            Log.d("UpdateTest", "awaitClose")
            appUpdateManager.unregisterListener(listener)
        }
    }

    private companion object {
        const val UPDATE_REQUEST_MADE = "update_request_made"
    }


}
