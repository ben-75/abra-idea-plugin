package org.abra.runtime.runconfig;

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
import org.abra.language.psi.*;
import org.jdom.Attribute;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class AbraInterpreterRunConfiguration extends ApplicationConfiguration {

    private boolean test = false;
    private boolean eval = false;
    private boolean echo = false;
    private boolean emit = false;
    private boolean trim = false;
    private boolean tree = false;
    private boolean fpga = false;

    private AbraFile targetModule = null;
    private AbraFuncStmt targetFunc = null;
    private AbraTypeInstantiation targetTypeInstantiation = null;
    String[] args;

    protected AbraInterpreterRunConfiguration(Project project, ConfigurationFactory factory, String name) {
        super(name, project, factory);//, new AbraInterpreterConfigurationFactory(AbraInterpreterConfigurationType.getInstance()));

    }

    @NotNull
    @Override
    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        return new AbraInterpreterSettingsEditor();
    }

    @Override
    public void checkConfiguration() throws RuntimeConfigurationException {
//        if(PropertiesComponent.getInstance().getValue("org.abra.language.interpreterpath")==null){
//            throw new RuntimeConfigurationException("AbraInterpreter path is not defined (settings)");
//        }
//        if(PropertiesComponent.getInstance().getValue("org.abra.language.interpreterpath").length()==0){
//            throw new RuntimeConfigurationException("AbraInterpreter path is not defined (settings)");
//        }
        if (targetModule == null) throw new RuntimeConfigurationException("Module is not defined");
        if (targetFunc == null && !test && !eval && !fpga && !tree ) {
            throw new RuntimeConfigurationException("Execution target (eval, test, tree or function) is not defined");
        }

        if (targetFunc != null) {
            String funcName = null;
            if (targetFunc instanceof AbraUseStmt) {
                funcName = ((AbraUseStmt) targetFunc).getTemplateNameRef().getText();
            } else if (targetFunc instanceof AbraFuncStmt) {
                funcName = targetFunc.getFuncSignature().getFuncName().getText();
            }
            if (funcName == null) {
                throw new RuntimeConfigurationException("Element " + targetFunc.getText() + " is not a valid evaluation target");
            }
            boolean foundTargetFunc = false;
            for (AbraFuncStmt n : targetModule.getAllFuncStmts()) {
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
        return new AbraInterpreterState(executionEnvironment, this);
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

    public boolean isEmit() {
        return emit;
    }

    public void setEmit(boolean emit) {
        this.emit = emit;
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

    public AbraFile getTargetModule() {
        return targetModule;
    }

    public void setTargetModule(AbraFile targetModule) {
        this.targetModule = targetModule;
    }

    public AbraFuncStmt getTargetFunc() {
        return targetFunc;
    }

    public void setTargetFunc(AbraFuncStmt targetFunc) {
        this.targetFunc = targetFunc;
    }

    public AbraTypeInstantiation getTargetTypeInstantiation() {
        return targetTypeInstantiation;
    }

    public void setTargetTypeInstantiation(AbraTypeInstantiation targetTypeInstantiation) {
        this.targetTypeInstantiation = targetTypeInstantiation;
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
                element.setAttribute("emit",emit?"true":"false");
                element.setAttribute("fpga",fpga?"true":"false");
                element.setAttribute("tree",tree?"true":"false");
                element.setAttribute("trim",trim?"true":"false");
                if (targetModule != null) {
                    element.setAttribute("targetModule", targetModule.getImportableFilePath());
                    if (targetFunc != null) {
                        if (targetFunc.isInTemplate()) {
                            element.setAttribute("targetFunc", ((AbraTemplateStmt) targetFunc.getParent()).getTemplateName().getText() + ":" + targetFunc.getFuncSignature().getFuncName().getText());
                        } else {
                            element.setAttribute("targetFunc", targetFunc.getFuncSignature().getFuncName().getText());
                        }
                        if (targetTypeInstantiation != null) {
                            element.setAttribute("targetTypeInstantiation", targetTypeInstantiation.getText().substring(1, targetTypeInstantiation.getTextLength() - 1));
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
                emit = attributeEquals("emit","true");
                fpga = attributeEquals("fpga","true");
                tree = attributeEquals("tree","true");
                trim = attributeEquals("trim","true");
                Attribute targetMod = element.getAttribute("targetModule");
                if (targetMod != null) {
                    targetModule = AbraPsiImplUtil.findFileForPath(getProject(), targetMod.getValue());
                    if (targetModule != null) {
                        Attribute targetF = element.getAttribute("targetFunc");
                        if (targetF != null) {
                            String tmp = targetF.getValue();
                            if (tmp.contains(":")) {
                                String tmpl_name = tmp.substring(0, tmp.indexOf(":"));
                                String func_name = tmp.substring(tmp.indexOf(":") + 1);
                                AbraTemplateStmt tmpl = targetModule.getTemplate(tmpl_name);
                                if (tmpl != null) {
                                    for (ASTNode stmt : tmpl.getNode().getChildren(TokenSet.create(AbraTypes.FUNC_STMT))) {
                                        if (((AbraFuncStmt) stmt.getPsi()).getFuncSignature().getFuncName().getText().equals(func_name)) {
                                            targetFunc = (AbraFuncStmt) stmt.getPsi();
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
                                        for (AbraTypeInstantiation typInst : targetFunc.getAllTypeInstantiation()) {
                                            if (tmp2.equals(typInst.getText())) {
                                                targetTypeInstantiation = typInst;
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
