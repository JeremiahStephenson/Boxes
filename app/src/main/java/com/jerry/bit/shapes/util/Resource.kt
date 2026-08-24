package com.jerry.bit.shapes.util

data class Resource<out DataType>(
    val status: Status,
    val data: DataType? = null,
    val throwable: Throwable? = null,
) {
    val isIdle get() = status == Status.IDLE
    val isSuccessful get() = status == Status.DONE
    val isLoading get() = status == Status.LOADING
    val isError get() = status == Status.ERROR

    companion object {
        fun <DataType> idle() = Resource<DataType>(Status.IDLE)

        fun <DataType> done(data: DataType) = Resource(Status.DONE, data)

        fun <DataType> loading(data: DataType? = null) = Resource(Status.LOADING, data)

        fun <DataType> error(
            throwable: Throwable?,
            data: DataType? = null,
        ) = Resource(Status.ERROR, data, throwable)
    }
}
