package no.nav.forms.translations.global.repository.entity

import jakarta.persistence.*
import org.hibernate.Hibernate

@Entity
@Table(name = "global_translation")
data class GlobalTranslationEntity(
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "id") val id: Long? = null,
	@Column(name = "key", columnDefinition = "varchar") val key: String,
	@Column(name = "tag", columnDefinition = "varchar") val tag: String,

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "globalTranslation", cascade = [CascadeType.ALL])
	@OrderBy("created_at asc")
	val revisions: List<GlobalTranslationRevisionEntity>? = emptyList(),
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
