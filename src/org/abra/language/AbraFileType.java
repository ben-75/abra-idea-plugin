package org.abra.language;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.*;

import javax.swing.*;

public class AbraFileType extends LanguageFileType {
    public static final AbraFileType INSTANCE = new AbraFileType();

    private AbraFileType() {
        super(AbraLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "Àbra file";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Abra language file";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "abra";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return AbraIcons.FILE;
    }
}