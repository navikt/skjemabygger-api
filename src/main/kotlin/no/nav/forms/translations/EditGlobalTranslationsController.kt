package no.nav.forms.translations

import no.nav.forms.api.EditGlobalTranslationsApi
import no.nav.forms.config.AzureAdConfig
import no.nav.forms.model.GlobalTranslation
import no.nav.forms.model.NewGlobalTranslationRequest
import no.nav.forms.model.UpdateGlobalTranslationRequest
import no.nav.forms.security.SecurityContextHolder
import no.nav.forms.translations.global.EditGlobalTranslationsService
import no.nav.security.token.support.core.api.ProtectedWithClaims
import no.nav.security.token.support.core.api.Unprotected
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
@ProtectedWithClaims(issuer = AzureAdConfig.ISSUER, claimMap = ["${AzureAdConfig.CLAIM_NAV_IDENT}=*"])
class EditGlobalTranslationsController(
	private val editGlobalTranslationsService: EditGlobalTranslationsService,
	private val securityContextHolder: SecurityContextHolder,
) : EditGlobalTranslationsApi {

	@Unprotected
	override fun getLatestRevisions(): ResponseEntity<List<GlobalTranslation>> {
		return ResponseEntity.ok(editGlobalTranslationsService.getLatestRevisions())
	}

	override fun createGlobalTranslation(newGlobalTranslationRequest: NewGlobalTranslationRequest): ResponseEntity<GlobalTranslation> {
		securityContextHolder.requireAdminUser()
		val userId = securityContextHolder.getUserName()
		val dto = editGlobalTranslationsService.createGlobalTranslation(
			newGlobalTranslationRequest.key,
			newGlobalTranslationRequest.tag,
			newGlobalTranslationRequest.nb,
			newGlobalTranslationRequest.nn,
			newGlobalTranslationRequest.en,
			userId
		)
		return ResponseEntity.status(HttpStatus.CREATED).body(dto)
	}

	override fun updateGlobalTranslation(
		id: Long,
		formsapiEntityRevision: Int,
		updateGlobalTranslationRequest: UpdateGlobalTranslationRequest
	): ResponseEntity<GlobalTranslation> {
		securityContextHolder.requireAdminUser()
		val userId = securityContextHolder.getUserName()
		val dto = editGlobalTranslationsService.updateGlobalTranslation(
			id,
			formsapiEntityRevision,
			updateGlobalTranslationRequest.nb,
			updateGlobalTranslationRequest.nn,
			updateGlobalTranslationRequest.en,
			userId,
		)
		return ResponseEntity.ok(dto)
	}

	override fun deleteGlobalTranslation(id: Long): ResponseEntity<Unit> {
		securityContextHolder.requireAdminUser()
		val userId = securityContextHolder.getUserName()
		editGlobalTranslationsService.deleteGlobalTranslation(
			id,
			userId,
		)
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
	}
}
