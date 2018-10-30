package org.abra;

import com.intellij.ide.DataManager;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.DataConstants;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.options.ConfigurableUi;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AbraPluginConfigurableUI implements ConfigurableUi<AbraPluginSettings> {

    private JTextField abraInterpreterPathTextField;
    private JPanel rootPanel;
    private JButton fileChooserButton;

    @Override
    public void reset(@NotNull AbraPluginSettings settings) {
        //add(abraInterpreterPathTextField);
        abraInterpreterPathTextField.setText(settings.getAbraInterpreterPath());
        fileChooserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileChooserDescriptor descriptor = new FileChooserDescriptor(false,false,true,true,
                        false,false);

                descriptor.setTitle("Select abra interpreter jar (abra.jar)");
                Project project = DataManager.getInstance().getDataContext(abraInterpreterPathTextField).getData(PlatformDataKeys.PROJECT);
                //e.getDataContext().getData(PlatformDataKeys.PROJECT);
                FileChooser.chooseFile(
                        descriptor,
                        project,project.getBaseDir().getParent(), f->abraInterpreterPathTextField.setText(f.getCanonicalPath()));
            }
        });
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
