package no.nav.forms.testutils

import com.nimbusds.jose.JOSEObjectType
import no.nav.forms.config.AzureAdConfig
import no.nav.security.mock.oauth2.MockOAuth2Server
import no.nav.security.mock.oauth2.token.DefaultOAuth2TokenCallback

fun MockOAuth2Server.createTokenFor(navIdent: String = "A123456", userName: String = "Testesen, Test"): String {
	return this.issueToken(
		issuerId = AzureAdConfig.ISSUER,
		clientId = "application",
		tokenCallback = DefaultOAuth2TokenCallback(
			issuerId = AzureAdConfig.ISSUER,
			subject = navIdent,
			typeHeader = JOSEObjectType.JWT.type,
			audience = listOf("aud-localhost"),
			claims = mapOf(
				AzureAdConfig.CLAIM_NAME to userName,
				AzureAdConfig.CLAIM_NAV_IDENT to navIdent,
				AzureAdConfig.CLAIM_GROUPS to listOf("mock-user-group-id", "mock-admin-group-id"),
			),
			expiry = (2 * 3600).toLong()
		)
	).serialize()
}
