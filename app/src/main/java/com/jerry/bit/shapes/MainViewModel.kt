package com.jerry.bit.shapes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.jerry.bit.shapes.datastore.AppDataStore
import com.jerry.bit.shapes.util.SavedHandle
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import timber.log.Timber

sealed interface Action {
    data object RequestUpdate : Action
    data object UpdateDownloaded : Action
}

class MainViewModel(
    private val appUpdateManager: AppUpdateManager,
    private val savedStateHandle: SavedStateHandle,
    private val appDataStore: AppDataStore,
) : ViewModel() {
    private var updateRequestState by SavedHandle(
        savedStateHandle,
        UPDATE_REQUEST_MADE,
        false,
    )

    val updateInfoFlow =
        callbackFlow {
            val listener =
                InstallStateUpdatedListener { state ->
                    if (state.installStatus() == InstallStatus.DOWNLOADED) {
                        trySend(Action.UpdateDownloaded)
                    }
                }
            val canRequest = appDataStore.canRequestInAppUpdate()
            appUpdateManager.registerListener(listener)
            appUpdateManager.appUpdateInfo
                .addOnSuccessListener { updateInfo ->
                    when {
                        updateInfo.installStatus() == InstallStatus.DOWNLOADED -> {
                            trySend(Action.UpdateDownloaded)
                        }

                        !(updateRequestState ?: false) &&
                            updateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                            updateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE) -> {
                            if (canRequest) {
                                updateRequestState = true
                                trySend(Action.RequestUpdate)
                            }
                        }
                    }
                }.addOnFailureListener { error ->
                    // Fail silently
                    Timber.w(error, "Unable to check for an in-app update")
                }

            awaitClose {
                appUpdateManager.unregisterListener(listener)
            }
        }

    fun deferUpdatePrompt() =
        viewModelScope.launch {
            appDataStore.deferInAppUpdatePrompt()
        }

    private companion object {
        const val UPDATE_REQUEST_MADE = "update_request_made"
    }
}
