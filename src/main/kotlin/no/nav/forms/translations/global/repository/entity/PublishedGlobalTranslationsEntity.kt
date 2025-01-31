package no.nav.forms.translations.global.repository.entity

import jakarta.persistence.*
import org.hibernate.Hibernate
import java.time.LocalDateTime

@Entity
@Table(name = "published_global_translation")
data class PublishedGlobalTranslationsEntity(
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "id") val id: Long? = null,
	@Column(name = "created_at", columnDefinition = "TIMESTAMP WITH TIME ZONE") val createdAt: LocalDateTime,
	@Column(name = "created_by", columnDefinition = "varchar") val createdBy: String,

	@ManyToMany(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
	@JoinTable(
		name = "published_global_translation_revision",
		joinColumns = [JoinColumn(name = "published_global_translation_id", referencedColumnName = "id")],
		inverseJoinColumns = [JoinColumn(name = "global_translation_revision_id", referencedColumnName = "id")]
	)
	val globalTranslationRevisions: List<GlobalTranslationRevisionEntity>
) {

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
		other as PublishedGlobalTranslationsEntity

		return id != null && id == other.id
	}

	override fun hashCode(): Int = javaClass.hashCode()

	@Override
	override fun toString(): String {
		return this::class.simpleName + "(id = $id , createdAt = $createdAt , createdBy = $createdBy)"
	}

}
