package com.jerry.bit.shapes.ui.home

sealed class ProjectTransferEvent {
    abstract val result: Result<Long>

    data class Imported(
        override val result: Result<Long>,
    ) : ProjectTransferEvent()

    data class Pasted(
        override val result: Result<Long>,
    ) : ProjectTransferEvent()
}
