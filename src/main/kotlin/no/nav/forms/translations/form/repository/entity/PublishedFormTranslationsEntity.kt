package no.nav.forms.translations.form.repository.entity

import jakarta.persistence.*
import no.nav.forms.forms.repository.entity.FormEntity
import org.hibernate.Hibernate
import java.time.LocalDateTime

@Entity
@Table(name = "published_form_translation")
class PublishedFormTranslationsEntity(
	@Column(name = "created_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
	val createdAt: LocalDateTime,

	@Column(name = "created_by", columnDefinition = "varchar")
	val createdBy: String,

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "form_id", nullable = false)
	val form: FormEntity,

	@ManyToMany(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
	@JoinTable(
		name = "published_form_translation_revision",
		joinColumns = [JoinColumn(name = "published_form_translation_id", referencedColumnName = "id")],
		inverseJoinColumns = [JoinColumn(name = "form_translation_revision_id", referencedColumnName = "id")]
	)
	val formTranslationRevisions: Set<FormTranslationRevisionEntity>,

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	val id: Long? = null,
){

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
		other as PublishedFormTranslationsEntity

		return id != null && id == other.id
	}

	override fun hashCode(): Int = javaClass.hashCode()

	@Override
	override fun toString(): String {
		return this::class.simpleName + "(id = $id)"
	}

}
