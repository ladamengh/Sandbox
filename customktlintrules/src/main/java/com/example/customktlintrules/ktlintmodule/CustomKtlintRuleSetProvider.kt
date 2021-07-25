package com.example.customktlintrules.ktlintmodule

import com.pinterest.ktlint.core.RuleSet
import com.pinterest.ktlint.core.RuleSetProvider

class CustomKtlintRuleSetProvider : RuleSetProvider {

    override fun get() = RuleSet(
        "custom-rule-set",
        TrailingCommaRule(),
    )
}
