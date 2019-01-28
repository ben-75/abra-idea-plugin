package org.qupla.ide.highlighter;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import org.qupla.language.QuplaLexerAdapter;
import org.qupla.language.psi.QuplaTypes;
import org.jetbrains.annotations.NotNull;

import static com.intellij.openapi.editor.DefaultLanguageHighlighterColors.KEYWORD;
import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

public class QuplaSyntaxHighlighter extends SyntaxHighlighterBase  {

    public static final TextAttributesKey COMMENT =
            createTextAttributesKey("SIMPLE_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);
    public static final TextAttributesKey BAD_CHARACTER =
            createTextAttributesKey("SIMPLE_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER);


    public static final TextAttributesKey QUPLA_TYPE_DECLARATION =
            createTextAttributesKey("QUPLA_TYPE_DECLARATION", DefaultLanguageHighlighterColors.TEMPLATE_LANGUAGE_COLOR);
    public static final TextAttributesKey QUPLA_TYPE_REFERENCE =
            createTextAttributesKey("QUPLA_TYPE_REFERENCE", DefaultLanguageHighlighterColors.TEMPLATE_LANGUAGE_COLOR);
    public static final TextAttributesKey QUPLA_STATE_VAR_REFERENCE =
            createTextAttributesKey("QUPLA_STATE_VAR_REFERENCE", DefaultLanguageHighlighterColors.TEMPLATE_LANGUAGE_COLOR);
    public static final TextAttributesKey QUPLA_FIELD_DECLARATION =
            createTextAttributesKey("QUPLA_FIELD_DECLARATION", DefaultLanguageHighlighterColors.GLOBAL_VARIABLE);
    public static final TextAttributesKey QUPLA_FCT_DECLARATION =
            createTextAttributesKey("QUPLA_FCT_DECLARATION", DefaultLanguageHighlighterColors.STRING);
    public static final TextAttributesKey QUPLA_LUT_DECLARATION =
            createTextAttributesKey("QUPLA_LUT_DECLARATION", DefaultLanguageHighlighterColors.INSTANCE_FIELD);
    public static final TextAttributesKey QUPLA_TEMPLATE_DECLARATION =
            createTextAttributesKey("QUPLA_TEMPLATE_DECLARATION", DefaultLanguageHighlighterColors.FUNCTION_CALL);
    public static final TextAttributesKey QUPLA_LOCAL_VAR =
            createTextAttributesKey("QUPLA_LOCAL_VAR", DefaultLanguageHighlighterColors.LOCAL_VARIABLE);
    public static final TextAttributesKey QUPLA_TRIT =
            createTextAttributesKey("QUPLA_TRIT", DefaultLanguageHighlighterColors.IDENTIFIER);
    public static final TextAttributesKey QUPLA_TEST_ASSERTION_PREFIX =
            createTextAttributesKey("QUPLA_TEST_ASSERTION_PREFIX", DefaultLanguageHighlighterColors.LINE_COMMENT);
    public static final TextAttributesKey QUPLA_EXPR_ASSERTION_PREFIX =
            createTextAttributesKey("QUPLA_EXPR_ASSERTION_PREFIX", DefaultLanguageHighlighterColors.LINE_COMMENT);
    public static final TextAttributesKey QUPLA_TEST_ASSERTION =
            createTextAttributesKey("QUPLA_TEST_ASSERTION", DefaultLanguageHighlighterColors.LINE_COMMENT);
    public static final TextAttributesKey QUPLA_EXPR_ASSERTION =
            createTextAttributesKey("QUPLA_EXPR_ASSERTION", DefaultLanguageHighlighterColors.LINE_COMMENT);
    public static final TextAttributesKey ENV_KEYWORD =
            createTextAttributesKey("ENV_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD);
    public static final TextAttributesKey ENV_NAME =
            createTextAttributesKey("ENV_NAME", DefaultLanguageHighlighterColors.IDENTIFIER);
    public static final TextAttributesKey ENV_VALUE =
            createTextAttributesKey("ENV_VALUE", DefaultLanguageHighlighterColors.IDENTIFIER);
    public static final TextAttributesKey ENV_EXPR =
            createTextAttributesKey("ENV_EXPR", DefaultLanguageHighlighterColors.IDENTIFIER);
    public static final TextAttributesKey QUPLA_ALIAS =
            createTextAttributesKey("QUPLA_ALIAS", DefaultLanguageHighlighterColors.TEMPLATE_LANGUAGE_COLOR);
    private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];



    private static final TextAttributesKey[] BAD_CHAR_KEYS = new TextAttributesKey[]{BAD_CHARACTER};
    private static final TextAttributesKey[] COMMENT_KEYS = new TextAttributesKey[]{COMMENT};
    private static final TextAttributesKey[] TEST_COMMENT_KEYS = new TextAttributesKey[]{QUPLA_TEST_ASSERTION_PREFIX};
    private static final TextAttributesKey[] EXPR_COMMENT_KEYS = new TextAttributesKey[]{QUPLA_EXPR_ASSERTION_PREFIX};
    private static final TextAttributesKey[] TEST_ASSERT_KEYS = new TextAttributesKey[]{QUPLA_TEST_ASSERTION};
    private static final TextAttributesKey[] EXPR_ASSERT_KEYS = new TextAttributesKey[]{QUPLA_EXPR_ASSERTION};
    private static final TextAttributesKey[] KEYWORD_KEYS = new TextAttributesKey[]{KEYWORD};
    private static final TextAttributesKey[] ENV_KEYWORD_KEYS = new TextAttributesKey[]{ENV_KEYWORD};
    private static final TextAttributesKey[] TRIT_KEYS = new TextAttributesKey[]{QUPLA_TRIT};
    private static final TextAttributesKey[] ALIAS_KEYS = new TextAttributesKey[]{QUPLA_ALIAS};

    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new QuplaLexerAdapter();
    }

    @NotNull
    @Override
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
        if (tokenType.equals(QuplaTypes.TEMPLATE_KEYWORD)) {
            return KEYWORD_KEYS;
        }else if (tokenType.equals(QuplaTypes.FUNC_KEYWORD)) {
            return KEYWORD_KEYS;
        }else if (tokenType.equals(QuplaTypes.LUT_KEYWORD)) {
            return KEYWORD_KEYS;
        }else if (tokenType.equals(QuplaTypes.RETURN_KEYWORD)) {
            return KEYWORD_KEYS;
        }else if (tokenType.equals(QuplaTypes.IMPORT_KEYWORD)) {
            return KEYWORD_KEYS;
        }else if (tokenType.equals(QuplaTypes.TYPE_KEYWORD)) {
            return KEYWORD_KEYS;
        }else if (tokenType.equals(QuplaTypes.USE_KEYWORD)) {
            return KEYWORD_KEYS;
        }else if (tokenType.equals(QuplaTypes.STATE_KEYWORD)) {
            return KEYWORD_KEYS;
        }else if (tokenType.equals(QuplaTypes.JOIN_KEYWORD)) {
            return ENV_KEYWORD_KEYS;
        }else if (tokenType.equals(QuplaTypes.LIMIT_KEYWORD)) {
            return ENV_KEYWORD_KEYS;
        }else if (tokenType.equals(QuplaTypes.AFFECT_KEYWORD)) {
            return ENV_KEYWORD_KEYS;
        }else if (tokenType.equals(QuplaTypes.DELAY_KEYWORD)) {
            return ENV_KEYWORD_KEYS;
        }else if (tokenType.equals(QuplaTypes.COMMENT)) {
            return COMMENT_KEYS;
        }else if (tokenType.equals(QuplaTypes.TEST_KEYWORD)) {
            return TEST_ASSERT_KEYS;
        }else if (tokenType.equals(QuplaTypes.EVAL_KEYWORD)) {
            return TEST_ASSERT_KEYS;
        }else if (tokenType.equals(QuplaTypes.NULL_KEYWORD)) {
            return KEYWORD_KEYS;
        }else if (tokenType.equals(QuplaTypes.SIZEOF_KEYWORD)) {
            return KEYWORD_KEYS;
        }else if (tokenType.equals(QuplaTypes.TRIT)) {
            return TRIT_KEYS;
        }else if (tokenType.equals(QuplaTypes.AT)) {
            return ALIAS_KEYS;
        }else if (tokenType.equals(TokenType.BAD_CHARACTER)) {
            return BAD_CHAR_KEYS;
        } else {
            return EMPTY_KEYS;
        }
    }

}