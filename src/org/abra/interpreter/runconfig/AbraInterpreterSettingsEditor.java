package org.abra.interpreter.runconfig;

import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.roots.ContentIterator;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.ui.ComponentWithBrowseButton;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileFilter;
import com.intellij.openapi.vfs.newvfs.impl.VirtualDirectoryImpl;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class AbraInterpreterSettingsEditor extends SettingsEditor<AbraInterpreterRunConfiguration> {
    private AbraInterpreterRunConfigUI myPanel;
    private LabeledComponent<ComponentWithBrowseButton> myMainClass;



    @Override
    protected void resetEditorFrom(AbraInterpreterRunConfiguration abraInterpreterRunConfiguration) {
        myPanel.modules.setModel(abraInterpreterRunConfiguration.getModulesModel());

    }

    @Override
    protected void applyEditorTo(AbraInterpreterRunConfiguration abraInterpreterRunConfiguration) throws ConfigurationException {
        //myPanel.modules.setModel(abraInterpreterRunConfiguration.getModulesModel());
    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        myPanel = new AbraInterpreterRunConfigUI();
        return myPanel.rootConfigPane;
    }

    private void createUIComponents() {
        myMainClass = new LabeledComponent<ComponentWithBrowseButton>();
        myMainClass.setComponent(new TextFieldWithBrowseButton());
    }

}