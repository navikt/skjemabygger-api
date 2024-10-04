package no.nav.forms.recipients.repository

import com.fasterxml.jackson.databind.JsonNode
import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.LocalDateTime

@Entity
@Table(name = "recipient")
data class RecipientEntity(
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "id") val id: Long? = null,
	@Column(name = "recipient_id", columnDefinition = "varchar") val recipientId: String,
	@Column(name = "name", columnDefinition = "varchar") val name: String,
	@Column(name = "po_box_address", columnDefinition = "varchar") val poBoxAddress: String,
	@Column(name = "postal_code", columnDefinition = "varchar") val postalCode: String,
	@Column(name = "postal_name", columnDefinition = "varchar") val postalName: String,
	@Column(name = "created_at", columnDefinition = "TIMESTAMP WITH TIME ZONE") val createdAt: LocalDateTime,
	@Column(name = "created_by", columnDefinition = "varchar") val createdBy: String,
	@Column(name = "changed_at", columnDefinition = "TIMESTAMP WITH TIME ZONE") val changedAt: LocalDateTime,
	@Column(name = "changed_by", columnDefinition = "varchar") val changedBy: String,

	@Convert(converter = JsonNodeConverter::class)
	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "archive_subjects", columnDefinition = "jsonb", nullable = true) val archiveSubjects: JsonNode? = null,
)
