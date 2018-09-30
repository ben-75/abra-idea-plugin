package org.abra.interpreter.action.org.abra.interpreter.action;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiElement;
import org.abra.interpreter.AbraEvaluationContext;
import org.abra.interpreter.FuncEvaluator;
import org.abra.language.psi.AbraFuncDefinition;
import org.abra.language.psi.AbraFuncName;
import org.abra.language.psi.AbraFuncParameter;
import org.abra.language.psi.AbraParamName;
import org.abra.utils.TRIT;
import org.abra.utils.TritUtils;

public class RunAbraInterpreter extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        System.out.println("RunAbraInterpreter");

        PsiElement element = e.getData(LangDataKeys.PSI_ELEMENT);
        if(element instanceof AbraFuncName){

            StartInterpreterDialog dialog = new StartInterpreterDialog((AbraFuncName) element);
            dialog.pack();
            dialog.show();//(true);
            if (dialog.getExitCode() == DialogWrapper.OK_EXIT_CODE) {
                AbraFuncDefinition funcDefinition = (AbraFuncDefinition) element.getParent();
                AbraEvaluationContext rootContext = new AbraEvaluationContext();
                AbraEvaluationContext evaluationContext = new AbraEvaluationContext();
                for(AbraFuncParameter param:funcDefinition.getFuncParameterList()){
                    rootContext.add(param.getParamName(),dialog.getTrits(param.getParamName()));
                 //   evaluationContext.add(param.getParamName(),dialog.getTrits(param.getParamName()));
                }
                rootContext.pushChildContext(evaluationContext);
                TRIT[] response = FuncEvaluator.execute(rootContext,funcDefinition,evaluationContext);
                System.out.println("TRITS :"+ TritUtils.trit2String(response));
                System.out.println("DECIM :"+ TritUtils.trit2Decimal(response));
                System.out.println("TRYTES:"+ TritUtils.trit2Trytes(response));
            }
//            ((AbraFuncDefinition)element.getParent()).getFuncParameterList()
//            Project project = e.getData(PlatformDataKeys.PROJECT);
//            String txt= Messages.showInputDialog(project, "What is your name?", "Input your name", Messages.getQuestionIcon());
//            Messages.showMessageDialog(project, "Hello, " + txt + "!\n I am glad to see you.", "Information", Messages.getInformationIcon());
        }
    }
}
