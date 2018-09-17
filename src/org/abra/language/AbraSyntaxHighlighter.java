package org.abra.language;

import com.intellij.lang.BracePair;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.PsiFile;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import org.abra.language.psi.AbraTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.intellij.openapi.editor.DefaultLanguageHighlighterColors.KEYWORD;
import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

public class AbraSyntaxHighlighter extends SyntaxHighlighterBase  {

    public static final TextAttributesKey COMMENT =
            createTextAttributesKey("SIMPLE_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);
    public static final TextAttributesKey BAD_CHARACTER =
            createTextAttributesKey("SIMPLE_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER);


    public static final TextAttributesKey TYPE_DECLARATION =
            createTextAttributesKey("TYPE_DECLARATION", DefaultLanguageHighlighterColors.TEMPLATE_LANGUAGE_COLOR);
    public static final TextAttributesKey FIELD_DECLARATION =
            createTextAttributesKey("FIELD_DECLARATION", DefaultLanguageHighlighterColors.GLOBAL_VARIABLE);
    public static final TextAttributesKey FCT_DECLARATION =
            createTextAttributesKey("FCT_DECLARATION", DefaultLanguageHighlighterColors.STRING);
    public static final TextAttributesKey LUT_DECLARATION =
            createTextAttributesKey("LUT_DECLARATION", DefaultLanguageHighlighterColors.INSTANCE_FIELD);
    public static final TextAttributesKey SIZE_DEF =
            createTextAttributesKey("LUT_DECLARATION", DefaultLanguageHighlighterColors.NUMBER);
    public static final TextAttributesKey LOCAL_VAR =
            createTextAttributesKey("LOCAL_VAR", DefaultLanguageHighlighterColors.LOCAL_VARIABLE);


    private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];



    private static final TextAttributesKey[] BAD_CHAR_KEYS = new TextAttributesKey[]{BAD_CHARACTER};
    private static final TextAttributesKey[] COMMENT_KEYS = new TextAttributesKey[]{COMMENT};
    private static final TextAttributesKey[] KEYWORD_KEYS = new TextAttributesKey[]{KEYWORD};

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
        }else if (tokenType.equals(AbraTypes.COMMENT)) {
            return COMMENT_KEYS;
        } else if (tokenType.equals(TokenType.BAD_CHARACTER)) {
            return BAD_CHAR_KEYS;
        } else {
            return EMPTY_KEYS;
        }
    }

}