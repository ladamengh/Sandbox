package com.example.sandboxlog.detektcustomrules.rules

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.KtNodeTypes
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.com.intellij.psi.JavaTokenType
import org.jetbrains.kotlin.com.intellij.psi.PsiWhiteSpace
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtIfExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtPrimaryConstructor
import org.jetbrains.kotlin.psi.KtSecondaryConstructor
import org.jetbrains.kotlin.psi.psiUtil.allChildren
import org.jetbrains.kotlin.psi.psiUtil.children
import org.jetbrains.kotlin.psi.stubs.elements.KtPlaceHolderStubElementType

class TrailingComma(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        id = "TrailingComma",
        description = "Missing trailing comma",
        severity = Severity.CodeSmell,
        debt = Debt.FIVE_MINS
    )

    override fun visitPrimaryConstructor(constructor: KtPrimaryConstructor) {
        super.visitPrimaryConstructor(constructor)

        constructor.allChildren.forEach {
            processNode(it.node)
        }
    }

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)

        function.allChildren.forEach {
            processNode(it.node)
        }
    }

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        expression.allChildren.forEach {
            processNode(it.node)
        }
    }

    private fun processNode(node: ASTNode) {
        if (node.isArgumentOrParameterList() && node.isMultiLine()) {
            val lastArgument = node.lastArgumentOrParameter()
            if (lastArgument != null && !lastArgument.treeNext.chars.contains(',')) {
                report(
                    CodeSmell(
                        issue = issue,
                        entity = Entity.from(node.psi),
                        message = "Missing trailing comma, please add one"
                    )
                )
                if (autoCorrect) {
                    node.addChild(
                        LeafPsiElement(JavaTokenType.COMMA, ","),
                        lastArgument.treeNext
                    )
                }
            }
        }
    }

    private fun ASTNode.isArgumentOrParameterList(): Boolean =
        elementType == KtNodeTypes.VALUE_ARGUMENT_LIST ||
                elementType == KtNodeTypes.VALUE_PARAMETER_LIST

    private fun ASTNode.isMultiLine(): Boolean =
        children().any { it is PsiWhiteSpace && it.textContains('\n') }

    private fun ASTNode.lastArgumentOrParameter() =
        children()
            .findLast {
                it.elementType == KtNodeTypes.VALUE_ARGUMENT ||
                        it.elementType == KtNodeTypes.VALUE_PARAMETER
            }
}
