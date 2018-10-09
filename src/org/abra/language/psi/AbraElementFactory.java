package org.abra.language.psi;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import org.abra.language.AbraFileType;

/**
 * Factory for AbraPsi elements
 */
public class AbraElementFactory {

    //====================================================================
    //====================== AbraNames ===================================
    //====================================================================

    public static AbraTypeName createAbraTypeName(Project project, String name) {
        final AbraFile file = createFile(project, "type "+name+" [1]");
        return ((AbraTypeStmt) file.getFirstChild()).getTypeName();
    }

    public static AbraFuncName createAbraFuncName(Project project, String name) {
        final AbraFile file = createFile(project, "func "+name+"(param [1])=param");
        return ((AbraFuncStmt) file.getFirstChild()).getFuncSignature().getFuncName();
    }

    public static AbraParamName createAbraParamName(Project project, String name) {
        final AbraFile file = createFile(project, "func f("+name+" [1])=param");
        return ((AbraFuncStmt) file.getFirstChild()).getFuncSignature().getFuncParameterList().get(0).getParamName();
    }

    public static AbraVarName createAbraVarName(Project project, String name) {
        final AbraFile file = createFile(project, "func f(param [1])={"+name+"=param\na}");
        return ((AbraFuncStmt) file.getFirstChild()).getFuncBody().getAssignExprList().get(0).getVarName();
    }

    public static AbraLutName createAbraLutName(Project project, String name) {
        final AbraFile file = createFile(project, "lut "+name+"[0,0=0]");
        return ((AbraLutStmt) file.getFirstChild()).getLutName();
    }

    public static AbraTemplateName createAbraTemplateName(Project project, String name) {
        final AbraFile file = createFile(project, "template "+name+"<T> f<T>(c [T]) = {1}");
        return ((AbraTemplateStmt) file.getFirstChild()).getTemplateName();
    }

    public static AbraPlaceHolderTypeName createAbraPlaceHolderName(Project project, String name) {
        final AbraFile file = createFile(project, "template t<"+name+"> f<T>(c [T]) = {1}");
        return ((AbraTemplateStmt) file.getFirstChild()).getPlaceHolderTypeNameList().get(0);
    }

    //====================================================================
    //====================== AbraReferences ==============================
    //====================================================================


    public static AbraFuncNameRef createAbraFunctionReference(Project project, String name) {
        final AbraFile file = createFile(project, "func f(p [1])={"+name+"(1)}");
        return ((AbraFuncStmt)file.getFirstChild()).getFuncBody().getReturnExpr().getMergeExpr().getConcatExprList().get(0).getPostfixExprList().get(0).getFuncExpr().getFuncNameRef();
    }

    public static AbraFuncNameRef createAbraFunctionReference(Project project, String name, AbraFile originalFile) {
        StringBuilder sb = new StringBuilder();
        if(originalFile!=null){
            sb.append("import ").append(originalFile.getImportableFilePath()).append("\n");
        }


        final AbraFile file = createFile(project, sb.toString()+"func f(p [1])={return "+name+"(1)}");
        return ((AbraFuncStmt)file.getFirstChild().getNextSibling().getNextSibling()).getFuncBody().getReturnExpr().getMergeExpr().getConcatExprList().get(0).getPostfixExprList().get(0).getFuncExpr().getFuncNameRef();
    }

    public static AbraTypeNameRef createAbraTypeNameRef(Project project, String name) {
        final AbraFile file = createFile(project, "func f<"+name+">");
        return ((AbraUseStmt)file.getFirstChild()).getTypeInstantiationList().get(0).getTypeNameRefList().get(0);
    }

    public static AbraParamOrVarNameRef createAbraVarOrParamNameRef(Project project, String name) {
        final AbraFile file = createFile(project, "func f(p [1])={a="+name+"[1..3]\na}");
        return ((AbraFuncStmt)file.getFirstChild()).getFuncBody()
                .getAssignExprList().get(0).getMergeExpr().getConcatExprList().get(0).getPostfixExprList().get(0).getSliceExpr().getParamOrVarNameRef();
    }

    public static AbraLutOrParamOrVarNameRef createAbraLutOrParamOrVarRef(Project project, String name) {
        final AbraFile file = createFile(project, "func f(p [1])={a="+name+"[1]\na}");
        return ((AbraFuncStmt)file.getFirstChild()).getFuncBody()
                .getAssignExprList().get(0).getMergeExpr().getConcatExprList().get(0).getPostfixExprList().get(0).getLutOrSliceExpr().getLutOrParamOrVarNameRef();
    }


    public static AbraTemplateNameRef createAbraTemplateNameRef(Project project, String newElementName) {
        final AbraFile file = createFile(project, "type "+newElementName+"<Tryte>");
        return ((AbraUseStmt)file.getFirstChild()).getTemplateNameRef();
    }

    public static AbraTypeOrPlaceHolderNameRef createAbraTypeOrPlaceHolderNameRef(Project project, String name) {
        final AbraFile file = createFile(project, "template incFunc<T> inc<"+name+"> (a[1]) = {return 1}");
        return ((AbraTemplateStmt)file.getFirstChild()).getFuncStmtList().get(0).getFuncSignature().getConstExpr().getConstTermList().get(0).getConstFactorList().get(0).getTypeOrPlaceHolderNameRef();
    }

    private static AbraFile createFile(Project project, String text) {
        String name = "dummy.abra";
        return (AbraFile) PsiFileFactory.getInstance(project).
                createFileFromText(name, AbraFileType.INSTANCE, text);
    }

    public static AbraConstExpr createAbraConstExpr(Project project, String s) {
        final AbraFile file = createFile(project, "func f(a["+s+"])={a}");
        try{
            return ((AbraFuncStmt)file.getFirstChild()).getFuncSignature().getFuncParameterList().get(0).getTypeSize().getConstExpr();
        }catch (ClassCastException e){
            throw new RuntimeException("Invalid syntax:"+s, e);
        }
    }

    public static AbraLutNameRef createAbraLutNameRef(Project project, String name) {
        final AbraFile file = createFile(project, "func f(a[1])={b="+name+"[1,1]\nb}");
        return ((AbraFuncStmt)file.getFirstChild()).getFuncBody().getAssignExprList().get(0).getMergeExpr().getConcatExprList().get(0).getPostfixExprList().get(0).getLutExpr().getLutNameRef();
    }
}