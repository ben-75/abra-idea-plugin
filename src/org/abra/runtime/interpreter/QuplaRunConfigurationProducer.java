package org.abra.runtime.interpreter;

import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.RunConfigurationProducer;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiElement;
import org.abra.language.psi.QuplaFile;
import org.abra.language.psi.QuplaFuncStmt;

public class QuplaRunConfigurationProducer extends RunConfigurationProducer<QuplaInterpreterRunConfiguration> {

    public QuplaRunConfigurationProducer() {
        super(QuplaInterpreterConfigurationType.getInstance());
    }

    public QuplaRunConfigurationProducer(ConfigurationFactory configurationFactory) {
        super(configurationFactory);
    }

    public QuplaRunConfigurationProducer(ConfigurationType configurationType) {
        super(configurationType);

    }

//    @Override
//    public void onFirstRun(@NotNull ConfigurationFromContext configuration, @NotNull ConfigurationContext context, @NotNull Runnable startRunnable) {
//        QuplaInterpreterSettingsEditor settingsEditor = new QuplaInterpreterSettingsEditor();
//        settingsEditor.resetFrom((QuplaInterpreterRunConfiguration) configuration.getConfiguration());
//
//        settingsEditor.super.onFirstRun(configuration, context, startRunnable);
//    }

    @Override
    protected boolean setupConfigurationFromContext(QuplaInterpreterRunConfiguration configuration, ConfigurationContext context, Ref<PsiElement> sourceElement) {
        if(sourceElement.get().getContainingFile() instanceof QuplaFile){
            QuplaFuncStmt func = findFuncStmt(sourceElement.get());
            if(func==null)return false;
            configuration.setTargetModule((QuplaFile) func.getContainingFile());
            configuration.setTargetFunc(func);
            configuration.setName(func.getFuncSignature().getFuncName().getText());
            return true;
        }
        return false;
    }

    @Override
    public boolean isConfigurationFromContext(QuplaInterpreterRunConfiguration configuration, ConfigurationContext context) {
//        QuplaFuncStmt func = findFuncStmt(context.getPsiLocation());
//        return func!=null && func.isEquivalentTo(configuration.getTargetFunc());
        return false;
    }

    private QuplaFuncStmt findFuncStmt(PsiElement f){
        while(! (f instanceof QuplaFuncStmt) && ! (f instanceof QuplaFile) && f!=null){
            f = f.getParent();
        }
        if(f instanceof QuplaFile)return null;
        return (QuplaFuncStmt) f;
    }
}
