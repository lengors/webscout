package io.github.lengors.webscout.testing.utilities

import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecification
import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecificationDefaults
import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecificationFlatAction
import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecificationGates
import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecificationHandler
import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecificationJexlExpression
import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecificationRequest
import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecificationRequestAction
import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecificationRequestMethod
import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecificationRequestParser
import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecificationRequirement
import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecificationRequirementType
import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecificationReturn
import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecificationReturnAction
import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecificationReturnBrand
import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecificationReturnDescriptionlessDetail
import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecificationReturnDescriptiveDetail
import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecificationReturnExtractStock
import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecificationReturnFlatStock
import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecificationSettings
import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecificationUrl
import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecificationUrlParameter

object TestingSpecifications {
    fun buildTestSpecification(
        name: String = "test",
        host: String = "localhost",
        locale: String = "inputs.locale",
        requestParser: ScraperSpecificationRequestParser = ScraperSpecificationRequestParser.JSON,
        brokenGateLogic: Boolean = false,
    ): ScraperSpecification =
        ScraperSpecification(
            name,
            ScraperSpecificationSettings(
                ScraperSpecificationDefaults(
                    ScraperSpecificationUrl(
                        null,
                        ScraperSpecificationJexlExpression("'http'"),
                        ScraperSpecificationJexlExpression("'$host'"),
                        null,
                        emptyList(),
                    ),
                    null,
                    listOf(ScraperSpecificationJexlExpression("'search'")),
                ),
                ScraperSpecificationJexlExpression(locale),
                ScraperSpecificationJexlExpression("inputs.timezone"),
                emptyList(),
                listOf(
                    ScraperSpecificationRequirement("locale", ScraperSpecificationRequirementType.TEXT, "en-GB"),
                    ScraperSpecificationRequirement(
                        "timezone",
                        ScraperSpecificationRequirementType.TEXT,
                        "Europe/London",
                    ),
                ),
            ),
            listOf(
                ScraperSpecificationHandler(
                    "search",
                    null,
                    ScraperSpecificationGates(
                        if (brokenGateLogic) emptyList() else listOf(ScraperSpecificationJexlExpression("'table'")),
                        listOf(ScraperSpecificationJexlExpression("'search'")),
                        listOf(ScraperSpecificationJexlExpression("'search'")),
                    ),
                    ScraperSpecificationRequestAction(
                        ScraperSpecificationRequest(
                            ScraperSpecificationUrl(
                                null,
                                null,
                                null,
                                ScraperSpecificationJexlExpression("'test'"),
                                listOf(
                                    ScraperSpecificationUrlParameter(
                                        ScraperSpecificationJexlExpression("'term'"),
                                        ScraperSpecificationJexlExpression("searchTerm"),
                                    ),
                                ),
                            ),
                            ScraperSpecificationRequestMethod.GET,
                            null,
                            null,
                            requestParser,
                        ),
                        null,
                    ),
                ),
                ScraperSpecificationHandler(
                    "table",
                    ScraperSpecificationJexlExpression("uri.getPath() != null && uri.getPath().contains('test')"),
                    ScraperSpecificationGates(
                        listOf(ScraperSpecificationJexlExpression("'row'")),
                        listOf(ScraperSpecificationJexlExpression("'table'")),
                        listOf(ScraperSpecificationJexlExpression("'table'")),
                    ),
                    ScraperSpecificationFlatAction(
                        ScraperSpecificationJexlExpression(
                            when (requestParser) {
                                ScraperSpecificationRequestParser.HTML -> "selectAll('//tr')"
                                ScraperSpecificationRequestParser.JSON -> "selectAll('/rows')"
                                ScraperSpecificationRequestParser.TEXT -> ""
                            },
                        ),
                    ),
                ),
                ScraperSpecificationHandler(
                    "row",
                    null,
                    ScraperSpecificationGates(
                        null,
                        listOf(ScraperSpecificationJexlExpression("'row'")),
                        listOf(ScraperSpecificationJexlExpression("'row'")),
                    ),
                    ScraperSpecificationReturnAction(
                        ScraperSpecificationReturn(
                            ScraperSpecificationJexlExpression(
                                when (requestParser) {
                                    ScraperSpecificationRequestParser.HTML -> "select('.//td[contains(@class, \\'description\\')]')"
                                    ScraperSpecificationRequestParser.JSON -> "select('/description')"
                                    ScraperSpecificationRequestParser.TEXT -> ""
                                },
                            ),
                            ScraperSpecificationReturnBrand(
                                ScraperSpecificationJexlExpression(
                                    when (requestParser) {
                                        ScraperSpecificationRequestParser.HTML ->
                                            "select('.//td[contains(@class, \\'brand\\')]')"

                                        ScraperSpecificationRequestParser.JSON -> "select('/brand')"
                                        ScraperSpecificationRequestParser.TEXT -> ""
                                    },
                                ),
                                ScraperSpecificationJexlExpression(
                                    when (requestParser) {
                                        ScraperSpecificationRequestParser.HTML ->
                                            "select('.//td[contains(@class, \\'brandImage\\')]/a').attr('href')"

                                        ScraperSpecificationRequestParser.JSON -> "select('/brandImage')"
                                        ScraperSpecificationRequestParser.TEXT -> ""
                                    },
                                ),
                            ),
                            ScraperSpecificationJexlExpression(
                                when (requestParser) {
                                    ScraperSpecificationRequestParser.HTML ->
                                        "select('.//td[contains(@class, \\'price\\')]')"

                                    ScraperSpecificationRequestParser.JSON -> "select('/price')"
                                    ScraperSpecificationRequestParser.TEXT -> ""
                                },
                            ),
                            ScraperSpecificationJexlExpression(
                                when (requestParser) {
                                    ScraperSpecificationRequestParser.HTML ->
                                        "select('.//td[contains(@class, \\'image\\')]/a').attr('href')"

                                    ScraperSpecificationRequestParser.JSON -> "select('/image')"
                                    ScraperSpecificationRequestParser.TEXT -> ""
                                },
                            ),
                            listOf(
                                ScraperSpecificationReturnFlatStock(
                                    ScraperSpecificationJexlExpression(
                                        when (requestParser) {
                                            ScraperSpecificationRequestParser.HTML ->
                                                "selectAll('.//td[contains(@class, \\'stocks\\')]/div')"

                                            ScraperSpecificationRequestParser.JSON -> "selectAll('/stocks')"
                                            ScraperSpecificationRequestParser.TEXT -> ""
                                        },
                                    ),
                                    listOf(
                                        ScraperSpecificationReturnExtractStock(
                                            ScraperSpecificationJexlExpression(
                                                when (requestParser) {
                                                    ScraperSpecificationRequestParser.HTML ->
                                                        "select('.//p[contains(@class, \\'availability\\')]')"

                                                    ScraperSpecificationRequestParser.JSON -> "select('/availability')"
                                                    ScraperSpecificationRequestParser.TEXT -> ""
                                                },
                                            ),
                                            ScraperSpecificationJexlExpression(
                                                when (requestParser) {
                                                    ScraperSpecificationRequestParser.HTML ->
                                                        "select('.//p[contains(@class, \\'storage\\')]')"

                                                    ScraperSpecificationRequestParser.JSON -> "select('/storage')"
                                                    ScraperSpecificationRequestParser.TEXT -> ""
                                                },
                                            ),
                                            ScraperSpecificationJexlExpression(
                                                when (requestParser) {
                                                    ScraperSpecificationRequestParser.HTML ->
                                                        "select('.//p[contains(@class, \\'delivery\\')]')"

                                                    ScraperSpecificationRequestParser.JSON -> "select('/delivery')"
                                                    ScraperSpecificationRequestParser.TEXT -> ""
                                                },
                                            ),
                                        ),
                                    ),
                                ),
                            ),
                            ScraperSpecificationJexlExpression(
                                when (requestParser) {
                                    ScraperSpecificationRequestParser.HTML -> "select('.//td[contains(@class, \\'grip\\')]')"
                                    ScraperSpecificationRequestParser.JSON -> "select('/grip')"
                                    ScraperSpecificationRequestParser.TEXT -> ""
                                },
                            ),
                            ScraperSpecificationJexlExpression(
                                when (requestParser) {
                                    ScraperSpecificationRequestParser.HTML -> "select('.//td[contains(@class, \\'noise\\')]')"
                                    ScraperSpecificationRequestParser.JSON -> "select('/noise')"
                                    ScraperSpecificationRequestParser.TEXT -> ""
                                },
                            ),
                            ScraperSpecificationJexlExpression(
                                when (requestParser) {
                                    ScraperSpecificationRequestParser.HTML -> "select('.//td[contains(@class, \\'decibels\\')]')"
                                    ScraperSpecificationRequestParser.JSON -> "select('/decibels')"
                                    ScraperSpecificationRequestParser.TEXT -> ""
                                },
                            ),
                            ScraperSpecificationJexlExpression(
                                when (requestParser) {
                                    ScraperSpecificationRequestParser.HTML -> "select('.//td[contains(@class, \\'consumption\\')]')"
                                    ScraperSpecificationRequestParser.JSON -> "select('/consumption')"
                                    ScraperSpecificationRequestParser.TEXT -> ""
                                },
                            ),
                            listOf(
                                ScraperSpecificationReturnDescriptiveDetail(
                                    ScraperSpecificationJexlExpression("'descriptive'"),
                                    ScraperSpecificationJexlExpression(
                                        when (requestParser) {
                                            ScraperSpecificationRequestParser.HTML -> "select('.//td[contains(@class, \\'extra\\')]')"
                                            ScraperSpecificationRequestParser.JSON -> "select('/extra')"
                                            ScraperSpecificationRequestParser.TEXT -> ""
                                        },
                                    ),
                                    ScraperSpecificationJexlExpression(
                                        when (requestParser) {
                                            ScraperSpecificationRequestParser.HTML ->
                                                "selectAll('.//td[contains(@class, \\'extraImages\\')]/a').at(0).attr('href')"

                                            ScraperSpecificationRequestParser.JSON -> "selectAll('/extraImages').at(0)"
                                            ScraperSpecificationRequestParser.TEXT -> ""
                                        },
                                    ),
                                ),
                                ScraperSpecificationReturnDescriptionlessDetail(
                                    ScraperSpecificationJexlExpression("'descriptionless'"),
                                    ScraperSpecificationJexlExpression(
                                        when (requestParser) {
                                            ScraperSpecificationRequestParser.HTML ->
                                                "selectAll('.//td[contains(@class, \\'extraImages\\')]/a').at(1).attr('href')"

                                            ScraperSpecificationRequestParser.JSON -> "selectAll('/extraImages').at(1)"
                                            ScraperSpecificationRequestParser.TEXT -> ""
                                        },
                                    ),
                                ),
                            ),
                        ),
                    ),
                ),
            ),
        )
}
