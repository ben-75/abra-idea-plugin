package org.abra.interpreter.debugger;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import org.abra.language.psi.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AbraToJavaMapper {

    private final Project project;
    private Map<String,JavaBreakpointInfo> breakpointInfos = new HashMap<>();

    public AbraToJavaMapper(Project project) {
        this.project = project;
        PsiFile[] allEvalContext = FilenameIndex.getFilesByName(project, "EvalContext.java", GlobalSearchScope.projectScope(project));
        PsiFile evalContextFile = allEvalContext[0];
        Visitor visitor = new Visitor();
        evalContextFile.accept(visitor);
        for(PsiMethod method:visitor.psiMethods){
            //System.out.println(method.getName());
            if(method.getName().startsWith("eval"))
                breakpointInfos.put(method.getName(),new JavaBreakpointInfo(method));
        }
    }
    public static class JavaBreakpointInfo {
            final PsiMethod method;
            final int lineNumber;
            final PsiElement evalFirstLine;

        public JavaBreakpointInfo(PsiMethod method) {
            this.method = method;
            PsiElement e = method.getBody().getFirstBodyElement();
            while(e instanceof PsiWhiteSpace && e!=null)e = e.getNextSibling();
            this.evalFirstLine = e;
            this.lineNumber = getLineNum(evalFirstLine)+1;
        }

        public PsiMethod getMethod() {
            return method;
        }

        public int getLineNumber() {
            return lineNumber;
        }

        public PsiElement getEvalFirstLine() {
            return evalFirstLine;
        }
    }

    public static int getLineNum(PsiElement element){
        PsiFile containingFile = element.getContainingFile();
        Project project = containingFile.getProject();
        PsiDocumentManager psiDocumentManager = PsiDocumentManager.getInstance(project);
        Document document = psiDocumentManager.getDocument(containingFile);
        int textOffset = element.getTextOffset();
        return document.getLineNumber(textOffset);
    }

    public JavaBreakpointInfo getEvalMethod(PsiElement abraElement){
        if(abraElement instanceof AbraPostfixExpr){
            return getEvalMethod(abraElement.getFirstChild());
        }
        if(abraElement instanceof AbraAssignExpr){
            return breakpointInfos.get("evalAssign");
        }
        if(abraElement instanceof AbraConcatExpr){
            return breakpointInfos.get("evalConcat");
        }
        if(abraElement instanceof AbraLutExpr){
            return breakpointInfos.get("evalLutLookup");
        }
        if(abraElement instanceof AbraMergeExpr){
            return breakpointInfos.get("evalMerge");
        }
        if(abraElement instanceof AbraSliceExpr){
            return breakpointInfos.get("evalSlice");
        }
        if(abraElement instanceof AbraStateExpr){
            return breakpointInfos.get("evalState");
        }
        if(abraElement instanceof AbraFuncExpr){
            return breakpointInfos.get("evalFuncCall");
        }
        if(abraElement instanceof AbraInteger){
            return breakpointInfos.get("evalVector");
        }
        if(abraElement instanceof AbraReturnExpr){
            return getEvalMethod(((AbraReturnExpr)abraElement).getMergeExpr());
        }
        if(abraElement!=null)
           System.out.println("No eval method for "+abraElement.getText()+"   "+abraElement.getClass());
        else
            System.out.println("No eval method for null");
        return null;
    }
    private class Visitor extends PsiRecursiveElementVisitor {

        private List<PsiMethod> psiMethods = new ArrayList<PsiMethod>();

        @Override
        public void visitElement(PsiElement element) {

            if (element instanceof PsiMethod) {
                psiMethods.add((PsiMethod) element);
            }

            super.visitElement(element);
        }

        public List<PsiMethod> getPsiMethods() {
            return psiMethods;
        }
    }
}
