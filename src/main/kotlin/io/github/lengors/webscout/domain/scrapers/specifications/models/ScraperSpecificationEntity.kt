package io.github.lengors.webscout.domain.scrapers.specifications.models

import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecification
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.PersistenceCreator
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@ConsistentCopyVisibility
@Table(name = "scraper_specifications")
data class ScraperSpecificationEntity
    @PersistenceCreator
    private constructor(
        @Id
        @Column("id")
        val id: Long?,
        @Column("name")
        val name: String,
        @Column("data")
        val data: ScraperSpecification,
    ) {
        init {
            if (name != data.name) {
                throw IllegalArgumentException("Specification name (${data.name}) and entity name ($name) differ")
            }
        }

        constructor(data: ScraperSpecification) : this(null, data.name, data)

        fun clone(data: ScraperSpecification): ScraperSpecificationEntity = copy(data = data)
    }
