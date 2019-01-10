package org.qupla.runtime.interpreter;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.application.ApplicationConfiguration;
import com.intellij.execution.configurations.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.psi.tree.TokenSet;
import org.qupla.language.psi.*;
import org.jdom.Attribute;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class QuplaInterpreterRunConfiguration extends ApplicationConfiguration {

    private boolean test = false;
    private boolean eval = false;
    private boolean echo = false;
    private boolean abra = false;
    private boolean trim = false;
    private boolean tree = false;
    private boolean fpga = false;

    private QuplaFile targetModule = null;
    private QuplaFuncStmt targetFunc = null;
    private QuplaTypeInstantiation targetTypeInstantiation = null;
    private QuplaTypeName targetTypeName = null;
    String[] args;

    protected QuplaInterpreterRunConfiguration(Project project, ConfigurationFactory factory, String name) {
        super(name, project, factory);//, new QuplaInterpreterConfigurationFactory(QuplaInterpreterConfigurationType.getInstance()));

    }

    @NotNull
    @Override
    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        return new QuplaInterpreterSettingsEditor();
    }

    @Override
    public void checkConfiguration() throws RuntimeConfigurationException {
        if (targetModule == null) throw new RuntimeConfigurationException("Module is not defined");
        if (targetFunc == null && !test && !eval && !fpga && !tree ) {
            throw new RuntimeConfigurationException("Execution target (eval, test, tree or function) is not defined");
        }

        if (targetFunc != null) {
            String funcName = null;
            if (targetFunc instanceof QuplaUseStmt) {
                funcName = ((QuplaUseStmt) targetFunc).getTemplateNameRef().getText();
            } else if (targetFunc instanceof QuplaFuncStmt) {
                funcName = targetFunc.getFuncSignature().getFuncName().getText();
            }
            if (funcName == null) {
                throw new RuntimeConfigurationException("Element " + targetFunc.getText() + " is not a valid evaluation target");
            }
            boolean foundTargetFunc = false;
            for (QuplaFuncStmt n : targetModule.getAllFuncStmts()) {
                if (n.getFuncSignature().getFuncName().getText().equals(funcName)) {
                    foundTargetFunc = true;
                    break;
                }
            }

            if (!foundTargetFunc) {
                throw new RuntimeConfigurationException("Function " + funcName + " is not defined");
            }
            if(args==null || args.length==0)
                throw new RuntimeConfigurationException("Parameters not defined");

            if(args.length<targetFunc.getFuncSignature().getFuncParameterList().size())
                throw new RuntimeConfigurationException("Parameters missing");
            for(int i=0;i<targetFunc.getFuncSignature().getFuncParameterList().size();i++){
                if(args[i]==null){
                    throw new RuntimeConfigurationException("Parameter empty");
                }
            }
        }
    }

    @Nullable
    @Override
    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment executionEnvironment) throws ExecutionException {
        return new QuplaInterpreterState(executionEnvironment, this);
    }

    public boolean isRunTest() {
        return test;
    }

    public void setRunTest(boolean test) {
        this.test = test;
    }

    public boolean isRunEval() {
        return eval;
    }

    public void setRunEval(boolean eval) {
        this.eval = eval;
    }

    public boolean isEcho() {
        return echo;
    }

    public void setEcho(boolean echo) {
        this.echo = echo;
    }

    public boolean isAbra() {
        return abra;
    }

    public void setAbra(boolean abra) {
        this.abra = abra;
    }

    public boolean isTrim() {
        return trim;
    }

    public void setTrim(boolean trim) {
        this.trim = trim;
    }

    public boolean isTree() {
        return tree;
    }

    public void setTree(boolean tree) {
        this.tree = tree;
    }

    public boolean isFpga() {
        return fpga;
    }

    public void setFpga(boolean fpga) {
        this.fpga = fpga;
    }

    public QuplaFile getTargetModule() {
        return targetModule;
    }

    public void setTargetModule(QuplaFile targetModule) {
        this.targetModule = targetModule;
    }

    public QuplaFuncStmt getTargetFunc() {
        return targetFunc;
    }

    public void setTargetFunc(QuplaFuncStmt targetFunc) {
        this.targetFunc = targetFunc;
    }

    public QuplaTypeInstantiation getTargetTypeInstantiation() {
        return targetTypeInstantiation;
    }

    public void setTargetTypeInstantiation(QuplaTypeInstantiation targetTypeInstantiation) {
        this.targetTypeInstantiation = targetTypeInstantiation;
    }

    public QuplaTypeName getTargetTypeName() {
        return targetTypeName;
    }

    public void setTargetTypeName(QuplaTypeName targetTypeName) {
        this.targetTypeName = targetTypeName;
    }

    public boolean hasArgs(){
        return args!=null && args.length>0;
    }
    public List<String> getArgs() {
        return Arrays.asList(args);
    }

    public void setArgs(List<String> args) {
        this.args = (String[]) args.toArray();
    }

    @Override
    public void writeExternal(@NotNull final Element element) throws WriteExternalException {
        ApplicationManager.getApplication().runReadAction(new Runnable() {
            @Override
            public void run() {
                element.setAttribute("test",test?"true":"false");
                element.setAttribute("eval",eval?"true":"false");
                element.setAttribute("echo",echo?"true":"false");
                element.setAttribute("abra",abra?"true":"false");
                element.setAttribute("fpga",fpga?"true":"false");
                element.setAttribute("tree",tree?"true":"false");
                element.setAttribute("trim",trim?"true":"false");
                if (targetModule != null) {
                    element.setAttribute("targetModule", targetModule.getImportableFilePath());
                    if (targetFunc != null) {
                        if (targetFunc.isInTemplate()) {
                            element.setAttribute("targetFunc", ((QuplaTemplateStmt) targetFunc.getParent()).getTemplateName().getText() + ":" + targetFunc.getFuncSignature().getFuncName().getText());
                        } else {
                            element.setAttribute("targetFunc", targetFunc.getFuncSignature().getFuncName().getText());
                        }
                        if (targetTypeInstantiation != null) {
                            element.setAttribute("targetTypeInstantiation", targetTypeInstantiation.getText().substring(1, targetTypeInstantiation.getTextLength() - 1));
                        }
                        if (targetTypeName != null) {
                            element.setAttribute("targetTypeName", targetTypeName.getText());
                        }
                        for(int i=0;i<targetFunc.getFuncSignature().getFuncParameterList().size();i++){
                            if(args!=null && args.length>i && args[i]!=null && args[i].length()>0){
                                element.setAttribute("arg_"+i, args[i]);
                            }
                        }
                    }
                }
            }
        });
        super.writeExternal(element);
    }

    @Override
    public void readExternal(@NotNull final Element element) throws InvalidDataException {
        super.readExternal(element);

        ApplicationManager.getApplication().runReadAction(new Runnable() {

            private boolean attributeEquals(String attr, String val){
                return element.getAttribute(attr)!=null && val.equals(element.getAttribute(attr).getValue());
            }
            @Override
            public void run() {
                test = attributeEquals("test","true");
                eval = attributeEquals("eval","true");
                echo = attributeEquals("echo","true");
                abra = attributeEquals("abra","true");
                fpga = attributeEquals("fpga","true");
                tree = attributeEquals("tree","true");
                trim = attributeEquals("trim","true");
                Attribute targetMod = element.getAttribute("targetModule");
                if (targetMod != null) {
                    targetModule = QuplaPsiImplUtil.findFileForPath(getProject(), targetMod.getValue());
                    if (targetModule != null) {
                        Attribute targetF = element.getAttribute("targetFunc");
                        if (targetF != null) {
                            String tmp = targetF.getValue();
                            if (tmp.contains(":")) {
                                String tmpl_name = tmp.substring(0, tmp.indexOf(":"));
                                String func_name = tmp.substring(tmp.indexOf(":") + 1);
                                QuplaTemplateStmt tmpl = targetModule.getTemplate(tmpl_name);
                                if (tmpl != null) {
                                    for (ASTNode stmt : tmpl.getNode().getChildren(TokenSet.create(QuplaTypes.FUNC_STMT))) {
                                        if (((QuplaFuncStmt) stmt.getPsi()).getFuncSignature().getFuncName().getText().equals(func_name)) {
                                            targetFunc = (QuplaFuncStmt) stmt.getPsi();
                                            break;
                                        }
                                    }
                                }
                            } else {
                                targetFunc = targetModule.getStandaloneFunc(tmp);
                            }
                            if (targetFunc != null) {
                                if (targetFunc.isInTemplate()) {
                                    //process type inst
                                    Attribute targetType = element.getAttribute("targetTypeInstantiation");
                                    if (targetType != null) {
                                        String tmp2 = "<" + targetType.getValue() + ">";
                                        for (QuplaTypeInstantiation typInst : targetFunc.getAllTypeInstantiation()) {
                                            if (tmp2.equals(typInst.getText())) {
                                                targetTypeInstantiation = typInst;
                                                break;
                                            }
                                        }
                                    }

                                    Attribute targetTypeNameAttr = element.getAttribute("targetTypeName");
                                    if (targetTypeNameAttr != null) {
                                        String tmp2 = targetTypeNameAttr.getValue();
                                        for (QuplaTypeName typInst : ((QuplaFile)targetFunc.getContainingFile()).findAllVisibleConcreteTypeName(null)) {
                                            if (tmp2.equals(typInst.getText())) {
                                                targetTypeName = typInst;
                                                break;
                                            }
                                        }
                                    }
                                }
                                for(int i=0;i<targetFunc.getFuncSignature().getFuncParameterList().size();i++){
                                    Attribute arg = element.getAttribute("arg_"+i);
                                    if(arg!=null){
                                        if(args==null) args=new String[targetFunc.getFuncSignature().getFuncParameterList().size()];
                                        args[i]=arg.getValue();
                                    }
                                }
                            }

                        }
                    }
                }
            }
        });
    }
}
