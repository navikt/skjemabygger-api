package no.nav.forms.forms.repository.entity

import com.fasterxml.jackson.databind.JsonNode
import jakarta.persistence.*
import no.nav.forms.forms.repository.converter.DbJsonArrayConverter
import org.hibernate.Hibernate
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity
@Table(name = "form_revision_components")
data class FormRevisionComponentsEntity(
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	val id: Long? = null,

	@Convert(converter = DbJsonArrayConverter::class)
	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "value", columnDefinition = "jsonb", nullable = true)
	val value: JsonNode,
) {

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
		other as FormRevisionComponentsEntity

		return id != null && id == other.id
	}

	override fun hashCode(): Int = javaClass.hashCode()

	@Override
	override fun toString(): String {
		return this::class.simpleName + "(id = $id)"
	}
}
