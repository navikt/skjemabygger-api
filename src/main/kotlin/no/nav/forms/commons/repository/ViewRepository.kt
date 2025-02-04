package no.nav.forms.commons.repository

import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.Repository

@NoRepositoryBean
interface ViewRepository<T, ID> : Repository<T, ID> {

	fun findAll(): List<T>

}
