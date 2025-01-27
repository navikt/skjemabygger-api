package no.nav.forms.forms.repository.entity

import jakarta.persistence.*
import no.nav.forms.translations.form.repository.entity.PublishedFormTranslationsEntity
import no.nav.forms.translations.global.repository.entity.PublishedGlobalTranslationsEntity
import org.hibernate.Hibernate
import java.time.LocalDateTime

@Entity
@Table(name = "form_publication")
data class FormPublicationEntity(
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "id") val id: Long? = null,
	@Column(
		name = "created_at",
		columnDefinition = "TIMESTAMP WITH TIME ZONE",
		nullable = false
	) val createdAt: LocalDateTime,
	@Column(name = "created_by", columnDefinition = "varchar", nullable = false) val createdBy: String,

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "form_revision_id", nullable = false)
	val formRevision: FormRevisionEntity,

	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "published_form_translation_id", referencedColumnName = "id", nullable = false)
	val publishedFormTranslation: PublishedFormTranslationsEntity,

	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "published_global_translation_id", referencedColumnName = "id", nullable = false)
	val publishedGlobalTranslation: PublishedGlobalTranslationsEntity,
) {

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
		other as FormPublicationEntity

		return id != null && id == other.id
	}

	override fun hashCode(): Int = javaClass.hashCode()

	@Override
	override fun toString(): String {
		return this::class.simpleName + "(id = $id, form = ${formRevision.form.path}, createdAt = $createdAt, createdBy = $createdBy)"
	}

}
