package no.nav.forms.forms.repository.entity

import jakarta.persistence.*
import org.hibernate.Hibernate
import java.time.LocalDateTime

@Entity
@Table(name = "form")
data class FormEntity(
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "id") val id: Long? = null,
	@Column(name = "skjemanummer", columnDefinition = "varchar", nullable = false) val skjemanummer: String,
	@Column(name = "path", columnDefinition = "varchar", nullable = false) val path: String,
	@Column(
		name = "created_at",
		columnDefinition = "TIMESTAMP WITH TIME ZONE",
		nullable = false
	) val createdAt: LocalDateTime,
	@Column(name = "created_by", columnDefinition = "varchar", nullable = false) val createdBy: String,
	@Column(name = "deleted_at", columnDefinition = "TIMESTAMP WITH TIME ZONE") val deletedAt: LocalDateTime? = null,
	@Column(name = "deleted_by", columnDefinition = "varchar") val deletedBy: String? = null,

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "form")
	@OrderBy("created_at asc")
	val revisions: List<FormRevisionEntity> = emptyList(),
) {

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
		other as FormEntity

		return id != null && id == other.id
	}

	override fun hashCode(): Int = javaClass.hashCode()

	@Override
	override fun toString(): String {
		return this::class.simpleName + "(id = $id, skjemanummer = $skjemanummer, createdAt = $createdAt)"
	}

}
