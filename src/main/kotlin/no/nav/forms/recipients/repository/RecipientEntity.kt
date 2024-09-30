package no.nav.forms.recipients.repository

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "recipients")
data class RecipientEntity(
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "id") val id: Long?,
	@Column(name = "recipientid", columnDefinition = "varchar") val recipientId: String,
	@Column(name = "name", columnDefinition = "varchar") val name: String,
	@Column(name = "poboxaddress", columnDefinition = "varchar") val poBoxAddress: String,
	@Column(name = "postalcode", columnDefinition = "varchar") val postalCode: String,
	@Column(name = "postalname", columnDefinition = "varchar") val postalName: String,
	@Column(name = "archivesubjects", columnDefinition = "varchar", nullable = true) val archiveSubjects: String?,
	@Column(name = "createdat", columnDefinition = "TIMESTAMP WITH TIME ZONE") val createdAt: LocalDateTime,
	@Column(name = "createdby", columnDefinition = "varchar") val createdBy: String,
	@Column(name = "changedat", columnDefinition = "TIMESTAMP WITH TIME ZONE") val changedAt: LocalDateTime,
	@Column(name = "changedby", columnDefinition = "varchar") val changedBy: String,
)
