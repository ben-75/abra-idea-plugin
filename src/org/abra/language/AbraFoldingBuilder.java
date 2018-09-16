package org.abra.language;

import com.intellij.codeInsight.folding.CodeFoldingSettings;
import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilder;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.abra.language.psi.AbraFile;
import org.abra.language.psi.AbraTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class AbraFoldingBuilder implements FoldingBuilder {
    @Override
    @NotNull
    public FoldingDescriptor[] buildFoldRegions(@NotNull ASTNode node, @NotNull Document document) {

        final ArrayList<FoldingDescriptor> regions = new ArrayList<>();
        process(node, document, regions);

        return regions.size() > 0
                ? regions.toArray(FoldingDescriptor.EMPTY)
                : FoldingDescriptor.EMPTY;
    }

    @Override
    public String getPlaceholderText(@NotNull ASTNode node) {
        final IElementType type = node.getElementType();
        if (type == AbraTypes.OPEN_BRACE) {
            return "{ ... }";
        } else if (isCommentLike(type)) {
            return "// ...";
        } else if (type == AbraTypes.OPEN_BRACKET) {
            return "[ ... ]";
        } else {
            return "...";
        }
    }

    private static boolean isCommentLike(IElementType type) {
        return AbraTypes.COMMENT == type;
    }

    @Override
    public boolean isCollapsedByDefault(@NotNull ASTNode node) {
        return isCommentLike(node.getElementType()) && CodeFoldingSettings.getInstance().COLLAPSE_DOC_COMMENTS;
    }

    private static void process(@Nullable ASTNode node, Document document, ArrayList<FoldingDescriptor> regions) {
        if (node == null) {
            return;
        }

        final ASTNode[] braces = node.getChildren(TokenSet.create(AbraTypes.OPEN_BRACE,AbraTypes.CLOSE_BRACE));
        if (braces.length == 2) {
            final ASTNode lbrace = braces[0];
            final ASTNode rbrace = braces[1];
            if (shouldFold(lbrace, rbrace, document)) {
                final TextRange range = new TextRange(lbrace.getStartOffset(), rbrace.getTextRange().getEndOffset());
                regions.add(new FoldingDescriptor(lbrace, range));
            }
        }

        final ASTNode[] brackets = node.getChildren(TokenSet.create(AbraTypes.OPEN_BRACKET,AbraTypes.CLOSE_BRACKET));
        if (brackets.length == 2) {
            final ASTNode lbrace = brackets[0];
            final ASTNode rbrace = brackets[1];
            if (shouldFoldBracket(lbrace, rbrace, document)) {
                final TextRange range = new TextRange(lbrace.getStartOffset(), rbrace.getTextRange().getEndOffset());
                regions.add(new FoldingDescriptor(lbrace, range));
            }
        }


        node = node.getFirstChildNode();
        while (node != null) {

            node = checkNodeAndSiblings(node, TokenSet.create(AbraTypes.COMMENT), regions, document);

            process(node, document, regions);

            if (node != null) {
                node = node.getTreeNext();
            }
        }
    }

    @Nullable
    private static ASTNode checkNodeAndSiblings(@Nullable ASTNode node, TokenSet tokens, ArrayList<FoldingDescriptor> regions, Document document) {
        if (node != null && tokens.contains(node.getElementType())) {
            final ASTNode start = node;
            ASTNode end = start;

            node = node.getTreeNext();
            if (node != null) {
                do {
                    end = node;
                    node = node.getTreeNext();
                } while (node != null && (tokens.contains(node.getElementType()) || node.getPsi() instanceof PsiWhiteSpace));
            }
            if (end != start) {
                while (end.getPsi() instanceof PsiWhiteSpace) {
                    end = end.getTreePrev();
                }
                if (isOnDifferentLine(start, end, document)) {
                    regions.add(new FoldingDescriptor(start, new TextRange(start.getStartOffset(), end.getTextRange().getEndOffset())));
                }
            }
        }
        return node;
    }

    private static boolean shouldFold(ASTNode first, ASTNode second, Document document) {
        if (first.getElementType() != AbraTypes.OPEN_BRACE) {
            return false;
        } else if (second.getElementType() != AbraTypes.CLOSE_BRACE) {
            return false;
        } else {
            return isOnDifferentLine(first, second, document);
        }
    }
    private static boolean shouldFoldBracket(ASTNode first, ASTNode second, Document document) {
        if (first.getElementType() != AbraTypes.OPEN_BRACKET) {
            return false;
        } else if (second.getElementType() != AbraTypes.CLOSE_BRACKET) {
            return false;
        } else {
            return isOnDifferentLine(first, second, document);
        }
    }
    private static boolean isOnDifferentLine(ASTNode first, ASTNode second, Document document) {
        return document.getLineNumber(first.getStartOffset()) != document.getLineNumber(second.getStartOffset());
    }
}
