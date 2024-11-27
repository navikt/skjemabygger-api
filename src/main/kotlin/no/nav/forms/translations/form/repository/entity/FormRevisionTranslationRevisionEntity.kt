package no.nav.forms.translations.form.repository.entity

import jakarta.persistence.*
import org.hibernate.Hibernate

@Entity
@Table(name = "form_revision_translation_revision")
data class FormRevisionTranslationRevisionEntity(
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "id") val id: Long? = null,
	@Column(name = "form_path", columnDefinition = "varchar") val formPath: String,

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "form_translation_revision_id", nullable = false)
	val formTranslationRevision: FormTranslationRevisionEntity,
) {

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
		other as FormRevisionTranslationRevisionEntity

		return id != null && id == other.id
	}

	override fun hashCode(): Int = javaClass.hashCode()

	@Override
	override fun toString(): String {
		return this::class.simpleName + "(id = $id , formPath = $formPath , formTranslationRevision = $formTranslationRevision)"
	}
}

