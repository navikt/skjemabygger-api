package no.nav.forms.security

import no.nav.forms.config.AzureAdConfig
import no.nav.security.token.support.core.context.TokenValidationContextHolder
import no.nav.security.token.support.core.exceptions.JwtTokenInvalidClaimException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

interface SecurityContextHolder {
	fun verifyWritePermission()
	fun getNavIdent(): String
	fun getUserName(): String
	fun isAdminUser(): Boolean
}

@Component
@Profile("preprod | prod | test")
class SecurityContextHolderImpl(
	private val ctxHolder: TokenValidationContextHolder,
	private val azureAdConfig: AzureAdConfig,
) : SecurityContextHolder {

	val logger: Logger = LoggerFactory.getLogger(javaClass)

	override fun verifyWritePermission() {
		val isMemberOfUserGroup = isMemberOfGroup(azureAdConfig.groups.userGroupId)
		if (!isMemberOfUserGroup) throw JwtTokenInvalidClaimException("Missing claim with write permission")
	}

	override fun getNavIdent(): String {
		val claims = ctxHolder.getTokenValidationContext().getClaims(AzureAdConfig.ISSUER)
		return claims.getStringClaim("NAVIdent") ?: throw Exception("No NAVIdent claim found")
	}

	override fun getUserName(): String {
		val claims = ctxHolder.getTokenValidationContext().getClaims(AzureAdConfig.ISSUER)
		return claims.getStringClaim("name") ?: throw Exception("No user name claim found")
	}

	override fun isAdminUser(): Boolean = isMemberOfGroup(azureAdConfig.groups.adminGroupId)

	private fun isMemberOfGroup(groupId: String): Boolean {
		return ctxHolder.getTokenValidationContext().getJwtToken(AzureAdConfig.ISSUER)?.jwtTokenClaims?.containsClaim(
			"groups",
			groupId
		) == true
	}

}

@Component
@Profile("local | docker")
class SecurityContextHolderMock : SecurityContextHolder {
	override fun verifyWritePermission() {}
	override fun getNavIdent(): String = "testuser"
	override fun getUserName(): String = "Testesen, Test"
	override fun isAdminUser(): Boolean = true
}
