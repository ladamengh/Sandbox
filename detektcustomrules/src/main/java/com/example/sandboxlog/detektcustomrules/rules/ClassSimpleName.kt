package com.example.sandboxlog.detektcustomrules.rules

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.KtNodeTypes
import org.jetbrains.kotlin.psi.KtProperty

class ClassSimpleName(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        id = "ClassSimpleName",
        description = "Getting class name by using .simpleName is not advisable",
        severity = Severity.Minor,
        debt = Debt.FIVE_MINS
    )

    override fun visitProperty(property: KtProperty) {
        super.visitProperty(property)

        property.children.forEach {
            if (it.node.elementType == KtNodeTypes.DOT_QUALIFIED_EXPRESSION &&
                it.node.text.contains(".simpleName")
            ) {
                report(
                    CodeSmell(
                        issue = issue,
                        entity = Entity.from(property.valOrVarKeyword),
                        message = "Replace .simpleName call by string literal"
                    )
                )
            }
        }
    }
}