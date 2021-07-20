package com.example.customktlintrules.detektmodule

import com.pinterest.ktlint.core.RuleSet
import com.pinterest.ktlint.core.RuleSetProvider

class CustomDetektRuleSetProvider : RuleSetProvider {

    override fun get() = RuleSet(
        "custom-rule-set",
        TrailingCommaRule(),
    )
}
