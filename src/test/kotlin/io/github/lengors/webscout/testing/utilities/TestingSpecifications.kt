package io.github.lengors.webscout.testing.utilities

import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecification
import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecificationDataPayload
import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecificationDefaults
import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecificationFlatAction
import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecificationGates
import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecificationHandler
import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecificationJexlExpression
import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecificationJsonPayload
import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecificationPayloadEntry
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
import io.github.lengors.webscout.domain.scrapers.models.ScraperDefinitionPayloadType

object TestingSpecifications {
    fun buildTestSpecification(
        name: String = "test",
        host: String = "localhost",
        locale: String = "inputs.locale",
        requestParser: ScraperSpecificationRequestParser = ScraperSpecificationRequestParser.JSON,
        brokenGateLogic: Boolean = false,
        payloadType: ScraperDefinitionPayloadType? = null,
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
                    ScraperSpecificationRequirement("test", ScraperSpecificationRequirementType.EMAIL, null),
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
                            if (payloadType != null) ScraperSpecificationRequestMethod.POST else ScraperSpecificationRequestMethod.GET,
                            null,
                            payloadType?.let {
                                val payload = listOf<ScraperSpecificationPayloadEntry>()
                                when (it) {
                                    ScraperDefinitionPayloadType.DATA -> ScraperSpecificationDataPayload(payload)
                                    ScraperDefinitionPayloadType.JSON -> ScraperSpecificationJsonPayload(payload)
                                }
                            },
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
                        listOf(
                            ScraperSpecificationJexlExpression(
                                when (requestParser) {
                                    ScraperSpecificationRequestParser.HTML -> "toolkit.selectAll('//tr')"
                                    ScraperSpecificationRequestParser.JSON -> "toolkit.selectAll('/rows')"
                                    ScraperSpecificationRequestParser.AUTO, ScraperSpecificationRequestParser.TEXT -> ""
                                },
                            ),
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
                                    ScraperSpecificationRequestParser.HTML -> "toolkit.select('.//td[contains(@class, \\'description\\')]')"
                                    ScraperSpecificationRequestParser.JSON -> "toolkit.select('/description')"
                                    ScraperSpecificationRequestParser.AUTO, ScraperSpecificationRequestParser.TEXT -> ""
                                },
                            ),
                            ScraperSpecificationReturnBrand(
                                ScraperSpecificationJexlExpression(
                                    when (requestParser) {
                                        ScraperSpecificationRequestParser.HTML ->
                                            "toolkit.select('.//td[contains(@class, \\'brand\\')]')"

                                        ScraperSpecificationRequestParser.JSON -> "toolkit.select('/brand')"
                                        ScraperSpecificationRequestParser.AUTO, ScraperSpecificationRequestParser.TEXT -> ""
                                    },
                                ),
                                ScraperSpecificationJexlExpression(
                                    when (requestParser) {
                                        ScraperSpecificationRequestParser.HTML ->
                                            "toolkit.attr(toolkit.select('.//td[contains(@class, \\'brandImage\\')]/a'), 'href')"

                                        ScraperSpecificationRequestParser.JSON -> "toolkit.select('/brandImage')"
                                        ScraperSpecificationRequestParser.AUTO, ScraperSpecificationRequestParser.TEXT -> ""
                                    },
                                ),
                            ),
                            ScraperSpecificationJexlExpression(
                                when (requestParser) {
                                    ScraperSpecificationRequestParser.HTML ->
                                        "toolkit.select('.//td[contains(@class, \\'price\\')]')"

                                    ScraperSpecificationRequestParser.JSON -> "toolkit.select('/price')"
                                    ScraperSpecificationRequestParser.AUTO, ScraperSpecificationRequestParser.TEXT -> ""
                                },
                            ),
                            ScraperSpecificationJexlExpression(
                                when (requestParser) {
                                    ScraperSpecificationRequestParser.HTML ->
                                        "toolkit.attr(toolkit.select('.//td[contains(@class, \\'image\\')]/a'), 'href')"

                                    ScraperSpecificationRequestParser.JSON -> "toolkit.select('/image')"
                                    ScraperSpecificationRequestParser.AUTO, ScraperSpecificationRequestParser.TEXT -> ""
                                },
                            ),
                            listOf(
                                ScraperSpecificationReturnFlatStock(
                                    listOf(
                                        ScraperSpecificationJexlExpression(
                                            when (requestParser) {
                                                ScraperSpecificationRequestParser.HTML ->
                                                    "toolkit.selectAll('.//td[contains(@class, \\'stocks\\')]/div')"

                                                ScraperSpecificationRequestParser.JSON -> "toolkit.selectAll('/stocks')"
                                                ScraperSpecificationRequestParser.AUTO, ScraperSpecificationRequestParser.TEXT -> ""
                                            },
                                        ),
                                    ),
                                    listOf(
                                        ScraperSpecificationReturnExtractStock(
                                            ScraperSpecificationJexlExpression(
                                                when (requestParser) {
                                                    ScraperSpecificationRequestParser.HTML ->
                                                        "toolkit.select('.//p[contains(@class, \\'availability\\')]')"

                                                    ScraperSpecificationRequestParser.JSON -> "toolkit.select('/availability')"
                                                    ScraperSpecificationRequestParser.AUTO, ScraperSpecificationRequestParser.TEXT -> ""
                                                },
                                            ),
                                            ScraperSpecificationJexlExpression(
                                                when (requestParser) {
                                                    ScraperSpecificationRequestParser.HTML ->
                                                        "toolkit.select('.//p[contains(@class, \\'storage\\')]')"

                                                    ScraperSpecificationRequestParser.JSON -> "toolkit.select('/storage')"
                                                    ScraperSpecificationRequestParser.AUTO, ScraperSpecificationRequestParser.TEXT -> ""
                                                },
                                            ),
                                            ScraperSpecificationJexlExpression(
                                                when (requestParser) {
                                                    ScraperSpecificationRequestParser.HTML ->
                                                        "toolkit.select('.//p[contains(@class, \\'delivery\\')]')"

                                                    ScraperSpecificationRequestParser.JSON -> "toolkit.select('/delivery')"
                                                    ScraperSpecificationRequestParser.AUTO, ScraperSpecificationRequestParser.TEXT -> ""
                                                },
                                            ),
                                        ),
                                    ),
                                ),
                            ),
                            ScraperSpecificationJexlExpression(
                                when (requestParser) {
                                    ScraperSpecificationRequestParser.HTML -> "toolkit.select('.//td[contains(@class, \\'grip\\')]')"
                                    ScraperSpecificationRequestParser.JSON -> "toolkit.select('/grip')"
                                    ScraperSpecificationRequestParser.AUTO, ScraperSpecificationRequestParser.TEXT -> ""
                                },
                            ),
                            ScraperSpecificationJexlExpression(
                                when (requestParser) {
                                    ScraperSpecificationRequestParser.HTML -> "toolkit.select('.//td[contains(@class, \\'noise\\')]')"
                                    ScraperSpecificationRequestParser.JSON -> "toolkit.select('/noise')"
                                    ScraperSpecificationRequestParser.AUTO, ScraperSpecificationRequestParser.TEXT -> ""
                                },
                            ),
                            ScraperSpecificationJexlExpression(
                                when (requestParser) {
                                    ScraperSpecificationRequestParser.HTML -> "toolkit.select('.//td[contains(@class, \\'decibels\\')]')"
                                    ScraperSpecificationRequestParser.JSON -> "toolkit.select('/decibels')"
                                    ScraperSpecificationRequestParser.AUTO, ScraperSpecificationRequestParser.TEXT -> ""
                                },
                            ),
                            ScraperSpecificationJexlExpression(
                                when (requestParser) {
                                    ScraperSpecificationRequestParser.HTML -> "toolkit.select('.//td[contains(@class, \\'consumption\\')]')"
                                    ScraperSpecificationRequestParser.JSON -> "toolkit.select('/consumption')"
                                    ScraperSpecificationRequestParser.AUTO, ScraperSpecificationRequestParser.TEXT -> ""
                                },
                            ),
                            listOf(
                                ScraperSpecificationReturnDescriptiveDetail(
                                    ScraperSpecificationJexlExpression("'descriptive'"),
                                    ScraperSpecificationJexlExpression(
                                        when (requestParser) {
                                            ScraperSpecificationRequestParser.HTML ->
                                                "toolkit.select('.//td[contains(@class, \\'extra\\')]')"

                                            ScraperSpecificationRequestParser.JSON -> "toolkit.select('/extra')"
                                            ScraperSpecificationRequestParser.AUTO, ScraperSpecificationRequestParser.TEXT -> ""
                                        },
                                    ),
                                    ScraperSpecificationJexlExpression(
                                        when (requestParser) {
                                            ScraperSpecificationRequestParser.HTML ->
                                                "toolkit.attr(toolkit.selectAll('.//td[contains(@class, \\'extraImages\\')]/a')[0], 'href')"

                                            ScraperSpecificationRequestParser.JSON -> "toolkit.selectAll('/extraImages')[0]"
                                            ScraperSpecificationRequestParser.AUTO, ScraperSpecificationRequestParser.TEXT -> ""
                                        },
                                    ),
                                ),
                                ScraperSpecificationReturnDescriptionlessDetail(
                                    ScraperSpecificationJexlExpression("'descriptionless'"),
                                    ScraperSpecificationJexlExpression(
                                        when (requestParser) {
                                            ScraperSpecificationRequestParser.HTML ->
                                                "toolkit.attr(toolkit.selectAll('.//td[contains(@class, \\'extraImages\\')]/a')[1], 'href')"

                                            ScraperSpecificationRequestParser.JSON -> "toolkit.selectAll('/extraImages')[1]"
                                            ScraperSpecificationRequestParser.AUTO, ScraperSpecificationRequestParser.TEXT -> ""
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
