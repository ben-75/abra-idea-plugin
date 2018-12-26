package org.abra.runtime.interpreter;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class QuplaInterpreterConfigurationFactory extends ConfigurationFactory {

    private static final String FACTORY_NAME = "Qupla Interpreter configuration factory";

    protected QuplaInterpreterConfigurationFactory(ConfigurationType type) {
        super(type);
    }

    @Override
    @NotNull
    public RunConfiguration createTemplateConfiguration(@NotNull Project project) {
        return new QuplaInterpreterRunConfiguration(project, this, "Abra Interpreter");
    }

    @Override
    public String getName() {
        return FACTORY_NAME;
    }
}