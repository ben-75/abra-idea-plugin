package org.qupla.language;

import com.intellij.lexer.FlexAdapter;

import java.io.Reader;

public class QuplaLexerAdapter extends FlexAdapter {
    public QuplaLexerAdapter() {
        super(new QuplaLexer((Reader) null));
    }
}