package org.abra.interpreter.runconfig;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.project.Project;

public class AbraInterpreterConfigurationFactory  extends ConfigurationFactory {

    private static final String FACTORY_NAME = "Abra Interpreter configuration factory";

    protected AbraInterpreterConfigurationFactory(ConfigurationType type) {
        super(type);
    }

    @Override
    public RunConfiguration createTemplateConfiguration(Project project) {
        return new AbraInterpreterRunConfiguration(project, this, "Abra Interpreter");
    }

    @Override
    public String getName() {
        return FACTORY_NAME;
    }
}