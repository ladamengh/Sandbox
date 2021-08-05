package com.example.sandboxlog.detektcustomrules.provider

import com.example.sandboxlog.detektcustomrules.rules.TrailingComma
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider

class CustomRuleSetProvider : RuleSetProvider {

    override val ruleSetId: String = "detekt-custom-rules"

    override fun instance(
        config: Config,
    ) = RuleSet(
        ruleSetId,
        listOf(
            TrailingComma(config),
        ),
    )
}
