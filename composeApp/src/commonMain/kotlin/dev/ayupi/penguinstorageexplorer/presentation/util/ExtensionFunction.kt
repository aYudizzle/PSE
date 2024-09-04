package dev.ayupi.penguinstorageexplorer.presentation.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

fun Long.parseToLocalizedDateString(): String {
    val dateFormatter = if (Locale.getDefault() == Locale.GERMANY) {
        DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.getDefault())
    } else {
        DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault())
    }
    val localDate = LocalDate.ofEpochDay(this / 86_400_000L) // Convert millis to days
    val dayOfWeek = localDate.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
    return "$dayOfWeek ${localDate.format(dateFormatter)}"
}