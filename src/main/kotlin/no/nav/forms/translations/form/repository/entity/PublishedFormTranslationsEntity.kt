package no.nav.forms.translations.form.repository.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import no.nav.forms.forms.repository.entity.FormEntity
import java.time.LocalDateTime

@Entity
@Table(name = "published_form_translation")
data class PublishedFormTranslationsEntity(
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "id") val id: Long? = null,
	@Column(name = "created_at", columnDefinition = "TIMESTAMP WITH TIME ZONE") val createdAt: LocalDateTime,
	@Column(name = "created_by", columnDefinition = "varchar") val createdBy: String,

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "form_id", nullable = false)
	val form: FormEntity,

	@ManyToMany(cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
	@JoinTable(
		name = "published_form_translation_revision",
		joinColumns = [JoinColumn(name = "published_form_translation_id", referencedColumnName = "id")],
		inverseJoinColumns = [JoinColumn(name = "form_translation_revision_id", referencedColumnName = "id")]
	)
	val formTranslationRevisions: List<FormTranslationRevisionEntity>
)
