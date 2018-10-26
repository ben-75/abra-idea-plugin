package org.abra;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurableBase;
import com.intellij.openapi.options.ConfigurableUi;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AbraPluginConfigurable extends ConfigurableBase<AbraPluginConfigurableUI,AbraPluginSettings> implements Configurable {

    public AbraPluginConfigurable() {
        super("org.abra.language", "Abra Runtime", null);
    }

    @NotNull
    @Override
    protected AbraPluginSettings getSettings() {
        AbraPluginSettings settings = new AbraPluginSettings();
        settings.setAbraInterpreterPath(PropertiesComponent.getInstance().getValue("org.abra.language.interpreterpath"));
        return settings;
    }

    @Override
    protected AbraPluginConfigurableUI createUi() {
        return new AbraPluginConfigurableUI();
    }
}
