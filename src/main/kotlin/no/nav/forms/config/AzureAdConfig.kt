package no.nav.forms.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("forms-api.azure")
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
