package no.nav.forms.forms

import no.nav.forms.api.FormPublicationsApi
import no.nav.forms.config.AzureAdConfig
import no.nav.forms.model.FormDto
import no.nav.forms.model.PublishedTranslationsDto
import no.nav.forms.security.SecurityContextHolder
import no.nav.forms.utils.LanguageCode
import no.nav.security.token.support.core.api.ProtectedWithClaims
import no.nav.security.token.support.core.api.Unprotected
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
@ProtectedWithClaims(issuer = AzureAdConfig.ISSUER, claimMap = ["${AzureAdConfig.CLAIM_NAV_IDENT}=*"])
class FormPublicationsController(
	val formPublicationsService: FormPublicationsService,
	private val securityContextHolder: SecurityContextHolder,
) : FormPublicationsApi {

	override fun publishForm(formPath: String, formsapiEntityRevision: Int): ResponseEntity<FormDto> {
		securityContextHolder.requireValidUser()
		val userId = securityContextHolder.getUserName()
		val form = formPublicationsService.publishForm(formPath, formsapiEntityRevision, userId)
		return ResponseEntity.status(HttpStatus.CREATED).body(form)
	}

	@Unprotected
	override fun getPublishedForm(formPath: String): ResponseEntity<FormDto> {
		val form = formPublicationsService.getPublishedForm(formPath)
		return ResponseEntity.ok(form)
	}

	@Unprotected
	override fun getPublishedForms(): ResponseEntity<List<FormDto>> {
		val forms = formPublicationsService.getPublishedForms()
		return ResponseEntity.ok(forms)
	}

	@Unprotected
	override fun getPublishedFormTranslations(
		formPath: String,
		languageCodes: String?
	): ResponseEntity<PublishedTranslationsDto> {
		val langs: List<LanguageCode>? = languageCodes?.split(",")?.map { LanguageCode.validate(it.trim()) }
		val formTranslations = formPublicationsService.getPublishedFormTranslations(formPath, langs)
		return ResponseEntity.ok(formTranslations)
	}


}
