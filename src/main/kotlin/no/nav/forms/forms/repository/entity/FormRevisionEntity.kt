package no.nav.forms.forms.repository.entity

import com.fasterxml.jackson.databind.JsonNode
import jakarta.persistence.*
import org.hibernate.Hibernate
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.LocalDateTime

@Entity
@Table(name = "form_revision")
data class FormRevisionEntity(
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "id") val id: Long? = null,
	@Column(name = "revision", columnDefinition = "int", nullable = false) val revision: Int,

	@Column(name = "title", columnDefinition = "varchar", nullable = false) val title: String,
	@Column(
		name = "created_at",
		columnDefinition = "TIMESTAMP WITH TIME ZONE",
		nullable = false
	) val createdAt: LocalDateTime,
	@Column(name = "created_by", columnDefinition = "varchar", nullable = false) val createdBy: String,

	@Basic(fetch = FetchType.LAZY)
	@Convert(converter = DbJsonArrayConverter::class)
	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "components", columnDefinition = "jsonb", nullable = true) val components: JsonNode,

	@Basic(fetch = FetchType.LAZY)
	@Convert(converter = DbJsonObjectConverter::class)
	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "properties", columnDefinition = "jsonb", nullable = true) val properties: JsonNode,

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "form_id", nullable = false)
	val form: FormEntity,

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "formRevision")
	@OrderBy("created_at asc")
	val publications: List<FormPublicationEntity> = emptyList(),
	) {

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
		other as FormRevisionEntity

		return id != null && id == other.id
	}

	override fun hashCode(): Int = javaClass.hashCode()

	@Override
	override fun toString(): String {
		return this::class.simpleName + "(id = $id, form = $form, revision = $revision, createdAt = $createdAt)"
	}
}
