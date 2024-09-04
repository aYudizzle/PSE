package dev.ayupi.penguinstorageexplorer.ui.util

import java.time.LocalDate

fun LocalDate.formatDate(): String {
    val day = this.dayOfMonth.toString().padStart(2, '0')
    val month = this.monthValue.toString().padStart(2, '0')
    val year = this.year
    return "$day.$month.$year"
}