package no.nav.forms.forms.repository

import no.nav.forms.forms.repository.entity.FormRevisionComponentsEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FormRevisionComponentsRepository : JpaRepository<FormRevisionComponentsEntity, Long>
