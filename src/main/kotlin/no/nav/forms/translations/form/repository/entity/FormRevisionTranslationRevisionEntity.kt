package no.nav.forms.translations.form.repository.entity

import jakarta.persistence.*

@Entity
@IdClass(FormRevisionTranslationRevisionId::class)
@Table(name = "form_revision_translation_revision")
data class FormRevisionTranslationRevisionEntity(
	@Id @Column(name = "form_path", columnDefinition = "varchar") val formPath: String,
	@Id @Column(name = "form_translation_revision_id", columnDefinition = "bigint") val formTranslationRevisionId: Long,
)
