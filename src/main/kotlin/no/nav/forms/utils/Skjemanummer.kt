package no.nav.forms.utils

typealias Skjemanummer = String

fun Skjemanummer.toFormPath() = this.replace("[\\s.-]".toRegex(), "").lowercase()
