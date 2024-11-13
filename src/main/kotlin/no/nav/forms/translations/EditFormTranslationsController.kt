package no.nav.forms.translations

import no.nav.forms.api.EditFormTranslationsApi
import no.nav.forms.config.AzureAdConfig
import no.nav.forms.model.FormTranslationDto
import no.nav.forms.model.NewFormTranslationRequestDto
import no.nav.forms.model.UpdateFormTranslationRequest
import no.nav.forms.security.SecurityContextHolder
import no.nav.forms.translations.form.EditFormTranslationsService
import no.nav.forms.translations.form.validator.validate
import no.nav.security.token.support.core.api.ProtectedWithClaims
import no.nav.security.token.support.core.api.Unprotected
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
@ProtectedWithClaims(issuer = AzureAdConfig.ISSUER, claimMap = ["${AzureAdConfig.CLAIM_NAV_IDENT}=*"])
class EditFormTranslationsController(
	val editFormTranslationsService: EditFormTranslationsService,
	private val securityContextHolder: SecurityContextHolder,
) : EditFormTranslationsApi {

	@Unprotected
	override fun getFormTranslations(formPath: String): ResponseEntity<List<FormTranslationDto>> {
		return ResponseEntity.ok(editFormTranslationsService.getTranslations(formPath))
	}

	override fun updateFormTranslation(
		formPath: String,
		id: Long,
		formsapiEntityRevision: Int,
		updateFormTranslationRequest: UpdateFormTranslationRequest
	): ResponseEntity<FormTranslationDto> {
		securityContextHolder.requireValidUser()
		updateFormTranslationRequest.validate()
		val userId = securityContextHolder.getUserName()
		val dto = editFormTranslationsService.updateTranslation(
			formPath,
			id,
			formsapiEntityRevision,
			updateFormTranslationRequest.globalTranslationId,
			updateFormTranslationRequest.nb,
			updateFormTranslationRequest.nn,
			updateFormTranslationRequest.en,
			userId,
		)
		return ResponseEntity.ok(dto)
	}

	override fun createFormTranslation(
		formPath: String,
		newFormTranslationRequestDto: NewFormTranslationRequestDto
	): ResponseEntity<FormTranslationDto> {
		securityContextHolder.requireValidUser()
		newFormTranslationRequestDto.validate()
		val userId = securityContextHolder.getUserName()
		val dto = editFormTranslationsService.createTranslation(
			formPath,
			newFormTranslationRequestDto.key,
			newFormTranslationRequestDto.globalTranslationId,
			newFormTranslationRequestDto.nb,
			newFormTranslationRequestDto.nn,
			newFormTranslationRequestDto.en,
			userId,
		)
		return ResponseEntity.ok(dto)
	}

}
