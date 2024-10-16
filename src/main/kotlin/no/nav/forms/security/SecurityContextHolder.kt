package no.nav.forms.security

import no.nav.forms.config.AzureAdConfig
import no.nav.security.token.support.core.context.TokenValidationContextHolder
import no.nav.security.token.support.core.exceptions.JwtTokenInvalidClaimException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

interface SecurityContextHolder {
	fun requireValidUser()
	fun requireAdminUser()
	fun getNavIdent(): String
	fun getUserName(): String
	fun isValidUser(): Boolean
	fun isAdminUser(): Boolean
}

@Component
@Profile("preprod | prod | test")
class SecurityContextHolderImpl(
	private val ctxHolder: TokenValidationContextHolder,
	private val azureAdConfig: AzureAdConfig,
) : SecurityContextHolder {

	val logger: Logger = LoggerFactory.getLogger(javaClass)

	override fun requireValidUser() = verifyClaim(isValidUser()) { "Invalid user" }

	override fun requireAdminUser() = verifyClaim(isAdminUser()) { "User is not admin" }

	override fun getNavIdent(): String {
		val claims = ctxHolder.getTokenValidationContext().getClaims(AzureAdConfig.ISSUER)
		return claims.getStringClaim(AzureAdConfig.CLAIM_NAV_IDENT)
			?: throw Exception("No ${AzureAdConfig.CLAIM_NAV_IDENT} claim found")
	}

	override fun getUserName(): String {
		val claims = ctxHolder.getTokenValidationContext().getClaims(AzureAdConfig.ISSUER)
		return claims.getStringClaim(AzureAdConfig.CLAIM_NAME)
			?: throw Exception("No ${AzureAdConfig.CLAIM_NAME} claim found")
	}

	override fun isValidUser(): Boolean = isMemberOfGroup(azureAdConfig.groups.userGroupId)

	override fun isAdminUser(): Boolean = isMemberOfGroup(azureAdConfig.groups.adminGroupId)

	private fun isMemberOfGroup(groupId: String): Boolean {
		return ctxHolder.getTokenValidationContext().getJwtToken(AzureAdConfig.ISSUER)?.jwtTokenClaims?.containsClaim(
			AzureAdConfig.CLAIM_GROUPS,
			groupId
		) == true
	}

	private fun verifyClaim(bool: Boolean, getMessage: () -> String) {
		if (!bool) throw JwtTokenInvalidClaimException(getMessage())
	}

}

@Component
@Profile("local | docker")
class SecurityContextHolderMock : SecurityContextHolder {
	override fun requireValidUser() {}
	override fun requireAdminUser() {}
	override fun getNavIdent(): String = "testuser"
	override fun getUserName(): String = "Testesen, Test"
	override fun isValidUser(): Boolean = true
	override fun isAdminUser(): Boolean = true
}
