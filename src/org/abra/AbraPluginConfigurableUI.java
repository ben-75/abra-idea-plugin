package org.abra;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.options.ConfigurableUi;
import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class AbraPluginConfigurableUI implements ConfigurableUi<AbraPluginSettings> {

    private JTextField abraInterpreterPathTextField;
    private JPanel rootPanel;

    @Override
    public void reset(@NotNull AbraPluginSettings settings) {
        //add(abraInterpreterPathTextField);
        abraInterpreterPathTextField.setText(settings.getAbraInterpreterPath());
    }

    @Override
    public boolean isModified(@NotNull AbraPluginSettings settings) {
        return !(abraInterpreterPathTextField.getText().equals(settings.getAbraInterpreterPath()));
    }

    @Override
    public void apply(@NotNull AbraPluginSettings settings) throws ConfigurationException {
        settings.setAbraInterpreterPath(abraInterpreterPathTextField.getText());
        PropertiesComponent.getInstance().setValue("org.abra.language.interpreterpath",settings.getAbraInterpreterPath());
    }

    @NotNull
    @Override
    public JComponent getComponent() {
        return rootPanel;
    }
}
