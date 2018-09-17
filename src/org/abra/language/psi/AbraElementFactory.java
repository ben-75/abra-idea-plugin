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
        final AbraFile file = createFile(project, "type "+name+" [1];");
        return ((AbraTypeStmt) file.getFirstChild()).getTypeName();
    }

    public static AbraFuncName createAbraFuncName(Project project, String name) {
        final AbraFile file = createFile(project, "func "+name+"(param [1])=param,param;");
        return ((AbraFuncStmt) file.getFirstChild()).getFuncDefinition().getFuncName();
    }

    public static AbraParamName createAbraParamName(Project project, String name) {
        final AbraFile file = createFile(project, "f("+name+" [1])=param,param;");
        return ((AbraFuncStmt) file.getFirstChild()).getFuncDefinition().getFuncParameterList().get(0).getParamName();
    }

    public static AbraVarName createAbraVarName(Project project, String name) {
        final AbraFile file = createFile(project, "func f(param [1])={"+name+"=param;a;};");
        return ((AbraFuncStmt) file.getFirstChild()).getFuncDefinition().getFuncBody().getAssignExprList().get(0).getVarName();
    }

    public static AbraLutName createAbraLutName(Project project, String name) {
        final AbraFile file = createFile(project, name+"[0,0=0;];");
        return ((AbraLutStmt) file.getFirstChild()).getLutName();
    }

    public static AbraTemplateName createAbraTemplateName(Project project, String name) {
        final AbraFile file = createFile(project, "template "+name+"<T> f<T>(c [T]) = {1;};");
        return ((AbraTemplateStmt) file.getFirstChild()).getTemplateName();
    }

    public static AbraPlaceHolderName createAbraPlaceHolderName(Project project, String name) {
        final AbraFile file = createFile(project, "template t<"+name+"> f<T>(c [T]) = {1;};");
        return ((AbraTemplateStmt) file.getFirstChild()).getPlaceHolderNameList().get(0);
    }

    //====================================================================
    //====================== AbraReferences ==============================
    //====================================================================


    public static AbraFuncNameRef createAbraFunctionReference(Project project, String name) {
        final AbraFile file = createFile(project, "f(p [1])={"+name+"(1);};");
        return ((AbraFuncStmt)file.getFirstChild()).getFuncDefinition().getFuncBody().getMergeExpr().getConcatExprList().get(0).getFuncExprList().get(0).getFuncNameRef();
    }


    public static AbraTypeNameRef createAbraTypeNameRef(Project project, String name) {
        final AbraFile file = createFile(project, "f<"+name+">;");
        return ((AbraUseStmt)file.getFirstChild()).getTypeNameRefList().get(0);
    }

    public static AbraParamOrVarNameRef createAbraVarOrParamNameRef(Project project, String name) {
        final AbraFile file = createFile(project, "f(p [1])={a="+name+";a;};");
        return ((AbraFuncStmt)file.getFirstChild()).getFuncDefinition().getFuncBody()
                .getAssignExprList().get(0).getMergeExpr().getConcatExprList().get(0).getSliceExprList().get(0).getParamOrVarNameRef();
    }

    public static AbraLutOrParamOrVarNameRef createAbraLutOrParamOrVarRef(Project project, String name) {
        final AbraFile file = createFile(project, "f(p [1])={a="+name+"[1];a;};");
        return ((AbraFuncStmt)file.getFirstChild()).getFuncDefinition().getFuncBody()
                .getAssignExprList().get(0).getMergeExpr().getConcatExprList().get(0).getLutExprList().get(0).getLutOrParamOrVarNameRef();
    }

    private static AbraFile createFile(Project project, String text) {
        String name = "dummy.abra";
        return (AbraFile) PsiFileFactory.getInstance(project).
                createFileFromText(name, AbraFileType.INSTANCE, text);
    }
}