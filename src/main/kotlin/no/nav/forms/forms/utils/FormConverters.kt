package no.nav.forms.forms.utils

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.forms.forms.repository.entity.FormEntity
import no.nav.forms.model.FormDto
import no.nav.forms.forms.repository.entity.FormRevisionEntity
import no.nav.forms.utils.mapDateTime

private val mapper = ObjectMapper()

fun FormEntity.toDto(): FormDto = this.revisions.last().toDto()

fun FormRevisionEntity.toDto(): FormDto {
	val typeRefComponents = object : TypeReference<List<Map<String, Any>>>() {}
	val typeRefProperties = object : TypeReference<Map<String, Any>>() {}
	return FormDto(
		id = this.form.id!!,
		revision = this.revision,
		skjemanummer = this.form.skjemanummer,
		path = this.form.path,
		title = this.title,
		components = mapper.convertValue(this.components, typeRefComponents),
		properties = mapper.convertValue(this.properties, typeRefProperties),
		createdAt = mapDateTime(this.form.createdAt),
		createdBy = this.form.createdBy,
		changedAt = mapDateTime(this.createdAt),
		changedBy = this.createdBy,
	)
}
