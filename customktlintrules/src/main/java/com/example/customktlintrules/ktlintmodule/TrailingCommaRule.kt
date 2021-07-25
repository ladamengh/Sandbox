package com.example.customktlintrules.ktlintmodule

import com.pinterest.ktlint.core.Rule
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.com.intellij.psi.JavaTokenType.COMMA
import org.jetbrains.kotlin.com.intellij.psi.PsiWhiteSpace
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.psi.psiUtil.children
import org.jetbrains.kotlin.psi.stubs.elements.KtStubElementTypes.VALUE_ARGUMENT
import org.jetbrains.kotlin.psi.stubs.elements.KtStubElementTypes.VALUE_ARGUMENT_LIST
import org.jetbrains.kotlin.psi.stubs.elements.KtStubElementTypes.VALUE_PARAMETER
import org.jetbrains.kotlin.psi.stubs.elements.KtStubElementTypes.VALUE_PARAMETER_LIST

class TrailingCommaRule : Rule("mandate-trailing-comma") {

    override fun visit(
        node: ASTNode,
        autoCorrect: Boolean,
        emit: (offset: Int, errorMessage: String, canBeAutoCorrected: Boolean) -> Unit,
    ) {
        if (node.isArgumentOrParameterList() && node.isMultiLine()) {
            val lastArgument = node.lastArgumentOrParameter()
            if (lastArgument != null && lastArgument.treeNext.elementType != COMMA) {
                emit(
                    lastArgument.startOffset + lastArgument.textLength,
                    "Trailing comma is missing",
                    true,
                )
                if (autoCorrect) {
                    node.addChild(LeafPsiElement(COMMA, ","), lastArgument.treeNext)
                }
            }
        }
    }

    private fun ASTNode.isArgumentOrParameterList(): Boolean =
        elementType == VALUE_ARGUMENT_LIST || elementType == VALUE_PARAMETER_LIST

    private fun ASTNode.isMultiLine(): Boolean = children().any { it is PsiWhiteSpace && it.textContains('\n') }
    private fun ASTNode.lastArgumentOrParameter() =
        children().findLast { it.elementType == VALUE_ARGUMENT || it.elementType == VALUE_PARAMETER }
}
