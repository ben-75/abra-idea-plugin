package org.abra.runtime.runconfig;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import org.abra.ide.ui.AbraIcons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class AbraInterpreterConfigurationType implements ConfigurationType {

    private static AbraInterpreterConfigurationType instance = new AbraInterpreterConfigurationType();

    public static AbraInterpreterConfigurationType getInstance(){
        return instance;
    }
    @Override
    public String getDisplayName() {
        return "Abra Interpreter";
    }

    @Override
    public String getConfigurationTypeDescription() {
        return "Abra Interpreter Run Configuration Type";
    }

    @Override
    public Icon getIcon() {
        return AbraIcons.RUN;
    }

    @NotNull
    @Override
    public String getId() {
        return "ABRA_INTERPRETER_RUN_CONFIGURATION";
    }

    @Override
    public ConfigurationFactory[] getConfigurationFactories() {
        return new ConfigurationFactory[]{new AbraInterpreterConfigurationFactory(this)};
    }
}
