package no.nav.forms.translations.form.repository.entity

import jakarta.persistence.*

@Entity
@Table(name = "form_revision_translation_revision")
data class FormRevisionTranslationRevisionEntity(
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "id") val id: Long? = null,
	@Column(name = "form_path", columnDefinition = "varchar") val formPath: String,
	@Column(name = "form_translation_revision_id", columnDefinition = "bigint") val formTranslationRevisionId: Long,
)
