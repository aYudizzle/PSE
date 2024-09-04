package dev.ayupi.penguinstorageexplorer.domain.model

enum class DateOfExpiryState {
    Expired,
    Warning,
    Ok,
}

fun String.toDateOfExpiryState(): DateOfExpiryState {
    return when(this) {
        "Expired" -> DateOfExpiryState.Expired
        "Warning" -> DateOfExpiryState.Warning
        else -> DateOfExpiryState.Ok

    }
}