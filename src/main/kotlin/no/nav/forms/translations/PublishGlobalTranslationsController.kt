package no.nav.forms.translations

import no.nav.forms.api.PublishGlobalTranslationsApi
import no.nav.forms.config.AzureAdConfig
import no.nav.forms.model.PublishedGlobalTranslationDto
import no.nav.forms.security.SecurityContextHolder
import no.nav.forms.translations.global.PublishGlobalTranslationsService
import no.nav.forms.utils.LanguageCode
import no.nav.security.token.support.core.api.ProtectedWithClaims
import no.nav.security.token.support.core.api.Unprotected
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
@ProtectedWithClaims(issuer = AzureAdConfig.ISSUER, claimMap = ["${AzureAdConfig.CLAIM_NAV_IDENT}=*"])
class PublishGlobalTranslationsController(
	private val publishGlobalTranslationsService: PublishGlobalTranslationsService,
	private val securityContextHolder: SecurityContextHolder,
) : PublishGlobalTranslationsApi {

	@Unprotected
	override fun getPublishedGlobalTranslations(languageCode: String): ResponseEntity<List<PublishedGlobalTranslationDto>> {
		val lang: LanguageCode = LanguageCode.forValue(languageCode)
			?: throw IllegalArgumentException("Language code '$languageCode' is not supported")
		return ResponseEntity.ok(publishGlobalTranslationsService.getPublishedGlobalTranslations(lang))
	}

	override fun publishGlobalTranslations(): ResponseEntity<Unit> {
		securityContextHolder.requireAdminUser()
		val userId = securityContextHolder.getUserName()
		publishGlobalTranslationsService.publish(userId)
		return ResponseEntity.status(HttpStatus.CREATED).build()
	}

}
