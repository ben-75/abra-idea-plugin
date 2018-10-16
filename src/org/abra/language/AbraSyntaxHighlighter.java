package org.abra.language;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import org.abra.language.psi.AbraTypes;
import org.jetbrains.annotations.NotNull;

import static com.intellij.openapi.editor.DefaultLanguageHighlighterColors.KEYWORD;
import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

public class AbraSyntaxHighlighter extends SyntaxHighlighterBase  {

    public static final TextAttributesKey COMMENT =
            createTextAttributesKey("SIMPLE_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);
    public static final TextAttributesKey BAD_CHARACTER =
            createTextAttributesKey("SIMPLE_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER);


    public static final TextAttributesKey ABRA_TYPE_DECLARATION =
            createTextAttributesKey("ABRA_TYPE_DECLARATION", DefaultLanguageHighlighterColors.TEMPLATE_LANGUAGE_COLOR);
    public static final TextAttributesKey ABRA_TYPE_REFERENCE =
            createTextAttributesKey("ABRA_TYPE_REFERENCE", DefaultLanguageHighlighterColors.TEMPLATE_LANGUAGE_COLOR);
    public static final TextAttributesKey ABRA_STATE_VAR_REFERENCE =
            createTextAttributesKey("ABRA_STATE_VAR_REFERENCE", DefaultLanguageHighlighterColors.TEMPLATE_LANGUAGE_COLOR);
    public static final TextAttributesKey ABRA_FIELD_DECLARATION =
            createTextAttributesKey("ABRA_FIELD_DECLARATION", DefaultLanguageHighlighterColors.GLOBAL_VARIABLE);
    public static final TextAttributesKey ABRA_FCT_DECLARATION =
            createTextAttributesKey("ABRA_FCT_DECLARATION", DefaultLanguageHighlighterColors.STRING);
    public static final TextAttributesKey ABRA_LUT_DECLARATION =
            createTextAttributesKey("ABRA_LUT_DECLARATION", DefaultLanguageHighlighterColors.INSTANCE_FIELD);
    public static final TextAttributesKey ABRA_TEMPLATE_DECLARATION =
            createTextAttributesKey("ABRA_TEMPLATE_DECLARATION", DefaultLanguageHighlighterColors.FUNCTION_CALL);
    public static final TextAttributesKey ABRA_LOCAL_VAR =
            createTextAttributesKey("ABRA_LOCAL_VAR", DefaultLanguageHighlighterColors.LOCAL_VARIABLE);
    public static final TextAttributesKey ABRA_TRIT =
            createTextAttributesKey("ABRA_TRIT", DefaultLanguageHighlighterColors.IDENTIFIER);
    public static final TextAttributesKey ABRA_TEST_ASSERTION_PREFIX =
            createTextAttributesKey("ABRA_TEST_ASSERTION_PREFIX", DefaultLanguageHighlighterColors.LINE_COMMENT);
    public static final TextAttributesKey ABRA_EXPR_ASSERTION_PREFIX =
            createTextAttributesKey("ABRA_EXPR_ASSERTION_PREFIX", DefaultLanguageHighlighterColors.LINE_COMMENT);
    public static final TextAttributesKey ABRA_TEST_ASSERTION =
            createTextAttributesKey("ABRA_TEST_ASSERTION", DefaultLanguageHighlighterColors.LINE_COMMENT);
    public static final TextAttributesKey ABRA_EXPR_ASSERTION =
            createTextAttributesKey("ABRA_EXPR_ASSERTION", DefaultLanguageHighlighterColors.LINE_COMMENT);
    private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];



    private static final TextAttributesKey[] BAD_CHAR_KEYS = new TextAttributesKey[]{BAD_CHARACTER};
    private static final TextAttributesKey[] COMMENT_KEYS = new TextAttributesKey[]{COMMENT};
    private static final TextAttributesKey[] TEST_COMMENT_KEYS = new TextAttributesKey[]{ABRA_TEST_ASSERTION_PREFIX};
    private static final TextAttributesKey[] EXPR_COMMENT_KEYS = new TextAttributesKey[]{ABRA_EXPR_ASSERTION_PREFIX};
    private static final TextAttributesKey[] TEST_ASSERT_KEYS = new TextAttributesKey[]{ABRA_TEST_ASSERTION};
    private static final TextAttributesKey[] EXPR_ASSERT_KEYS = new TextAttributesKey[]{ABRA_EXPR_ASSERTION};
    private static final TextAttributesKey[] KEYWORD_KEYS = new TextAttributesKey[]{KEYWORD};
    private static final TextAttributesKey[] TRIT_KEYS = new TextAttributesKey[]{ABRA_TRIT};

    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new AbraLexerAdapter();
    }

    @NotNull
    @Override
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
        if (tokenType.equals(AbraTypes.TEMPLATE_KEYWORD)) {
            return KEYWORD_KEYS;
        }else if (tokenType.equals(AbraTypes.FUNC_KEYWORD)) {
            return KEYWORD_KEYS;
        }else if (tokenType.equals(AbraTypes.LUT_KEYWORD)) {
            return KEYWORD_KEYS;
        }else if (tokenType.equals(AbraTypes.RETURN_KEYWORD)) {
            return KEYWORD_KEYS;
        }else if (tokenType.equals(AbraTypes.IMPORT_KEYWORD)) {
            return KEYWORD_KEYS;
        }else if (tokenType.equals(AbraTypes.TYPE_KEYWORD)) {
            return KEYWORD_KEYS;
        }else if (tokenType.equals(AbraTypes.USE_KEYWORD)) {
            return KEYWORD_KEYS;
        }else if (tokenType.equals(AbraTypes.STATE_KEYWORD)) {
            return KEYWORD_KEYS;
        }else if (tokenType.equals(AbraTypes.JOIN_KEYWORD)) {
            return KEYWORD_KEYS;
        }else if (tokenType.equals(AbraTypes.LIMIT_KEYWORD)) {
            return KEYWORD_KEYS;
        }else if (tokenType.equals(AbraTypes.AFFECT_KEYWORD)) {
            return KEYWORD_KEYS;
        }else if (tokenType.equals(AbraTypes.DELAY_KEYWORD)) {
            return KEYWORD_KEYS;
        }else if (tokenType.equals(AbraTypes.COMMENT)) {
            return COMMENT_KEYS;
        }else if (tokenType.equals(AbraTypes.TEST_COMMENT)) {
            return TEST_COMMENT_KEYS;
        }else if (tokenType.equals(AbraTypes.EXPR_COMMENT)) {
            return EXPR_COMMENT_KEYS;
        }else if (tokenType.equals(AbraTypes.TEST_ASSERTION)) {
            return TEST_ASSERT_KEYS;
        }else if (tokenType.equals(AbraTypes.EXPR_ASSERTION)) {
            return EXPR_ASSERT_KEYS;
        }else if (tokenType.equals(TokenType.BAD_CHARACTER)) {
            return BAD_CHAR_KEYS;
        } else {
            return EMPTY_KEYS;
        }
    }

}