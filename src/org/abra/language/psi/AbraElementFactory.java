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
        final AbraFile file = createFile(project, "func Trit "+name+"(Trit param){return a}");
        return ((AbraFuncStmt) file.getFirstChild()).getFuncSignature().getFuncName();
    }

    public static AbraParamName createAbraParamName(Project project, String name) {
        final AbraFile file = createFile(project, "func Trit f(Trit "+name+"){return param}");
        return ((AbraFuncStmt) file.getFirstChild()).getFuncSignature().getFuncParameterList().get(0).getParamName();
    }

    public static AbraVarName createAbraVarName(Project project, String name) {
        final AbraFile file = createFile(project, "func Trit f(Trit param){"+name+"=param\n return a}");
        return ((AbraFuncStmt) file.getFirstChild()).getFuncBody().getAssignExprList().get(0).getVarName();
    }

    public static AbraLutName createAbraLutName(Project project, String name) {
        final AbraFile file = createFile(project, "lut "+name+"{0,0=0}");
        return ((AbraLutStmt) file.getFirstChild()).getLutName();
    }

    public static AbraTemplateName createAbraTemplateName(Project project, String name) {
        final AbraFile file = createFile(project, "template "+name+"<T> func Trit f<T>(Trit c) {return 1}");
        return ((AbraTemplateStmt) file.getFirstChild()).getTemplateName();
    }

    public static AbraPlaceHolderTypeName createAbraPlaceHolderName(Project project, String name) {
        final AbraFile file = createFile(project, "template t<"+name+"> func Trit f<T>(Trit c) {return 1}");
        return ((AbraTemplateStmt) file.getFirstChild()).getPlaceHolderTypeNameList().get(0);
    }

    //====================================================================
    //====================== AbraReferences ==============================
    //====================================================================


    public static AbraFuncNameRef createAbraFunctionReference(Project project, String name) {
        final AbraFile file = createFile(project, "func Trit f(Trit p){return "+name+"(1)}");
        return ((AbraFuncStmt)file.getFirstChild()).getFuncBody().getReturnExpr().getCondExpr().getMergeExprList().get(0).getConcatExprList().get(0).getPostfixExprList().get(0).getFuncExpr().getFuncNameRef();
    }

    public static AbraTypeNameRef createAbraTypeNameRef(Project project, String name) {
        final AbraFile file = createFile(project, "use f<"+name+">");
        return ((AbraUseStmt)file.getFirstChild()).getTypeInstantiationList().get(0).getTypeNameRefList().get(0);
    }

    public static AbraParamOrVarNameRef createAbraVarOrParamNameRef(Project project, String name) {
        final AbraFile file = createFile(project, "func Trit f(Trit p){a="+name+"[1..3]\nreturn a}");
        return ((AbraFuncStmt)file.getFirstChild()).getFuncBody()
                .getAssignExprList().get(0).getCondExpr().getMergeExprList().get(0).getConcatExprList().get(0).getPostfixExprList().get(0).getSliceExpr().getParamOrVarNameRef();
    }

    public static AbraLutOrParamOrVarNameRef createAbraLutOrParamOrVarRef(Project project, String name) {
        final AbraFile file = createFile(project, "func Trit f(Trit p){a="+name+"[1]\n return a}");
        return ((AbraFuncStmt)file.getFirstChild()).getFuncBody()
                .getAssignExprList().get(0).getCondExpr().getMergeExprList().get(0).getConcatExprList().get(0).getPostfixExprList().get(0).getLutOrSliceExpr().getLutOrParamOrVarNameRef();
    }


    public static AbraTemplateNameRef createAbraTemplateNameRef(Project project, String newElementName) {
        final AbraFile file = createFile(project, "use "+newElementName+"<Tryte>");
        return ((AbraUseStmt)file.getFirstChild()).getTemplateNameRef();
    }

    public static AbraTypeOrPlaceHolderNameRef createAbraTypeOrPlaceHolderNameRef(Project project, String name) {
        final AbraFile file = createFile(project, "template incFunc<T> {func "+name+" inc<"+name+"> (Trit a) {return 1}}");
        return ((AbraTemplateStmt)file.getFirstChild()).getFuncStmtList().get(0).getFuncSignature().getTypeOrPlaceHolderNameRefList().get(0);
    }

    private static AbraFile createFile(Project project, String text) {
        String name = "dummy.abra";
        return (AbraFile) PsiFileFactory.getInstance(project).
                createFileFromText(name, AbraFileType.INSTANCE, text);
    }


    public static AbraLutNameRef createAbraLutNameRef(Project project, String name) {
        final AbraFile file = createFile(project, "func Trit f(Trit a){b="+name+"[1,1]\nreturn b}");
        return ((AbraFuncStmt)file.getFirstChild()).getFuncBody().getAssignExprList().get(0).getCondExpr().getMergeExprList().get(0).getConcatExprList().get(0).getPostfixExprList().get(0).getLutExpr().getLutNameRef();
    }

    public static AbraFieldNameRef createAbraFieldNameReference(Project project, String name) {
        final AbraFile file = createFile(project, "func Trit f(Trit a){return f(p."+name+")}");
        return ((AbraFuncStmt)file.getFirstChild()).getFuncBody().getReturnExpr().getCondExpr().getMergeExprList().get(0)
                .getConcatExprList().get(0).getPostfixExprList().get(0).
                        getSliceExpr().getFieldNameRefList().get(0);
    }

    public static AbraPathName createAbraPathName(Project project, String name) {
        final AbraFile file = createFile(project, "import "+name);
        return ((AbraImportStmt)file.getFirstChild()).getPathName();
    }



}