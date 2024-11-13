package no.nav.forms.translations.form.repository.entity

import jakarta.persistence.*
import no.nav.forms.translations.global.repository.entity.GlobalTranslationEntity
import org.hibernate.Hibernate
import java.time.LocalDateTime

@Entity
@Table(name = "form_translation_revision")
data class FormTranslationRevisionEntity(
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "id") val id: Long? = null,
	@Column(name = "nb", columnDefinition = "varchar") val nb: String? = null,
	@Column(name = "nn", columnDefinition = "varchar") val nn: String? = null,
	@Column(name = "en", columnDefinition = "varchar") val en: String? = null,
	@Column(name = "revision", columnDefinition = "int") val revision: Int,
	@Column(
		name = "created_at",
		columnDefinition = "TIMESTAMP WITH TIME ZONE",
		nullable = false
	) val createdAt: LocalDateTime,
	@Column(name = "created_by", columnDefinition = "varchar", nullable = false) val createdBy: String,

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "form_translation_id", nullable = false)
	val formTranslation: FormTranslationEntity,

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "global_translation_id", nullable = true)
	val globalTranslation: GlobalTranslationEntity? = null,
) {

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
		other as FormTranslationRevisionEntity

		return id != null && id == other.id
	}

	override fun hashCode(): Int = javaClass.hashCode()

	@Override
	override fun toString(): String {
		return this::class.simpleName + "(id = $id , createdAt = $createdAt , createdBy = $createdBy)"
	}
}
