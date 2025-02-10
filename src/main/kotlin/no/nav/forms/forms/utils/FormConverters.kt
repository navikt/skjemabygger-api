package no.nav.forms.forms.utils

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.forms.forms.repository.entity.FormEntity
import no.nav.forms.forms.repository.entity.FormPublicationEntity
import no.nav.forms.forms.repository.entity.FormPublicationStatusDb
import no.nav.forms.model.FormDto
import no.nav.forms.forms.repository.entity.FormRevisionEntity
import no.nav.forms.forms.repository.entity.FormViewEntity
import no.nav.forms.model.FormCompactDto
import no.nav.forms.model.FormStatus
import no.nav.forms.utils.mapDateTime
import java.time.LocalDateTime

private val mapper = ObjectMapper()

fun FormEntity.toDto(select: List<String>? = null): FormDto =
	this.revisions.last().toDto(select)

fun FormEntity.findLatestPublication(): FormPublicationEntity? = this.publications.lastOrNull()

fun FormRevisionEntity.toDto(select: List<String>? = null): FormDto {
	val typeRefComponents = object : TypeReference<List<Map<String, Any>>>() {}
	val typeRefProperties = object : TypeReference<Map<String, Any>>() {}
	fun include(prop: String): Boolean = (select == null || select.contains(prop) == true)
	val latestPublication = this.form.findLatestPublication()
	val status = when {
		latestPublication == null -> FormStatus.draft
		latestPublication.status == FormPublicationStatusDb.Unpublished -> FormStatus.unpublished
		this.id == latestPublication.formRevision.id -> FormStatus.published
		else -> FormStatus.pending
	}
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
		status = if (include("status")) status else null,
	)
}

fun FormViewEntity.toFormCompactDto(select: List<String>? = null): FormCompactDto {
	val typeRefProperties = object : TypeReference<Map<String, Any>>() {}
	fun include(prop: String): Boolean = (select == null || select.contains(prop) == true)
	val status = when {
		this.publicationStatus == null -> FormStatus.draft
		this.publicationStatus == FormPublicationStatusDb.Unpublished -> FormStatus.unpublished
		this.currentRevisionId == this.publishedRevisionId -> FormStatus.published
		this.publishedRevisionId != null -> FormStatus.pending
		else -> null
	}
	return FormCompactDto(
		id = this.id,
		revision = if (include("revision")) this.revision else null,
		skjemanummer = if (include("skjemanummer")) this.skjemanummer else null,
		path = if (include("path")) this.path else null,
		title = if (include("title")) this.title else null,
		properties = if (include("properties")) mapper.convertValue(this.properties, typeRefProperties) else null,
		changedAt = if (include("changedAt")) mapDateTime(this.changedAt) else null,
		changedBy = if (include("changedBy")) this.changedBy else null,
		publishedAt = if (include("publishedAt") && this.publishedAt != null) mapDateTime(this.publishedAt as LocalDateTime) else null,
		publishedBy = if (include("publishedBy")) this.publishedBy else null,
		status = if (include("status")) status else null,
	)
}

fun FormPublicationEntity.toFormDto(): FormDto = this.formRevision.toDto()
