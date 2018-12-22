package org.abra.language;

import com.intellij.lang.Language;

public class AbraLanguage extends Language {
    public static final AbraLanguage INSTANCE = new AbraLanguage();

    private AbraLanguage() {
        super("Qupla");
    }
}
