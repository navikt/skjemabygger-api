package no.nav.forms.forms.repository.entity

import jakarta.persistence.*
import no.nav.forms.forms.repository.converter.FormLockConverter
import no.nav.forms.forms.repository.entity.attributes.FormLockDb
import org.hibernate.Hibernate
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.LocalDateTime

@Entity
@Table(name = "form")
class FormEntity(
	@Column(name = "skjemanummer", columnDefinition = "varchar", nullable = false, updatable = false)
	val skjemanummer: String,

	@Column(name = "path", columnDefinition = "varchar", nullable = false, updatable = false)
	val path: String,

	@Column(
		name = "created_at",
		columnDefinition = "TIMESTAMP WITH TIME ZONE",
		nullable = false,
		updatable = false,
	)
	val createdAt: LocalDateTime,

	@Column(name = "created_by", columnDefinition = "varchar", nullable = false, updatable = false)
	val createdBy: String,

	@Column(name = "deleted_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
	val deletedAt: LocalDateTime? = null,

	@Column(name = "deleted_by", columnDefinition = "varchar")
	val deletedBy: String? = null,

	@Fetch(FetchMode.JOIN)
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "form")
	@OrderBy("created_at asc")
	val revisions: Set<FormRevisionEntity> = emptySet(),

	@Fetch(FetchMode.JOIN)
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "form")
	@OrderBy("created_at asc")
	val publications: Set<FormPublicationEntity> = emptySet(),

	@JdbcTypeCode(SqlTypes.JSON)
	@Convert(converter = FormLockConverter::class)
	@Column(name = "lock", columnDefinition = "jsonb", nullable = true)
	var lock: FormLockDb? = null,

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	val id: Long? = null,
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
