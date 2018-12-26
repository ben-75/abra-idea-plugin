package org.abra.language;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.abra.ide.ui.QuplaIcons;
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
        return "Qupla file";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Qupla language file";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "qpl";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return QuplaIcons.FILE;
    }
}