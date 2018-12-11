package org.abra.language;

import com.intellij.lexer.FlexAdapter;

import java.io.Reader;

public class AbraLexerAdapter extends FlexAdapter {

    public AbraLexerAdapter() {
        super(new AbraLexer((Reader) null));
    }
}