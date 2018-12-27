package org.abra.language;

import com.intellij.lang.*;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import org.abra.language.parser.QuplaParser;
import org.abra.language.psi.QuplaFile;
import org.abra.language.psi.QuplaTypes;
import org.jetbrains.annotations.NotNull;

public class QuplaParserDefinition implements ParserDefinition {
    public static final TokenSet WHITE_SPACES = TokenSet.create(TokenType.WHITE_SPACE);
//    public static final TokenSet COMMENTS = TokenSet.create(QuplaTypes.COMMENT,QuplaTypes.TEST_COMMENT,QuplaTypes.TEST_ASSERTION,QuplaTypes.EXPR_COMMENT,QuplaTypes.EXPR_ASSERTION);
    public static final TokenSet COMMENTS = TokenSet.create(QuplaTypes.COMMENT);

    public static final IFileElementType FILE = new IFileElementType(QuplaLanguage.INSTANCE);

    @NotNull
    @Override
    public Lexer createLexer(Project project) {
        return new QuplaLexerAdapter();
    }

    @NotNull
    public TokenSet getWhitespaceTokens() {
        return WHITE_SPACES;
    }

    @NotNull
    public TokenSet getCommentTokens() {
        return COMMENTS;
    }

    @NotNull
    public TokenSet getStringLiteralElements() {
        return TokenSet.EMPTY;
    }

    @NotNull
    public PsiParser createParser(final Project project) {
        return new QuplaParser();
    }

    @Override
    public IFileElementType getFileNodeType() {
        return FILE;
    }

    public PsiFile createFile(FileViewProvider viewProvider) {
        return new QuplaFile(viewProvider);
    }

    @SuppressWarnings("deprecated")
    public SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode left, ASTNode right) {
        return SpaceRequirements.MAY;
    }

    @NotNull
    public PsiElement createElement(ASTNode node) {
        return QuplaTypes.Factory.createElement(node);
    }
}
