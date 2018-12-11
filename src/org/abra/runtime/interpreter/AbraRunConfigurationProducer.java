package org.abra.runtime.interpreter;

import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.RunConfigurationProducer;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiElement;
import org.abra.language.psi.AbraFile;
import org.abra.language.psi.AbraFuncStmt;

public class AbraRunConfigurationProducer extends RunConfigurationProducer<AbraInterpreterRunConfiguration> {

    public AbraRunConfigurationProducer() {
        super(AbraInterpreterConfigurationType.getInstance());
    }

    public AbraRunConfigurationProducer(ConfigurationFactory configurationFactory) {
        super(configurationFactory);
    }

    public AbraRunConfigurationProducer(ConfigurationType configurationType) {
        super(configurationType);

    }

//    @Override
//    public void onFirstRun(@NotNull ConfigurationFromContext configuration, @NotNull ConfigurationContext context, @NotNull Runnable startRunnable) {
//        AbraInterpreterSettingsEditor settingsEditor = new AbraInterpreterSettingsEditor();
//        settingsEditor.resetFrom((AbraInterpreterRunConfiguration) configuration.getConfiguration());
//
//        settingsEditor.super.onFirstRun(configuration, context, startRunnable);
//    }

    @Override
    protected boolean setupConfigurationFromContext(AbraInterpreterRunConfiguration configuration, ConfigurationContext context, Ref<PsiElement> sourceElement) {
        if(sourceElement.get().getContainingFile() instanceof AbraFile){
            AbraFuncStmt func = findFuncStmt(sourceElement.get());
            if(func==null)return false;
            configuration.setTargetModule((AbraFile) func.getContainingFile());
            configuration.setTargetFunc(func);
            configuration.setName(func.getFuncSignature().getFuncName().getText());
            return true;
        }
        return false;
    }

    @Override
    public boolean isConfigurationFromContext(AbraInterpreterRunConfiguration configuration, ConfigurationContext context) {
//        AbraFuncStmt func = findFuncStmt(context.getPsiLocation());
//        return func!=null && func.isEquivalentTo(configuration.getTargetFunc());
        return false;
    }

    private AbraFuncStmt findFuncStmt(PsiElement f){
        while(! (f instanceof AbraFuncStmt) && ! (f instanceof AbraFile) && f!=null){
            f = f.getParent();
        }
        if(f instanceof AbraFile)return null;
        return (AbraFuncStmt) f;
    }
}
