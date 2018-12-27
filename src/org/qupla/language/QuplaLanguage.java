package org.qupla.language;

import com.intellij.lang.Language;

public class QuplaLanguage extends Language {
    public static final QuplaLanguage INSTANCE = new QuplaLanguage();

    private QuplaLanguage() {
        super("Qupla");
    }
}
