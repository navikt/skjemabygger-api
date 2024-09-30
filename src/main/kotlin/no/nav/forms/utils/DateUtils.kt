package no.nav.forms.utils

import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId

fun mapDateTime(ldt: LocalDateTime): OffsetDateTime =
	ldt.atZone(ZoneId.of("CET")).toOffsetDateTime()
