package org.qupla.language;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.qupla.ide.ui.QuplaIcons;
import org.jetbrains.annotations.*;

import javax.swing.*;

public class QuplaFileType extends LanguageFileType {
    public static final QuplaFileType INSTANCE = new QuplaFileType();

    private QuplaFileType() {
        super(QuplaLanguage.INSTANCE);
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