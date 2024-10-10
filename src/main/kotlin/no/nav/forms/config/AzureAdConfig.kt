package no.nav.forms.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Profile

@ConfigurationProperties("forms-api.azuread")
@Profile("test | preprod | prod")
class AzureAdConfig {

	lateinit var groups: AdGroupIds

	companion object {
		const val ISSUER = "azuread"
	}

}

class AdGroupIds {
	lateinit var userGroupId: String
	lateinit var adminGroupId: String
}
