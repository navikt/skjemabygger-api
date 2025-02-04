package no.nav.forms.translations.global.repository.entity

import jakarta.persistence.*
import org.hibernate.Hibernate
import java.time.LocalDateTime

@Entity
@Table(name = "global_translation")
data class GlobalTranslationEntity(
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "id") val id: Long? = null,
	@Column(name = "key", columnDefinition = "varchar") val key: String,
	@Column(name = "tag", columnDefinition = "varchar") val tag: String,
	@Column(name = "deleted_at", columnDefinition = "TIMESTAMP WITH TIME ZONE") val deletedAt: LocalDateTime? = null,
	@Column(name = "deleted_by", columnDefinition = "varchar") val deletedBy: String? = null,

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "globalTranslation", cascade = [CascadeType.ALL])
	@OrderBy("created_at asc")
	val revisions: Set<GlobalTranslationRevisionEntity>? = emptySet(),
) {

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
		other as GlobalTranslationEntity

		return id != null && id == other.id
	}

	override fun hashCode(): Int = javaClass.hashCode()

	@Override
	override fun toString(): String {
		return this::class.simpleName + "(id = $id , key = $key , tag = $tag)"
	}

}
