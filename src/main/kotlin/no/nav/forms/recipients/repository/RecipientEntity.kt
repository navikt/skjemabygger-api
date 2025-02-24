package no.nav.forms.recipients.repository

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "recipient")
class RecipientEntity(
	@Column(name = "recipient_id", columnDefinition = "varchar")
	val recipientId: String,

	@Column(name = "name", columnDefinition = "varchar")
	var name: String,

	@Column(name = "po_box_address", columnDefinition = "varchar")
	var poBoxAddress: String,

	@Column(name = "postal_code", columnDefinition = "varchar")
	var postalCode: String,

	@Column(name = "postal_name", columnDefinition = "varchar")
	var postalName: String,

	@Column(name = "created_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
	val createdAt: LocalDateTime,

	@Column(name = "created_by", columnDefinition = "varchar")
	val createdBy: String,

	@Column(name = "changed_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
	var changedAt: LocalDateTime,

	@Column(name = "changed_by", columnDefinition = "varchar")
	var changedBy: String,

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	val id: Long? = null,
)
