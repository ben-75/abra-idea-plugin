package org.abra.interpreter.runconfig;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.icons.AllIcons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class AbraInterpreterConfigurationType implements ConfigurationType {
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
        return AllIcons.General.Information;
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
