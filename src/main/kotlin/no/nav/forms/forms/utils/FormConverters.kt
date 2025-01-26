package no.nav.forms.forms.utils

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.forms.forms.repository.entity.FormEntity
import no.nav.forms.forms.repository.entity.FormPublicationEntity
import no.nav.forms.model.FormDto
import no.nav.forms.forms.repository.entity.FormRevisionEntity
import no.nav.forms.utils.mapDateTime

private val mapper = ObjectMapper()

fun FormEntity.toDto(select: List<String>? = null): FormDto = this.revisions.last().toDto(select)

fun FormRevisionEntity.toDto(select: List<String>? = null): FormDto {
	val typeRefComponents = object : TypeReference<List<Map<String, Any>>>() {}
	val typeRefProperties = object : TypeReference<Map<String, Any>>() {}
	fun include(prop: String): Boolean { return select == null || select.contains(prop) == true}
	val latestPublication = this.publications.lastOrNull()
	return FormDto(
		id = this.form.id!!,
		revision = if (include("revision")) this.revision else null,
		skjemanummer = if (include("skjemanummer")) this.form.skjemanummer else null,
		path = if (include("path")) this.form.path else null,
		title = if (include("title")) this.title else null,
		components = if (include("components")) mapper.convertValue(this.components, typeRefComponents) else null,
		properties = if (include("properties")) mapper.convertValue(this.properties, typeRefProperties) else null,
		createdAt = if (include("createdAt")) mapDateTime(this.form.createdAt) else null,
		createdBy = if (include("createdBy")) this.form.createdBy else null,
		changedAt = if (include("changedAt")) mapDateTime(this.createdAt) else null,
		changedBy = if (include("changedBy")) this.createdBy else null,
		publishedAt = if (include("publishedAt") && latestPublication != null) mapDateTime(latestPublication.createdAt) else null,
		publishedBy = if (include("publishedBy") && latestPublication != null) latestPublication.createdBy else null,
	)
}

fun FormPublicationEntity.toFormDto(): FormDto = this.formRevision.toDto()
