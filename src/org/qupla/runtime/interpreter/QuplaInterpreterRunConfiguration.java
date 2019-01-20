package org.qupla.runtime.interpreter;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.application.ApplicationConfiguration;
import com.intellij.execution.configurations.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import org.qupla.language.module.QuplaModule;
import org.qupla.language.module.QuplaModuleManager;
import org.qupla.language.psi.*;
import org.jdom.Attribute;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QuplaInterpreterRunConfiguration extends ApplicationConfiguration {

    private boolean test = false;
    private boolean eval = false;
    private boolean echo = false;
    private boolean abra = false;
    private boolean view = false;
    private boolean trim = false;
    private boolean tree = false;
    private boolean fpga = false;
    private String runMode;
    private String customArgs;

    private List<QuplaModule> quplaModules = null;
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
        if (quplaModules == null || quplaModules.size()==0) throw new RuntimeConfigurationException("Module is not defined");
        if (targetFunc == null && !test && !eval && !fpga && !tree ) {
            throw new RuntimeConfigurationException("Execution target (eval, test, tree or function) is not defined");
        }

        if (targetFunc != null) {
            String funcName = targetFunc.getFuncSignature().getFuncName().getText();
            if(targetFunc.isInTemplate()){

            }
            boolean foundTargetFunc = false;
            for (QuplaModule targetModule : quplaModules){
                if(targetFunc.isInTemplate()){
                    QuplaFuncStmt func = targetModule.findFuncStmt(((QuplaTemplateStmt)targetFunc.getParent()).getTemplateName().getText(),targetFunc.getFuncSignature().getFuncName().getText());
                    if(func!=null){
                        foundTargetFunc = true;
                        break;
                    }
                }else{
                    QuplaFuncStmt func = targetModule.findFuncStmt(null,targetFunc.getFuncSignature().getFuncName().getText());
                    if(func!=null){
                        foundTargetFunc = true;
                        break;
                    }
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


    public List<QuplaModule> getQuplaModules() {
        return quplaModules;
    }

    public void setQuplaModules(List<QuplaModule> quplaModules) {
        this.quplaModules = quplaModules;
    }

    public String getRunMode() {
        return runMode;
    }

    public void setRunMode(String runMode) {
        this.runMode = runMode;
    }

    public String getCustomArgs() {
        return customArgs;
    }

    public void setCustomArgs(String customArgs) {
        this.customArgs = customArgs;
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

    public boolean isView() {
        return view;
    }

    public void setView(boolean view) {
        this.view = view;
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
                element.setAttribute("view",view?"true":"false");
                element.setAttribute("fpga",fpga?"true":"false");
                element.setAttribute("tree",tree?"true":"false");
                element.setAttribute("trim",trim?"true":"false");
                element.setAttribute("runMode",runMode==null?"function":runMode);
                element.setAttribute("customArgs",customArgs==null?"":customArgs);
                StringBuilder sb = new StringBuilder();
                if(quplaModules!=null) {
                    for (QuplaModule m : quplaModules) {
                        sb.append(m.getName()).append(" ");
                    }
                }else{
                    quplaModules = new ArrayList<>();
                }
                element.setAttribute("quplaModules",sb.toString().trim());

                if (quplaModules.size()>0) {
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

                //flags
                test = attributeEquals("test","true");
                eval = attributeEquals("eval","true");
                echo = attributeEquals("echo","true");
                abra = attributeEquals("abra","true");
                view = attributeEquals("view","true");
                fpga = attributeEquals("fpga","true");
                tree = attributeEquals("tree","true");
                trim = attributeEquals("trim","true");
                runMode = element.getAttribute("runMode")==null?"function":element.getAttributeValue("runMode");
                customArgs = element.getAttribute("customArgs")==null?"":element.getAttributeValue("customArgs");
                //modules
                quplaModules = new ArrayList<>();
                QuplaModuleManager moduleManager = getProject().getComponent(QuplaModuleManager.class);

                //upgrade code (can be deleted at some point)
                try{
                    String legacyTargetModule = element.getAttributeValue("targetModule");
                    if(legacyTargetModule!=null){
                        if(legacyTargetModule.indexOf("/")>-1){
                            legacyTargetModule = legacyTargetModule.substring(0,legacyTargetModule.indexOf("/"));
                        }
                        QuplaModule legacy = moduleManager.getModule(legacyTargetModule);
                        if(legacy!=null) {
                            quplaModules.add(legacy);
                        }
                    }
                }catch (Exception e){
                    //ignore
                }
                //end of upgrade code

                Attribute quplaMod = element.getAttribute("quplaModules");
                if(quplaMod!=null && quplaMod.getValue().length()>0){
                    String[] split = quplaMod.getValue().split(" ");
                    for(String m:split){
                        QuplaModule quplaModule = moduleManager.getModule(m);
                        if(quplaModule!=null && !quplaModules.contains(quplaModule)){
                            quplaModules.add(quplaModule);
                        }
                    }
                }


                if (quplaModules.size()>0) {
                        Attribute targetF = element.getAttribute("targetFunc");
                        if (targetF != null) {
                            String tmp = targetF.getValue();
                            String tmpl_name = null;
                            String func_name = null;
                            if (tmp.contains(":")) {
                                tmpl_name = tmp.substring(0, tmp.indexOf(":"));
                                func_name = tmp.substring(tmp.indexOf(":") + 1);
                            }else{
                                func_name = tmp;
                            }
                            for(QuplaModule module:quplaModules){
                                if(targetFunc==null) {
                                    targetFunc = module.findFuncStmt(tmpl_name, func_name);
                                }
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
        });
    }
}
