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

    public static AbraFieldName createAbraFieldName(Project project, String name) {
        final AbraFile file = createFile(project, "type t{"+name+" [1]}");
        return ((AbraTypeStmt) file.getFirstChild()).getFieldSpecList().get(0).getFieldName();
    }

    public static AbraFuncName createAbraFuncName(Project project, String name) {
        final AbraFile file = createFile(project, "func [1] "+name+"(param [1])=param");
        return ((AbraFuncStmt) file.getFirstChild()).getFuncSignature().getFuncName();
    }

    public static AbraParamName createAbraParamName(Project project, String name) {
        final AbraFile file = createFile(project, "func [1] f("+name+" [1])=param");
        return ((AbraFuncStmt) file.getFirstChild()).getFuncSignature().getFuncParameterList().get(0).getParamName();
    }

    public static AbraVarName createAbraVarName(Project project, String name) {
        final AbraFile file = createFile(project, "func [1] f(param [1])={"+name+"=param\n return a}");
        return ((AbraFuncStmt) file.getFirstChild()).getFuncBody().getAssignExprList().get(0).getVarName();
    }

    public static AbraLutName createAbraLutName(Project project, String name) {
        final AbraFile file = createFile(project, "lut "+name+"[0,0=0]");
        return ((AbraLutStmt) file.getFirstChild()).getLutName();
    }

    public static AbraTemplateName createAbraTemplateName(Project project, String name) {
        final AbraFile file = createFile(project, "template "+name+"<T> func [1] f<T>(c [T]) = {return 1}");
        return ((AbraTemplateStmt) file.getFirstChild()).getTemplateName();
    }

    public static AbraPlaceHolderTypeName createAbraPlaceHolderName(Project project, String name) {
        final AbraFile file = createFile(project, "template t<"+name+"> func [1] f<T>(c [T]) = {return 1}");
        return ((AbraTemplateStmt) file.getFirstChild()).getPlaceHolderTypeNameList().get(0);
    }

    //====================================================================
    //====================== AbraReferences ==============================
    //====================================================================


    public static AbraFuncNameRef createAbraFunctionReference(Project project, String name) {
        final AbraFile file = createFile(project, "func [1] f(p [1])={return "+name+"(1)}");
        return ((AbraFuncStmt)file.getFirstChild()).getFuncBody().getReturnExpr().getCondExpr().getMergeExprList().get(0).getConcatExprList().get(0).getPostfixExprList().get(0).getFuncExpr().getFuncNameRef();
    }

    public static AbraFuncNameRef createAbraFunctionReference(Project project, String name, AbraFile originalFile) {
        StringBuilder sb = new StringBuilder();
        if(originalFile!=null){
            sb.append("import ").append(originalFile.getImportableFilePath()).append("\n");
        }
        final AbraFile file = createFile(project, sb.toString()+"func [1] f(p [1])={return "+name+"(1)}");
        return ((AbraFuncStmt)file.getFirstChild().getNextSibling().getNextSibling()).getFuncBody().getReturnExpr().getCondExpr().getMergeExprList().get(0).getConcatExprList().get(0).getPostfixExprList().get(0).getFuncExpr().getFuncNameRef();
    }

    public static AbraTypeNameRef createAbraTypeNameRef(Project project, String name) {
        final AbraFile file = createFile(project, "use f<"+name+">");
        return ((AbraUseStmt)file.getFirstChild()).getTypeInstantiationList().get(0).getTypeNameRefList().get(0);
    }

    public static AbraParamOrVarNameRef createAbraVarOrParamNameRef(Project project, String name) {
        final AbraFile file = createFile(project, "func [1] f(p [1])={a="+name+"[1..3]\nreturn a}");
        return ((AbraFuncStmt)file.getFirstChild()).getFuncBody()
                .getAssignExprList().get(0).getCondExpr().getMergeExprList().get(0).getConcatExprList().get(0).getPostfixExprList().get(0).getSliceExpr().getParamOrVarNameRef();
    }

    public static AbraLutOrParamOrVarNameRef createAbraLutOrParamOrVarRef(Project project, String name) {
        final AbraFile file = createFile(project, "func [1] f(p [1])={a="+name+"[1]\n return a}");
        return ((AbraFuncStmt)file.getFirstChild()).getFuncBody()
                .getAssignExprList().get(0).getCondExpr().getMergeExprList().get(0).getConcatExprList().get(0).getPostfixExprList().get(0).getLutOrSliceExpr().getLutOrParamOrVarNameRef();
    }


    public static AbraTemplateNameRef createAbraTemplateNameRef(Project project, String newElementName) {
        final AbraFile file = createFile(project, "use "+newElementName+"<Tryte>");
        return ((AbraUseStmt)file.getFirstChild()).getTemplateNameRef();
    }

    public static AbraTypeOrPlaceHolderNameRef createAbraTypeOrPlaceHolderNameRef(Project project, String name) {
        final AbraFile file = createFile(project, "template incFunc<T> func [1] inc<"+name+"> (a[1]) = {return 1}");
        return ((AbraTemplateStmt)file.getFirstChild()).getFuncStmtList().get(0).getFuncSignature().getConstExprList().get(0).getConstTermList().get(0).getConstFactorList().get(0).getTypeOrPlaceHolderNameRef();
    }

    private static AbraFile createFile(Project project, String text) {
        String name = "dummy.abra";
        return (AbraFile) PsiFileFactory.getInstance(project).
                createFileFromText(name, AbraFileType.INSTANCE, text);
    }

    public static AbraConstExpr createAbraConstExpr(Project project, String s) {
        final AbraFile file = createFile(project, "func [1] f(a["+s+"])={a}");
        try{
            return ((AbraFuncStmt)file.getFirstChild()).getFuncSignature().getFuncParameterList().get(0).getTypeSize().getConstExpr();
        }catch (ClassCastException e){
            throw new RuntimeException("Invalid syntax:"+s, e);
        }
    }

    public static AbraLutNameRef createAbraLutNameRef(Project project, String name) {
        final AbraFile file = createFile(project, "func [1] f(a[1])={b="+name+"[1,1]\nreturn b}");
        return ((AbraFuncStmt)file.getFirstChild()).getFuncBody().getAssignExprList().get(0).getCondExpr().getMergeExprList().get(0).getConcatExprList().get(0).getPostfixExprList().get(0).getLutExpr().getLutNameRef();
    }

    public static AbraFieldNameRef createAbraFieldNameReference(Project project, String name) {
        final AbraFile file = createFile(project, "func [1] f(a [1])=f(p."+name+")");
        return ((AbraFuncStmt)file.getFirstChild()).getFuncBody().getCondExpr().getMergeExprList().get(0).getConcatExprList().get(0).getPostfixExprList().get(0)
                .getFuncExpr().getMergeExprList().get(0).getConcatExprList().get(0).getPostfixExprList().get(0).
                        getSliceExpr().getFieldNameRefList().get(0);
    }

    public static AbraPathName createAbraPathName(Project project, String name) {
        final AbraFile file = createFile(project, "import Abra/"+name);
        return ((AbraImportStmt)file.getFirstChild()).getPathNameList().get(1);
    }



}