package no.nav.forms.translations.form.repository.entity

import jakarta.persistence.*

@Entity
@IdClass(FormId::class)
@Table(name = "form_revision_translation_revision")
data class FormRevisionTranslationRevisionEntity(
	@Id @Column(name = "form_path", columnDefinition = "varchar") val formPath: String,
	@Id @Column(name = "form_translation_revision_id", columnDefinition = "bigint") val formTranslationRevisionId: Long,

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "form_translation_revision_id", nullable = false)
	val revision: FormTranslationRevisionEntity,


//	@OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
//	@JoinColumn(name = "form_translation_revision_id", nullable = false)
//	@OrderBy("created_at asc")
//	val revisions: List<FormTranslationRevisionEntity>? = emptyList(),
)
