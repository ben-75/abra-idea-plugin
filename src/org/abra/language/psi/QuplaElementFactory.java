package org.abra.language.psi;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import org.abra.language.QuplaFileType;

/**
 * Factory for AbraPsi elements
 */
public class QuplaElementFactory {

    //====================================================================
    //====================== AbraNames ===================================
    //====================================================================

    public static QuplaTypeName createAbraTypeName(Project project, String name) {
        final QuplaFile file = createFile(project, "type "+name+" [1]");
        return ((QuplaTypeStmt) file.getFirstChild()).getTypeName();
    }

    public static QuplaFieldName createAbraFieldName(Project project, String name) {
        final QuplaFile file = createFile(project, "type t{"+name+" [1]}");
        return ((QuplaTypeStmt) file.getFirstChild()).getFieldSpecList().get(0).getFieldName();
    }

    public static QuplaFuncName createAbraFuncName(Project project, String name) {
        final QuplaFile file = createFile(project, "func Trit "+name+"(Trit param){return a}");
        return ((QuplaFuncStmt) file.getFirstChild()).getFuncSignature().getFuncName();
    }

    public static QuplaParamName createAbraParamName(Project project, String name) {
        final QuplaFile file = createFile(project, "func Trit f(Trit "+name+"){return param}");
        return ((QuplaFuncStmt) file.getFirstChild()).getFuncSignature().getFuncParameterList().get(0).getParamName();
    }

    public static QuplaVarName createAbraVarName(Project project, String name) {
        final QuplaFile file = createFile(project, "func Trit f(Trit param){"+name+"=param\n return a}");
        return ((QuplaFuncStmt) file.getFirstChild()).getFuncBody().getAssignExprList().get(0).getVarName();
    }

    public static QuplaLutName createAbraLutName(Project project, String name) {
        final QuplaFile file = createFile(project, "lut "+name+"{0,0=0}");
        return ((QuplaLutStmt) file.getFirstChild()).getLutName();
    }

    public static QuplaTemplateName createAbraTemplateName(Project project, String name) {
        final QuplaFile file = createFile(project, "template "+name+"<T> func Trit f<T>(Trit c) {return 1}");
        return ((QuplaTemplateStmt) file.getFirstChild()).getTemplateName();
    }

    public static QuplaPlaceHolderTypeName createAbraPlaceHolderName(Project project, String name) {
        final QuplaFile file = createFile(project, "template t<"+name+"> func Trit f<T>(Trit c) {return 1}");
        return ((QuplaTemplateStmt) file.getFirstChild()).getPlaceHolderTypeNameList().get(0);
    }

    //====================================================================
    //====================== AbraReferences ==============================
    //====================================================================


    public static QuplaFuncNameRef createAbraFunctionReference(Project project, String name) {
        final QuplaFile file = createFile(project, "func Trit f(Trit p){return "+name+"(1)}");
        return ((QuplaFuncStmt)file.getFirstChild()).getFuncBody().getReturnExpr().getCondExpr().getMergeExprList().get(0).getConcatExprList().get(0).getPostfixExprList().get(0).getFuncExpr().getFuncNameRef();
    }

    public static QuplaTypeNameRef createAbraTypeNameRef(Project project, String name) {
        final QuplaFile file = createFile(project, "use f<"+name+">");
        return ((QuplaUseStmt)file.getFirstChild()).getTypeInstantiationList().get(0).getTypeNameRefList().get(0);
    }

    public static QuplaParamOrVarNameRef createAbraVarOrParamNameRef(Project project, String name) {
        final QuplaFile file = createFile(project, "func Trit f(Trit p){a="+name+"[1..3]\nreturn a}");
        return ((QuplaFuncStmt)file.getFirstChild()).getFuncBody()
                .getAssignExprList().get(0).getCondExpr().getMergeExprList().get(0).getConcatExprList().get(0).getPostfixExprList().get(0).getSliceExpr().getParamOrVarNameRef();
    }

    public static QuplaLutOrParamOrVarNameRef createAbraLutOrParamOrVarRef(Project project, String name) {
        final QuplaFile file = createFile(project, "func Trit f(Trit p){a="+name+"[1]\n return a}");
        return ((QuplaFuncStmt)file.getFirstChild()).getFuncBody()
                .getAssignExprList().get(0).getCondExpr().getMergeExprList().get(0).getConcatExprList().get(0).getPostfixExprList().get(0).getLutOrSliceExpr().getLutOrParamOrVarNameRef();
    }


    public static QuplaTemplateNameRef createAbraTemplateNameRef(Project project, String newElementName) {
        final QuplaFile file = createFile(project, "use "+newElementName+"<Tryte>");
        return ((QuplaUseStmt)file.getFirstChild()).getTemplateNameRef();
    }

    public static QuplaTypeOrPlaceHolderNameRef createAbraTypeOrPlaceHolderNameRef(Project project, String name) {
        final QuplaFile file = createFile(project, "template incFunc<T> {func "+name+" inc<"+name+"> (Trit a) {return 1}}");
        return ((QuplaTemplateStmt)file.getFirstChild()).getFuncStmtList().get(0).getFuncSignature().getTypeOrPlaceHolderNameRefList().get(0);
    }

    private static QuplaFile createFile(Project project, String text) {
        String name = "dummy.abra";
        return (QuplaFile) PsiFileFactory.getInstance(project).
                createFileFromText(name, QuplaFileType.INSTANCE, text);
    }


    public static QuplaLutNameRef createAbraLutNameRef(Project project, String name) {
        final QuplaFile file = createFile(project, "func Trit f(Trit a){b="+name+"[1,1]\nreturn b}");
        return ((QuplaFuncStmt)file.getFirstChild()).getFuncBody().getAssignExprList().get(0).getCondExpr().getMergeExprList().get(0).getConcatExprList().get(0).getPostfixExprList().get(0).getLutExpr().getLutNameRef();
    }

    public static QuplaFieldNameRef createAbraFieldNameReference(Project project, String name) {
        final QuplaFile file = createFile(project, "func Trit f(Trit a){return f(p."+name+")}");
        return ((QuplaFuncStmt)file.getFirstChild()).getFuncBody().getReturnExpr().getCondExpr().getMergeExprList().get(0)
                .getConcatExprList().get(0).getPostfixExprList().get(0).
                        getSliceExpr().getFieldNameRefList().get(0);
    }

    public static QuplaModuleName createAbraModuleName(Project project, String name) {
        final QuplaFile file = createFile(project, "import "+name);
        return ((QuplaImportStmt)file.getFirstChild()).getModuleName();
    }



}