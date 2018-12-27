package org.qupla.runtime.interpreter;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import org.qupla.ide.ui.QuplaIcons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class QuplaInterpreterConfigurationType implements ConfigurationType {

    private static QuplaInterpreterConfigurationType instance = new QuplaInterpreterConfigurationType();

    public static QuplaInterpreterConfigurationType getInstance(){
        return instance;
    }
    @Override
    public String getDisplayName() {
        return "Qupla Interpreter";
    }

    @Override
    public String getConfigurationTypeDescription() {
        return "Qupla Interpreter Run Configuration Type";
    }

    @Override
    public Icon getIcon() {
        return QuplaIcons.RUN;
    }

    @NotNull
    @Override
    public String getId() {
        return "QUPLA_INTERPRETER_RUN_CONFIGURATION";
    }

    @Override
    public ConfigurationFactory[] getConfigurationFactories() {
        return new ConfigurationFactory[]{new QuplaInterpreterConfigurationFactory(this)};
    }
}
