package no.nav.forms.translations.form.repository.entity

import jakarta.persistence.*
import no.nav.forms.forms.repository.entity.FormEntity
import org.hibernate.Hibernate
import java.time.LocalDateTime

@Entity
@Table(name = "form_translation")
data class FormTranslationEntity(
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "id") val id: Long? = null,

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "form_id", nullable = false)
	val form: FormEntity,

	@Column(name = "key", columnDefinition = "varchar") val key: String,

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "formTranslation")
	@OrderBy("created_at asc")
	val revisions: List<FormTranslationRevisionEntity>? = emptyList(),

	@Column(name = "deleted_at", columnDefinition = "TIMESTAMP WITH TIME ZONE") val deletedAt: LocalDateTime? = null,
	@Column(name = "deleted_by", columnDefinition = "varchar") val deletedBy: String? = null,
) {

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
		other as FormTranslationEntity

		return id != null && id == other.id
	}

	override fun hashCode(): Int = javaClass.hashCode()

	@Override
	override fun toString(): String {
		return this::class.simpleName + "(id = $id)"
	}
}
