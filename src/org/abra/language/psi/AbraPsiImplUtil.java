package org.abra.language.psi;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.tree.TokenSet;
import com.intellij.util.indexing.FileBasedIndex;
import org.abra.language.AbraFileType;
import org.abra.language.AbraIcons;
import org.abra.language.UnresolvableTokenException;
import org.abra.language.psi.impl.*;
import org.abra.utils.TRIT;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class AbraPsiImplUtil {


    //====================================================================
    //====================== AbraTypeStmt ================================
    //====================================================================


    @NotNull
    public static ItemPresentation getPresentation(AbraTypeStmt element) {
        return new ItemPresentation() {
            @NotNull
            @Override
            public String getPresentableText() {
                return element.getTypeName().getText();
            }

            @NotNull
            @Override
            public String getLocationString() {
                try {
                    int sz = element.getResolvedSize();
                    String txt = null;
                    if(element.getTypeSize()==null){
                        txt = "";
                    }else{
                        txt = element.getTypeSize().getText();
                    }
                    if(txt.startsWith("["))txt = txt.substring(1);
                    if(txt.endsWith("]"))txt = txt.substring(0,txt.length()-1);
                    return "["+(sz<=0?txt:sz)+"]";
                }catch (UnresolvableTokenException e){
                    return "[?]";
                }
            }

            @NotNull
            @Override
            public Icon getIcon(boolean unused) {
                return AbraIcons.TYPE;
            }
        };
    }

    public static String getName(AbraTypeStmt element) {
        return element.getTypeName().getText();
    }

    public static PsiElement setName(AbraTypeStmt element, String newName) {
        ASTNode globalIdNode = element.getTypeName().getNode();
        if (globalIdNode != null) {
            AbraTypeName typeName = AbraElementFactory.createAbraTypeName(element.getProject(), newName);
            ASTNode newKeyNode = typeName.getFirstChild().getNode();
            element.getNode().replaceChild(globalIdNode, newKeyNode);
        }
        return element;
    }

    public static PsiElement getNameIdentifier(AbraTypeStmt element) {
        return element.getTypeName();
    }

    public static int getResolvedSize(AbraTypeStmt element){
        if(element.getFieldSpecList().size()>0) {
            int resolvedSize = 0;
            for(AbraFieldSpec fieldSpec:element.getFieldSpecList()){
                resolvedSize = resolvedSize + fieldSpec.getResolvedSize();
            }
            return resolvedSize;
        }
        return element.getTypeSize().getResolvedSize();
    }

    public static String getName(AbraTypeName element) {
        return element.getText();
    }

    public static PsiElement setName(AbraTypeName element, String newName) {
        ASTNode globalIdNode = element.getNode().getFirstChildNode();
        if (globalIdNode != null) {
            AbraTypeName typeName = AbraElementFactory.createAbraTypeName(element.getProject(), newName);
            ASTNode newKeyNode = typeName.getFirstChild().getNode();
            element.getNode().replaceChild(globalIdNode, newKeyNode);
        }
        return element;
    }

    public static PsiElement getNameIdentifier(AbraTypeName element) {
        return element;
    }

    public static int getResolvedSize(AbraTypeName element){
        return ((AbraTypeStmt)element.getParent()).getResolvedSize();
    }
    //====================================================================
    //====================== AbraLutStmt =================================
    //====================================================================

    @NotNull
    public static ItemPresentation getPresentation(AbraLutStmt element) {
        return new ItemPresentation() {
            @NotNull
            @Override
            public String getPresentableText() {
                return element.getLutName().getText();
            }

            @NotNull
            @Override
            public String getLocationString() {
                return "("+element.getLutEntryList().get(0).getInputLength()+") -> "+
                        element.getLutEntryList().get(0).getOutputLength();
            }

            @NotNull
            @Override
            public Icon getIcon(boolean unused) {
                return AbraIcons.LUT;
            }
        };
    }

    public static String getName(AbraLutStmt element) {
        return element.getLutName().getText();
    }

    public static PsiElement setName(AbraLutStmt element, String newName) {
        ASTNode globalIdNode = element.getLutName().getNode();
        if (globalIdNode != null) {
            AbraLutName lutName = AbraElementFactory.createAbraLutName(element.getProject(), newName);
            ASTNode newKeyNode = lutName.getFirstChild().getNode();
            element.getNode().replaceChild(globalIdNode, newKeyNode);
        }
        return element;
    }

    public static PsiElement getNameIdentifier(AbraLutStmt element) {
        return element.getLutName();
    }

    public static String getName(AbraLutName element) {
        return element.getText();
    }

    public static PsiElement setName(AbraLutName element, String newName) {
        ASTNode globalIdNode = element.getNode().getFirstChildNode();
        if (globalIdNode != null) {
            AbraLutName lutName = AbraElementFactory.createAbraLutName(element.getProject(), newName);
            ASTNode newKeyNode = lutName.getFirstChild().getNode();
            element.getNode().replaceChild(globalIdNode, newKeyNode);
        }
        return element;
    }

    public static PsiElement getNameIdentifier(AbraLutName element) {
        return element;
    }


    //====================================================================
    //====================== AbraFieldSpec ===============================
    //====================================================================

    @NotNull
    public static ItemPresentation getPresentation(AbraFieldSpec element) {
        return new ItemPresentation() {
            @NotNull
            @Override
            public String getPresentableText() {
                return element.getFieldName().getText();
            }

            @NotNull
            @Override
            public String getLocationString() {
                try{
                    int sz = element.getTypeSize().getResolvedSize();
                    String txt = sz<=0?element.getTypeSize().getText():sz+"";
                    if(txt.startsWith("["))txt = txt.substring(1);
                    if(txt.endsWith("]"))txt = txt.substring(0,txt.length()-1);
                    return "["+(sz<=0?txt:sz)+"]";
                }catch (UnresolvableTokenException e){
                    return "[?]";
                }
            }

            @NotNull
            @Override
            public Icon getIcon(boolean unused) {
                return AbraIcons.VECTOR;
            }
        };
    }

    public static int getResolvedSize(AbraFieldSpec fieldSpec){
        return fieldSpec.getTypeSize().getResolvedSize();
    }

    //====================================================================
    //====================== AbraFuncStmt ================================
    //====================================================================

    @NotNull
    public static ItemPresentation getPresentation(AbraFuncStmt element) {
        return new ItemPresentation() {
            @NotNull
            @Override
            public String getPresentableText() {
                return element.getFuncSignature().getFuncName().getText();
            }

            @NotNull
            @Override
            public String getLocationString() {
                StringBuilder sb = new StringBuilder("( ");
                int i=0;
                for(AbraFuncParameter p:element.getFuncSignature().getFuncParameterList()){
                    try{
                        int sz = p.getTypeSize().getConstExpr().getResolvedSize();
                        sb.append(sz<=0?p.getTypeSize().getConstExpr().getText():sz);
                    }catch (UnresolvableTokenException e){
                        sb.append("[?]");
                    }
                    i++;
                    if(i<element.getFuncSignature().getFuncParameterList().size())sb.append(" , ");
                }
                sb.append(" ) -> ");
                if(element.getFuncSignature().getTypeSize()!=null){
                    try{
                        int sz = element.getFuncSignature().getTypeSize().getConstExpr().getResolvedSize();
                        sb.append(sz<=0?element.getFuncSignature().getTypeSize().getConstExpr().getText():sz);
                    }catch (UnresolvableTokenException e){
                        sb.append("[?]");
                    }
                }else{
                    sb.append("not specified !");
                }
                return sb.toString();
            }

            @NotNull
            @Override
            public Icon getIcon(boolean unused) {
                return AbraIcons.FUNCTION;
            }
        };
    }

    public static String getName(AbraFuncStmt element) {
        return element.getFuncSignature().getFuncName().getText();
    }

    public static PsiElement setName(AbraFuncStmt element, String newName) {
        ASTNode globalIdNode = element.getFuncSignature().getFuncName().getNode();
        if (globalIdNode != null) {
            AbraFuncName funcName = AbraElementFactory.createAbraFuncName(element.getProject(), newName);
            ASTNode newKeyNode = funcName.getFirstChild().getNode();
            element.getNode().replaceChild(globalIdNode, newKeyNode);
        }
        return element;
    }

    public static PsiElement getNameIdentifier(AbraFuncStmt element) {
        return element.getFuncSignature().getFuncName();
    }

    public static String getName(AbraFuncName element) {
        return element.getText();
    }

    public static PsiElement setName(AbraFuncName element, String newName) {
        ASTNode globalIdNode = element.getNode().getFirstChildNode();
        if (globalIdNode != null) {
            AbraFuncName funcName = AbraElementFactory.createAbraFuncName(element.getProject(), newName);
            ASTNode newKeyNode = funcName.getFirstChild().getNode();
            element.getNode().replaceChild(globalIdNode, newKeyNode);
        }
        return element;
    }

    public static PsiElement getNameIdentifier(AbraFuncName element) {
        return element;
    }

    public static AbraDefinition getStatement(AbraFuncNameRef element){
        PsiElement abraDefinition = element;
        while(!(abraDefinition instanceof AbraDefinition))abraDefinition = abraDefinition.getParent();
        return (AbraDefinition) abraDefinition;
    }

    public static boolean isInTemplate(AbraFuncStmt funcStmt){
        return funcStmt.getParent() instanceof AbraTemplateStmt;
    }
    //====================================================================
    //====================== AbraTemplateStmt ============================
    //====================================================================

    @NotNull
    public static ItemPresentation getPresentation(AbraTemplateStmt element) {
        return new ItemPresentation() {
            @NotNull
            @Override
            public String getPresentableText() {
                StringBuilder sb = new StringBuilder("<");
                int i=0;
                for(PsiElement p:element.getPlaceHolderTypeNameList()){
                    i++;
                    sb.append(p.getText());
                    if(i<element.getPlaceHolderTypeNameList().size())sb.append(",");
                }
                sb.append(">");
                return element.getTemplateName().getText()+sb.toString();
            }

            @NotNull
            @Override
            public String getLocationString() {
                if(element.getFuncStmtList().size()==1) {
                    AbraFuncSignature sig = element.getFuncStmtList().get(0).getFuncSignature();
                    return sig.getFuncName().getText()+"<"+
                            String.join(",",sig.getConstExprList().stream().map(expr->expr.getText()).collect(Collectors.toList()))
                            +">" + " -> " + sig.getTypeSize().getText();
                }
                return "";
            }

            @NotNull
            @Override
            public Icon getIcon(boolean unused) {
                return AbraIcons.TEMPLATE;
            }
        };
    }

    public static String getName(AbraTemplateStmt element) {
        return element.getTemplateName().getText();
    }

    public static PsiElement setName(AbraTemplateStmt element, String newName) {
        ASTNode globalIdNode = element.getTemplateName().getNode();
        if (globalIdNode != null) {
            AbraTemplateName templateName = AbraElementFactory.createAbraTemplateName(element.getProject(), newName);
            ASTNode newKeyNode = templateName.getFirstChild().getNode();
            element.getNode().replaceChild(globalIdNode, newKeyNode);
        }
        return element;
    }

    public static PsiElement getNameIdentifier(AbraTemplateStmt element) {
        return element.getTemplateName();
    }


    public static String getName(AbraTemplateName element) {
        return element.getText();
    }

    public static PsiElement setName(AbraTemplateName element, String newName) {
        ASTNode globalIdNode = element.getNode().getFirstChildNode();
        if (globalIdNode != null) {
            AbraTemplateName templateName = AbraElementFactory.createAbraTemplateName(element.getProject(), newName);
            ASTNode newKeyNode = templateName.getFirstChild().getNode();
            element.getNode().replaceChild(globalIdNode, newKeyNode);
        }
        return element;
    }

    public static PsiElement getNameIdentifier(AbraTemplateName element) {
        return element;
    }

    //====================================================================
    //====================== AbraUseStmt =================================
    //====================================================================

    @NotNull
    public static ItemPresentation getPresentation(AbraUseStmt element) {
        return new ItemPresentation() {
            @NotNull
            @Override
            public String getPresentableText() {
                return element.getTemplateNameRef().getText();
            }

            @NotNull
            @Override
            public String getLocationString() {
                return "";
            }

            @NotNull
            @Override
            public Icon getIcon(boolean unused) {
                return AbraIcons.USE;
            }
        };
    }

    public static String getName(AbraUseStmt element) {
        return element.getTemplateNameRef().getText();
    }

    public static PsiElement setName(AbraUseStmt element, String newName) {
        ASTNode globalIdNode = element.getTemplateNameRef().getNode();
        if (globalIdNode != null) {
            AbraFuncName funcName = AbraElementFactory.createAbraFuncName(element.getProject(), newName);
            ASTNode newKeyNode = funcName.getFirstChild().getNode();
            element.getNode().replaceChild(globalIdNode, newKeyNode);
        }
        return element;
    }

    public static PsiElement getNameIdentifier(AbraUseStmt element) {
        return element.getTemplateNameRef();
    }

    public static Map<Integer, AbraTypeNameRef> getResolutionMap(AbraUseStmt element, int size){
        HashMap<Integer, AbraTypeNameRef> map = new HashMap<>();
        for(AbraTypeInstantiation typeInstantiation:element.getTypeInstantiationList()){
            AbraTypeName typeName = (AbraTypeName) typeInstantiation.getTypeNameRefList().get(0).getReference().resolve();
            if(((AbraTypeStmt)typeName.getParent()).getResolvedSize()==size) {
                for(int i=0;i<typeInstantiation.getTypeNameRefList().size();i++){
                    map.put(i, typeInstantiation.getTypeNameRefList().get(i));
                    i++;
                }
            }
        }
        return map;
    }

    public static Map<AbraPlaceHolderTypeName,AbraTypeNameRef> getTemplateContextMap(AbraUseStmt useStmt, int size){
        Map<Integer, AbraTypeNameRef> resolutionMap = getResolutionMap(useStmt, size);
        AbraTemplateName templateName = (AbraTemplateName) useStmt.getTemplateNameRef().getReference().resolve();
        HashMap<AbraPlaceHolderTypeName,AbraTypeNameRef> context = new HashMap();
        if(templateName==null)return context;
        AbraTemplateStmt templateStmt = (AbraTemplateStmt)templateName.getParent();
        int i=0;
        for(AbraPlaceHolderTypeName phn:templateStmt.getPlaceHolderTypeNameList()){
            context.put(phn,resolutionMap.get(i));
        }
        return context;
    }

    //====================================================================
    //====================== AbraUseStmt =================================
    //====================================================================

    @NotNull
    public static ItemPresentation getPresentation(AbraTypeInstantiation element) {
        return new ItemPresentation() {
            @NotNull
            @Override
            public String getPresentableText() {
                return ((AbraUseStmt)element.getParent()).getTemplateNameRef().getText()+"<"+element.getTypeNameRefList().get(0).getText()+">";
            }

            @NotNull
            @Override
            public String getLocationString() {
                return "";
            }

            @NotNull
            @Override
            public Icon getIcon(boolean unused) {
                return AbraIcons.TEMPLATE;
            }
        };
    }

    //====================================================================
    //====================== Range Expr ==================================
    //====================================================================

//    public static int getResolvedSize(AbraRangeExpr rangeExpr){
//        if(!rangeExpr.hasRangeOperator() && !rangeExpr.hasSmartRange())return 1;
//
//        if(rangeExpr.hasSmartRange()){
//            return rangeExpr.getConstExprList().get(1).getResolvedSize();
//        }
//        if(rangeExpr.hasClosedRange()){
//            return rangeExpr.getConstExprList().get(1).getResolvedSize()-rangeExpr.getConstExprList().get(0).getResolvedSize();
//        }
//        PsiElement leftSibling = rangeExpr;
//        while(!(leftSibling instanceof AbraResolvable))leftSibling = leftSibling.getPrevSibling();
//        if(leftSibling instanceof AbraFieldNameRef){
//            return ((AbraFieldNameRef) leftSibling).getResolvedSize() - rangeExpr.getConstExprList().get(0).getResolvedSize();
//        }
//        if(leftSibling instanceof AbraParamOrVarNameRef){
//            return ((AbraParamOrVarNameRef) leftSibling).getResolvedSize() - rangeExpr.getConstExprList().get(0).getResolvedSize();
//        }
//        if(leftSibling instanceof AbraTypeOrPlaceHolderNameRef){
//            PsiElement resolved = leftSibling.getReference().resolve();
//            if(resolved instanceof AbraPlaceHolderTypeName){
//                resolved = ContextStack.INSTANCE.resolveInContext((AbraPlaceHolderTypeName) resolved);
//                if(resolved instanceof AbraTypeNameRef){
//                    return ((AbraTypeStmt) ContextStack.INSTANCE.get().peek().get(resolved).getParent()).getTypeSize().getResolvedSize();
//                }
//            }
//            if(resolved instanceof AbraTypeName){
//                return ((AbraTypeStmt) resolved.getParent()).getResolvedSize();
//            }
//        }
//        throw new UnresolvableTokenException(rangeExpr.getText());
//    }

    public static boolean hasRangeOperator(AbraRangeExpr rangeExpr){
        return rangeExpr.getText().contains("..");
    }

    public static boolean hasOpenRange(AbraRangeExpr rangeExpr){
        return hasRangeOperator(rangeExpr) && rangeExpr.getConstExprList().size()==1;
    }

    public static boolean hasClosedRange(AbraRangeExpr rangeExpr){
        return hasRangeOperator(rangeExpr) && rangeExpr.getConstExprList().size()==2;
    }

    public static boolean hasSmartRange(AbraRangeExpr rangeExpr){
        return rangeExpr.getText().contains(":");
    }

    //====================================================================
    //====================== Const Stuff =================================
    //====================================================================

//    public static int getResolvedSize(AbraSliceExpr sliceExpr){
//        if(sliceExpr.getRangeExpr()==null){
//            if(sliceExpr.getFieldNameRefList().size()==0)
//                return sliceExpr.getParamOrVarNameRef().getResolvedSize();
//            return sliceExpr.getFieldNameRefList().get(sliceExpr.getFieldNameRefList().size()-1).getResolvedSize();
//        }
//        return sliceExpr.getRangeExpr().getResolvedSize();
//    }

    public static boolean hasRangeOperator(AbraSliceExpr sliceExpr){
        return sliceExpr.getRangeExpr()!=null && sliceExpr.getRangeExpr().hasRangeOperator();
    }

    public static boolean hasOpenRange(AbraSliceExpr sliceExpr){
        return sliceExpr.getRangeExpr()!=null && sliceExpr.getRangeExpr().hasOpenRange();
    }

    public static boolean hasClosedRange(AbraSliceExpr sliceExpr){
        return sliceExpr.getRangeExpr()!=null && sliceExpr.getRangeExpr().hasClosedRange();
    }


//    public static int getResolvedSize(AbraParamOrVarNameRef paramOrVarNameRef){
//        PsiElement resolved = paramOrVarNameRef.getReference().resolve();
//        if(resolved instanceof AbraVarName){
//            return ((AbraVarName)resolved).getResolvedSize();
//        }
//        if(resolved instanceof AbraParamName){
//            return ((AbraParamName)resolved).getResolvedSize();
//        }
//        throw new UnresolvableTokenException(paramOrVarNameRef.getText());
//    }

    public static int getVirtualFunctionResolvedSize(AbraUseStmt useStatement,String funcName,int size){
        for(AbraTypeInstantiation typeInstantiation:useStatement.getTypeInstantiationList()){
            AbraTypeName typeName = (AbraTypeName) typeInstantiation.getTypeNameRefList().get(0).getReference().resolve();
            if(typeName!=null && ((AbraTypeStmt)typeName.getParent()).getResolvedSize()==size){
                AbraTemplateName templateName = (AbraTemplateName) useStatement.getTemplateNameRef().getReference().resolve();
                if(templateName!=null){
                    AbraTemplateStmt templateStmt = (AbraTemplateStmt) templateName.getParent();
                    AbraFuncStmt templateFunction = getFuncWithNameInTemplate(funcName, templateStmt);
                    Map<Integer, AbraTypeNameRef> resolutionMap = new HashMap<>();
                    resolutionMap.put(size,typeInstantiation.getTypeNameRefList().get(0));
                    for(AbraTypeStmt typeStmt:templateStmt.getTypeStmtList()){
                        resolutionMap.put(getResolvedSize(typeStmt.getTypeSize().getConstExpr(),resolutionMap,templateStmt),AbraElementFactory.createAbraTypeNameRef(templateStmt.getProject(),typeStmt.getTypeName().getText()));
                    }
                    return getResolvedSize(templateFunction.getFuncSignature().getTypeSize().getConstExpr(),resolutionMap,(AbraTemplateStmt) templateName.getParent());
                }
            }
        }
        return -1;
    }

    public static int getResolvedSize(AbraInteger integer){
        return Integer.valueOf(integer.getText());
    }

    public static int getResolvedSize(AbraTypeExpr typeExpr){
        int r = 0;
        for(AbraFieldNameRef fieldNameRef:typeExpr.getFieldNameRefList()){
            r += fieldNameRef.getResolvedSize();
        }
        return r;
    }

    public static int getResolvedSize(AbraFieldNameRef fieldNameRef) throws UnresolvableTokenException {
        AbraFieldName resolved = (AbraFieldName)fieldNameRef.getReference().resolve();
        if(resolved!=null){
            return ((AbraFieldSpec)resolved.getParent()).getTypeSize().getResolvedSize();
        }
        throw new UnresolvableTokenException(fieldNameRef.getText());
    }

    public static int getResolvedSize(AbraTypeSize typeSize){
        return typeSize.getConstExpr().getResolvedSize();
    }

    public static int getResolvedSize(AbraParamName paramName){
        return ((AbraFuncParameter)paramName.getParent()).getTypeSize().getResolvedSize();
    }

    public static int getResolvedSize(AbraLutName lutName){
        return ((AbraLutStmt)lutName.getParent()).getLutEntryList().get(0).getOutputLength();
    }

//    public static int getResolvedSize(AbraVarName varName){
//        if(varName.getParent() instanceof AbraAssignExpr) {
//            if (((AbraAssignExpr) varName.getParent()).getTypeSize() != null) {
//                return ((AbraAssignExpr) varName.getParent()).getTypeSize().getResolvedSize();
//            }
//            return ((AbraAssignExpr) varName.getParent()).getMergeExpr().getResolvedSize();
//        }
//        return ((AbraStateExpr) varName.getParent()).getTypeSize().getResolvedSize();
//    }
//
//    public static int getResolvedSize(AbraReturnExpr returnExpr){
//        return returnExpr.getMergeExpr().getResolvedSize();
//    }

//    public static int getResolvedSize(AbraConcatExpr concatExpr){
//        int r = 0;
//        for(AbraPostfixExpr postfixExpr:concatExpr.getPostfixExprList()){
//            r = r+postfixExpr.getResolvedSize();
//        }
//        return r;
//    }

    public static int getResolvedSize(AbraLutExpr element){
        PsiElement resolved = element.getLutNameRef().getReference().resolve();
        if(resolved instanceof AbraLutName){
            return ((AbraLutStmt)resolved.getParent()).getLutEntryList().get(0).getOutputLength();
        }
        throw new UnresolvableTokenException(element.getText());
    }

    public static int getResolvedSize(AbraLutOrSliceExpr lutOrSliceExpr){
        PsiElement resolved = lutOrSliceExpr.getLutOrParamOrVarNameRef().getReference().resolve();
        if(resolved instanceof AbraLutName){
            return ((AbraLutStmt)resolved.getParent()).getLutEntryList().get(0).getOutputLength();
        }
//        if(resolved instanceof AbraVarName){
//            return ((AbraVarName)resolved).getResolvedSize();
//        }
//        if(resolved instanceof AbraParamName){
//            return ((AbraParamName)resolved).getResolvedSize();
//        }
//        throw new UnresolvableTokenException(lutOrSliceExpr.getText());
        return 1;
    }

    public static boolean isTypeOrPlaceHolderNameRef(AbraConstExpr element){
        if(element.getConstTermList().size()==1){
            if(element.getConstTermList().get(0).getConstFactorList().size()==1){
                if(element.getConstTermList().get(0).getConstFactorList().get(0).getTypeOrPlaceHolderNameRef()!=null){
                    return element.getText().equals(element.getConstTermList().get(0).getConstFactorList().get(0).getTypeOrPlaceHolderNameRef().getText());
                }
            }
        }
        return false;
    }

    public static AbraTypeOrPlaceHolderNameRef getTypeOrPlaceHolderNameRef(AbraConstExpr element){
        if(isTypeOrPlaceHolderNameRef(element)){
            return element.getConstTermList().get(0).getConstFactorList().get(0).getTypeOrPlaceHolderNameRef();
        }
        return null;
    }


    public static int getResolvedSize(AbraConstExpr element){
        if(element.getConstTermList().size()==1){
            return element.getConstTermList().get(0).getResolvedSize();
        }
        int lhs = element.getConstTermList().get(0).getResolvedSize();
        int rhs = element.getConstTermList().get(1).getResolvedSize();
        if(element.getText().substring(element.getConstTermList().get(0).getTextLength()).trim().startsWith("+")){
            return lhs+rhs;
        }
        return lhs-rhs;
    }

    public static int getResolvedSize(AbraConstExpr element, Map<Integer, AbraTypeNameRef> resolutionMap, AbraTemplateStmt templateStmt){
        if(element.getConstTermList().size()==1){
            return getResolvedSize(element.getConstTermList().get(0),resolutionMap,templateStmt);
        }
        int lhs = getResolvedSize(element.getConstTermList().get(0),resolutionMap,templateStmt);
        int rhs = getResolvedSize(element.getConstTermList().get(1),resolutionMap,templateStmt);
        if(element.getText().substring(element.getConstTermList().get(0).getTextLength()).trim().startsWith("+")){
            return lhs+rhs;
        }
        return lhs-rhs;
    }

    public static int getResolvedSize(AbraConstTerm element){
        if(element.getConstFactorList().size()==1){
            return element.getConstFactorList().get(0).getResolvedSize();
        }
        int lhs = element.getConstFactorList().get(0).getResolvedSize();
        int rhs = element.getConstFactorList().get(1).getResolvedSize();
        if(element.getText().substring(element.getConstFactorList().get(0).getTextLength()).trim().startsWith("*")){
            return lhs*rhs;
        }else if(element.getText().substring(element.getConstFactorList().get(0).getTextLength()).trim().startsWith("%")) {
            return lhs % rhs;
        }
        return lhs / rhs;
    }

    public static int getResolvedSize(AbraConstFactor element){
        if(element.getNumber()!=null){
            return element.getNumber().getResolvedSize();
        }
        if(element.getMinus()!=null){
            return -1* (element.getConstFactor().getResolvedSize());
        }
        if(element.getConstExpr()!=null){
            return element.getConstExpr().getResolvedSize();
        }
        PsiElement resolved = element.getTypeOrPlaceHolderNameRef().getReference().resolve();
        if(resolved!=null){
           if(resolved instanceof AbraTypeName){
               return ((AbraTypeStmt)resolved.getParent()).getResolvedSize();
           }
            if(resolved instanceof AbraPlaceHolderTypeName){
                PsiElement resolvedInContext = ContextStack.INSTANCE.resolveInContext((AbraPlaceHolderTypeName) resolved);
                if(resolvedInContext instanceof AbraPlaceHolderTypeName){
                    if(ContextStack.INSTANCE.isEmpty())return -3;
                    return ((AbraTypeStmt)ContextStack.INSTANCE.get().peek().get(resolvedInContext).getReference().resolve().getParent()).getTypeSize().getResolvedSize();
                }
                if(resolvedInContext instanceof AbraTypeNameRef){
                    PsiElement e = resolvedInContext.getReference().resolve();
                    return e==null?-4:((AbraTypeStmt)e.getParent()).getResolvedSize();
                }
                return ((AbraTypeStmt)resolvedInContext.getParent()).getResolvedSize();
            }
        }
        throw new UnresolvableTokenException(element.getText()+ " in file "+element.getContainingFile().getName());
    }

    public static int getResolvedSize(AbraNumber number){
        return Integer.valueOf(number.getText());
    }

    public static int getResolvedSize(AbraConstTerm element, Map<Integer, AbraTypeNameRef> resolutionMap, AbraTemplateStmt templateStmt){
        if(element.getConstFactorList().size()==1){
            return getResolvedSize(element.getConstFactorList().get(0), resolutionMap, templateStmt);
        }
        int lhs = getResolvedSize(element.getConstFactorList().get(0), resolutionMap, templateStmt);
        int rhs = getResolvedSize(element.getConstFactorList().get(1), resolutionMap, templateStmt);
        if(element.getText().substring(element.getConstFactorList().get(0).getTextLength()).trim().startsWith("*")){
            return lhs*rhs;
        }
        if(element.getText().substring(element.getConstFactorList().get(0).getTextLength()).trim().startsWith("%")){
            return lhs % rhs;
        }
        return lhs / rhs;
    }

    public static int getResolvedSize(AbraConstFactor element, Map<Integer, AbraTypeNameRef> resolutionMap, AbraTemplateStmt templateStmt){
        if(element.getNumber()!=null){
            return element.getNumber().getResolvedSize();
        }
        if(element.getMinus()!=null){
            return -1* (element.getConstFactor().getResolvedSize());
        }
        if(element.getConstExpr()!=null){
            return getResolvedSize(element.getConstExpr(), resolutionMap, templateStmt);
        }
        PsiElement resolved = element.getTypeOrPlaceHolderNameRef().getReference().resolve();
        if(resolved!=null){
            if(resolved instanceof AbraTypeName){
                return ((AbraTypeStmt)resolved.getParent()).getResolvedSize();
            }
            if(resolved instanceof AbraPlaceHolderTypeName){
                String tag = resolved.getText();
                int index = getPlaceHolderIndex(tag, templateStmt);
                AbraTypeNameRef typeNameRef = resolutionMap.get(index);
                AbraTypeName typeName = (AbraTypeName) typeNameRef.getReference().resolve();
                if(typeName!=null){
                    return ((AbraTypeStmt)typeName.getParent()).getResolvedSize();
                }
            }
        }
        throw new UnresolvableTokenException(element.getText());
    }


    public static int getResolvedSize2(AbraConstExpr element, Map<String, Integer> resolutionMap, AbraTemplateStmt templateStmt){
        if(element.getConstTermList().size()==1){
            return getResolvedSize2(element.getConstTermList().get(0),resolutionMap,templateStmt);
        }
        int lhs = getResolvedSize2(element.getConstTermList().get(0),resolutionMap,templateStmt);
        int rhs = getResolvedSize2(element.getConstTermList().get(1),resolutionMap,templateStmt);
        if(element.getText().substring(element.getConstTermList().get(0).getTextLength()).trim().startsWith("+")){
            return lhs+rhs;
        }
        return lhs-rhs;
    }

    public static int getResolvedSize2(AbraConstTerm element, Map<String, Integer> resolutionMap, AbraTemplateStmt templateStmt){
        if(element.getConstFactorList().size()==1){
            return getResolvedSize2(element.getConstFactorList().get(0), resolutionMap, templateStmt);
        }
        int lhs = getResolvedSize2(element.getConstFactorList().get(0), resolutionMap, templateStmt);
        int rhs = getResolvedSize2(element.getConstFactorList().get(1), resolutionMap, templateStmt);
        if(element.getText().substring(element.getConstFactorList().get(0).getTextLength()).trim().startsWith("*")){
            return lhs*rhs;
        }
        if(element.getText().substring(element.getConstFactorList().get(0).getTextLength()).trim().startsWith("%")){
            return lhs % rhs;
        }
        return lhs / rhs;
    }

    public static int getResolvedSize2(AbraConstFactor element, Map<String, Integer> resolutionMap, AbraTemplateStmt templateStmt){
        if(element.getNumber()!=null){
            return element.getNumber().getResolvedSize();
        }
        if(element.getMinus()!=null){
            return -1* (element.getConstFactor().getResolvedSize());
        }
        if(element.getConstExpr()!=null){
            return getResolvedSize2(element.getConstExpr(), resolutionMap, templateStmt);
        }
        PsiElement resolved = element.getTypeOrPlaceHolderNameRef().getReference().resolve();
        if(resolved!=null){
            if(resolutionMap.get(resolved.getText())!=null)return resolutionMap.get(resolved.getText());
            if(resolved instanceof AbraTypeName)return ((AbraTypeName)resolved).getResolvedSize();
            return -1;
        }
        throw new UnresolvableTokenException(element.getText());
    }

    //====================================================================
    //====================== PlaceHolder==================================
    //====================================================================

    public static AbraTemplateStmt getTemplateStatement(AbraTypeOrPlaceHolderNameRef typeOrPlaceHolderNameRef){
        PsiElement stmt = typeOrPlaceHolderNameRef;
        while(!(stmt instanceof AbraFile) && !(stmt instanceof AbraTemplateStmt)) stmt = stmt.getParent();
        return stmt instanceof AbraTemplateStmt ? (AbraTemplateStmt)stmt : null;
    }

    public static int getPlaceHolderIndex(AbraTypeOrPlaceHolderNameRef typeOrPlaceHolderNameRef){
        AbraTemplateStmt stmt = getTemplateStatement(typeOrPlaceHolderNameRef);
        if(stmt==null) return -1;
        return getPlaceHolderIndex(typeOrPlaceHolderNameRef.getText(), stmt);
    }

    private static int getPlaceHolderIndex(String txt, AbraTemplateStmt stmt) {
        int i = 0;
        for(AbraPlaceHolderTypeName abraPlaceHolderName:stmt.getPlaceHolderTypeNameList()){
            if(abraPlaceHolderName.getText().equals(txt))return i;
            i++;
        }
        return -1;
    }

    public static String getName(AbraPlaceHolderTypeName element) {
        return element.getText();
    }

    public static PsiElement setName(AbraPlaceHolderTypeName element, String newName) {
        ASTNode globalIdNode = element.getNode().getFirstChildNode();
        if (globalIdNode != null) {
            AbraPlaceHolderTypeName templateName = AbraElementFactory.createAbraPlaceHolderName(element.getProject(), newName);
            ASTNode newKeyNode = templateName.getFirstChild().getNode();
            element.getNode().replaceChild(globalIdNode, newKeyNode);
        }
        return element;
    }

    public static PsiElement getNameIdentifier(AbraPlaceHolderTypeName element) {
        return element;
    }
    //====================================================================
    //====================== References ==================================
    //====================================================================

    @NotNull
    public static PsiReference getReference(AbraFieldNameRef fieldNameRef) {
        return new AbraFieldPsiReferenceImpl(fieldNameRef);
    }

    @NotNull
    public static PsiReference getReference(AbraTypeNameRef typeNameRef) {
        return new AbraTypePsiReferenceImpl(typeNameRef);
    }

    @NotNull
    public static PsiReference getReference(AbraTemplateNameRef templateNameRef) {
        return new AbraTemplatePsiReferenceImpl(templateNameRef);
    }

    @NotNull
    public static PsiReference getReference(AbraLutOrParamOrVarNameRef lutOrParamOrVarNameRef) {
        return new AbraLutOrVarOrParamPsiReferenceImpl(lutOrParamOrVarNameRef);
    }

    @NotNull
    public static PsiReference getReference(AbraLutNameRef lutNameRef) {
        return new AbraLutPsiReferenceImpl(lutNameRef);
    }

    @NotNull
    public static PsiReference getReference(AbraParamOrVarNameRef paramOrVarNameRef) {
        return new AbraVarOrParamPsiReferenceImpl(paramOrVarNameRef);
    }

    @NotNull
    public static PsiReference getReference(AbraTypeOrPlaceHolderNameRef typeOrPlaceHolderNameRef) {
        return new AbraTypeOrPlaceHolderPsiReferenceImpl(typeOrPlaceHolderNameRef);
    }

    @NotNull
    public static PsiReference getReference(AbraFuncNameRef funcNameRef) {
        return new AbraFuncPsiReferenceImpl(funcNameRef);
    }

    @NotNull
    public static PsiReference getReference(AbraVarName varName) {
        return new AbraStateVarPsiReferenceImpl(varName);
    }


    @NotNull
    public static PsiReference[] getReferences(AbraImportStmt importStmt){
        List<PsiReference> importedFiles = new ArrayList<>();
        VirtualFile srcRoot = importStmt.getSourceRoot();
        VirtualFile target = null;
        try{
            target = LocalFileSystem.getInstance().findFileByIoFile(new File(srcRoot.getPath(),importStmt.getFilePath()));
        }catch (NullPointerException e){
            //ignore : filename is not valid.
        }
        if(target!=null){
            VirtualFile[] children = target.getChildren();
            for(VirtualFile child:children){
                if(!child.isDirectory() && child.getFileType()==AbraFileType.INSTANCE){
                    importedFiles.add(new AbraFileReferencePsiReferenceImpl(importStmt, child));
                }
            }
        }else{
            try{
                target = LocalFileSystem.getInstance().findFileByIoFile(new File(srcRoot.getPath(),importStmt.getFilePath()+".abra"));
            }catch (NullPointerException e){
                //ignore : filename is not valid.
            }
            if(target!=null) {
                importedFiles.add(new AbraFileReferencePsiReferenceImpl(importStmt, target));
            }
        }
        PsiReference[] arr = new PsiReference[importedFiles.size()];
        return importedFiles.toArray(arr);
    }

    public static String getFilePath(AbraImportStmt importStmt){
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<importStmt.getPathNameList().size();i++){
            sb.append(importStmt.getPathNameList().get(i).getText());
            if(i+1<importStmt.getPathNameList().size())sb.append("/");
        }
        return sb.toString();
    }
    public static VirtualFile getSourceRoot(AbraImportStmt importStmt){
        List<VirtualFile> allRoots = getAllSourceRoot(importStmt.getProject());
        VirtualFile srcRoot = importStmt.getContainingFile().getVirtualFile();
        while(srcRoot!=null && !allRoots.contains(srcRoot))srcRoot = srcRoot.getParent();
        return srcRoot;
    }

    public static VirtualFile getSourceRoot(Project project, VirtualFile file){
        List<VirtualFile> allRoots = getAllSourceRoot(project);
        VirtualFile srcRoot = file;
        while(srcRoot!=null && !allRoots.contains(srcRoot))srcRoot = srcRoot.getParent();
        return srcRoot;
    }

    public static List<VirtualFile> getAllSourceRoot(Project project){
        ArrayList<VirtualFile> allRoots = new ArrayList<>();
        ModuleManager manager = ModuleManager.getInstance(project);
        Module[] modules = manager.getModules();
        for (Module module : modules) {
            ModuleRootManager root = ModuleRootManager.getInstance(module);
            allRoots.addAll(Arrays.asList(root.getSourceRoots()));
            allRoots.addAll(Arrays.asList(root.getContentRoots()));
        }
        return allRoots;
    }

    public static AbraFile findFileForPath(Project project, String path){
        AbraFile resp = null;
        List<VirtualFile> allRoots = getAllSourceRoot(project);
        for(VirtualFile vf:allRoots){
            VirtualFile tmp = vf.findFileByRelativePath(path+".abra");
            if(tmp!=null && tmp.exists()){
                return (AbraFile) PsiManager.getInstance(project).findFile(tmp);
            }
        }
        return null;
    }
    //====================================================================
    //====================== FuncParams ==================================
    //====================================================================

    public static AbraFuncSignature getFuncSignature(AbraFuncBody funcBody){
        return ((AbraFuncStmt)funcBody.getParent()).getFuncSignature();
    }
    public static String getName(AbraParamName element) {
        return element.getText();
    }

    public static PsiElement setName(AbraParamName element, String newName) {
        ASTNode globalIdNode = element.getNode().getFirstChildNode();
        if (globalIdNode != null) {
            AbraParamName paramName = AbraElementFactory.createAbraParamName(element.getProject(), newName);
            ASTNode newKeyNode = paramName.getFirstChild().getNode();
            element.getNode().replaceChild(globalIdNode, newKeyNode);
        }
        return element;
    }

    public static PsiElement getNameIdentifier(AbraParamName element) {
        return element;
    }

    public static AbraFuncExpr getFuncExpr(AbraFuncNameRef funcNameRef){
        return (AbraFuncExpr) funcNameRef.getParent();
    }

    public static AbraDefinition getStatment(AbraFuncExpr funcExpr){
        PsiElement abraDefinition = funcExpr;
        while(!(abraDefinition instanceof AbraDefinition))abraDefinition = abraDefinition.getParent();
        return (AbraDefinition) abraDefinition;
    }

    public static boolean isInTemplateStatement(AbraFuncExpr funcExpr){
        return getStatment(funcExpr) instanceof AbraTemplateStmt;
    }

    public static boolean isInFuncStatement(AbraFuncExpr funcExpr){
        return getStatment(funcExpr) instanceof AbraFuncStmt;
    }

    //====================================================================
    //====================== AbraField ===================================
    //====================================================================

    public static String getName(AbraFieldName element) {
        return element.getText();
    }

    public static PsiElement setName(AbraFieldName element, String newName) {
        ASTNode globalIdNode = element.getNode().getFirstChildNode();
        if (globalIdNode != null) {
            AbraFieldName paramName = AbraElementFactory.createAbraFieldName(element.getProject(), newName);
            ASTNode newKeyNode = paramName.getFirstChild().getNode();
            element.getNode().replaceChild(globalIdNode, newKeyNode);
        }
        return element;
    }

    public static PsiElement getNameIdentifier(AbraFieldName element) {
        return element;
    }

    //====================================================================
    //====================== LocalVars ===================================
    //====================================================================

    public static String getName(AbraVarName element) {
        return element.getText();
    }

    public static PsiElement setName(AbraVarName element, String newName) {
        ASTNode globalIdNode = element.getNode().getFirstChildNode();
        if (globalIdNode != null) {
            AbraVarName varName = AbraElementFactory.createAbraVarName(element.getProject(), newName);
            ASTNode newKeyNode = varName.getFirstChild().getNode();
            element.getNode().replaceChild(globalIdNode, newKeyNode);
        }
        return element;
    }

    public static PsiElement getNameIdentifier(AbraVarName element) {
        return element;
    }

    //====================================================================
    //====================== LUT =========================================
    //====================================================================

    public static int getLength(AbraTritList tritList){
        return 1+tritList.getTextLength()-tritList.getText().replaceAll(",","").length();
    }

    public static int getInputLength(AbraLutEntry lutEntry){
        return lutEntry.getTritListList().get(0).getLength();
    }

    public static int getOutputLength(AbraLutEntry lutEntry){
        return lutEntry.getTritListList().get(1).getLength();
    }


    //====================================================================
    //=============== Helper for UseStatement resolution =================
    //====================================================================


    public static class ContextStack extends ThreadLocal<Stack<Map<AbraPlaceHolderTypeName, AbraTypeNameRef>>>{

        public static final ContextStack INSTANCE = new ContextStack();

        public void push(Map<AbraPlaceHolderTypeName, AbraTypeNameRef> aContext){
            Stack<Map<AbraPlaceHolderTypeName, AbraTypeNameRef>> stack = get();
            if(stack==null){
                stack = new Stack<>();
                set(stack);
            }
            stack.push(aContext);
        }

        public void pop(){
            get().pop();
        }

        public PsiElement resolveInContext(AbraPlaceHolderTypeName elementToResolve){
            if(get()==null || get().size()==0)return elementToResolve;
            for(PsiElement key:get().peek().keySet()){
                if(key.getText().equals(elementToResolve.getText()))return get().peek().get(key);
            }
            return elementToResolve;
        }

        public boolean isEmpty() {
            return get()==null || get().size()==0;
        }
    }


    public static AbraFuncStmt getFuncWithNameInTemplate(String aName, AbraTemplateStmt templateStmt){
        if(templateStmt!=null) {
            for (AbraFuncStmt func : templateStmt.getFuncStmtList()) {
                if (func.getFuncSignature().getFuncName().getText().equals(aName)) return func;
            }
        }
        return null;
    }

    /**
     *
     * @param funcExpr
     * @param funcStmt
     * @return null when funcName don't match
     * @return empty list when none of the use statement for func statement match the type instantiation for func expr
     * @return the subset of typeinstantiation of funcExpr covered by the type instantiation of funcStmt
     */
    public static List<SizesInstantiation> match(AbraFuncExpr funcExpr, AbraFuncStmt funcStmt){
        if(! funcExpr.getFuncNameRef().getText().equals(funcStmt.getFuncSignature().getFuncName().getText())){
            return null;
        }
        List<SizesInstantiation> resp = new ArrayList<>();
        if(funcExpr.getConstExprList().size()==0){
            if(funcStmt.getFuncSignature().getConstExprList().size()==0){
                return resp;
            }
            return null;
        }
        List<SizesInstantiation> funcStmtSizesInst = null;
        if(funcStmt.getParent() instanceof AbraTemplateStmt) {
            funcStmtSizesInst = typesToSizesInstantiations(
                    funcStmt.getFuncSignature().getConstExprList(), (AbraTemplateStmt) funcStmt.getParent(), funcStmt.getAllTypeInstantiation());
        }else{
            SizesInstantiation sizesInstantiation = new SizesInstantiation();
            for(AbraConstExpr constExpr:funcStmt.getFuncSignature().getConstExprList()){
                sizesInstantiation.add(constExpr.getResolvedSize());
            }
            return Collections.singletonList(sizesInstantiation);
        }
        List<SizesInstantiation> allSizes = getAllSizesInstantiation(funcExpr);
        for(SizesInstantiation instantiation:allSizes){
            if(funcStmtSizesInst.contains(instantiation))resp.add(instantiation);
        }
        return resp.size()==0?null:resp;
    }

    public static AbraFuncStmt getFuncStmt(AbraFuncExpr funcExpr){
        PsiElement e = funcExpr;
        while(!(e instanceof AbraFuncStmt))e = e.getParent();
        return (AbraFuncStmt) e;
    }

    public static List<SizesInstantiation> getAllSizesInstantiation(AbraFuncExpr funcExpr){
        if(funcExpr.getConstExprList().size()==0){
            return new ArrayList<>();
        }
        if(!funcExpr.getFuncStmt().isInTemplate()){
            SizesInstantiation sizesInstantiation = new SizesInstantiation();
            for(AbraConstExpr constExpr:funcExpr.getConstExprList()){
                sizesInstantiation.add(constExpr.getResolvedSize());
            }
            return Collections.singletonList(sizesInstantiation);
        }
        AbraTemplateStmt templateStmt = (AbraTemplateStmt) funcExpr.getFuncStmt().getParent();
        AbraTypeInstantiation[] allTemplateInstantiations = templateStmt.getAllTypeInstantiation();
        ArrayList<SizesInstantiation> resp = typesToSizesInstantiations(funcExpr.getConstExprList(), templateStmt, allTemplateInstantiations);
        return resp;
    }

    @NotNull
    public static ArrayList<SizesInstantiation> typesToSizesInstantiations(List<AbraConstExpr> constExprList, AbraTemplateStmt templateStmt, AbraTypeInstantiation[] allTemplateInstantiations) {
        ArrayList<SizesInstantiation> resp = new ArrayList<>();
        for(AbraTypeInstantiation typeInstantiation:allTemplateInstantiations){
            SizesInstantiation sizesInstantiation = new SizesInstantiation();
            for(AbraConstExpr constExpr:constExprList){
                sizesInstantiation.add(getResolvedSize2(constExpr,getResolutionMap(templateStmt,typeInstantiation),templateStmt));
            }
            resp.add(sizesInstantiation);
        }
        return resp;
    }

    public static Map<String,Integer> getResolutionMap(AbraTemplateStmt templateStmt, AbraTypeInstantiation typeInstantiation){
        HashMap<String,Integer> map = new HashMap<>();
        int i=0;
        for(AbraTypeNameRef typeNameRef:typeInstantiation.getTypeNameRefList()) {
            AbraTypeName typeName = (AbraTypeName) typeNameRef.getReference().resolve();
            if(typeName!=null) {
                map.put(templateStmt.getPlaceHolderTypeNameList().get(i).getText(), typeName.getResolvedSize());
            }else {
                return Collections.EMPTY_MAP;
            }
            i++;
        }
        for(AbraTypeStmt typeStmt:templateStmt.getTypeStmtList()){
            if(typeStmt.getTypeSize()!=null) {
                map.put(typeStmt.getTypeName().getText(), getResolvedSize2(typeStmt.getTypeSize().getConstExpr(), map, templateStmt));
            }else{
               // System.out.println("hum... ? what do I do here ???");  --> ignore
            }
        }
        return map;
    }
    public static AbraTypeInstantiation[] getAllTypeInstantiation(AbraTemplateStmt templateStmt){
        Collection<VirtualFile> allAbraFiles = FilenameIndex.getAllFilesByExt(templateStmt.getProject(),"abra");
        ArrayList<AbraTypeInstantiation> allTypeInstantiation = new ArrayList<>();
        for(VirtualFile virtualFile:allAbraFiles){
            AbraFile abraFile = (AbraFile) PsiManager.getInstance(templateStmt.getProject()).findFile(virtualFile);
            if(abraFile.equals(templateStmt.getContainingFile()) || abraFile.isImporting((AbraFile) templateStmt.getContainingFile())){
                for (ASTNode stmt : abraFile.getNode().getChildren(TokenSet.create(AbraTypes.USE_STMT))) {
                        if (((AbraUseStmt) stmt.getPsi()).getTemplateNameRef().getText().equals(templateStmt.getTemplateName().getText())) {
                            allTypeInstantiation.addAll(((AbraUseStmt) stmt.getPsi()).getTypeInstantiationList());
                        }
                    }
                }
        }
        AbraTypeInstantiation[] arr = new AbraTypeInstantiation[allTypeInstantiation.size()];
        allTypeInstantiation.toArray(arr);
        return arr;
    }

    public static AbraTypeInstantiation[] getAllTypeInstantiation(AbraFuncStmt funcStmt){
        if(funcStmt.getParent() instanceof AbraTemplateStmt){
                return getAllTypeInstantiation((AbraTemplateStmt) funcStmt.getParent());
            }
        return new AbraTypeInstantiation[]{};
    }

    public static List<AbraFuncStmt> findAllFuncStmt(Project project, String name){
        ArrayList<AbraFuncStmt> resp = new ArrayList<>();
        Collection<VirtualFile> virtualFiles =FileBasedIndex.getInstance().getContainingFiles(
                FileTypeIndex.NAME, AbraFileType.INSTANCE, GlobalSearchScope.allScope(project));
        for (VirtualFile virtualFile : virtualFiles) {
            AbraFile abraFile = (AbraFile) PsiManager.getInstance(project).findFile(virtualFile);
            if (abraFile != null) {
                resp.addAll(abraFile.findAllFuncStmt(name));
            }
        }
        return resp;
    }

    public static List<AbraFuncNameRef> findAllFuncNameRef(Project project, String name){
        ArrayList<AbraFuncNameRef> resp = new ArrayList<>();
        Collection<VirtualFile> virtualFiles =FileBasedIndex.getInstance().getContainingFiles(
                FileTypeIndex.NAME, AbraFileType.INSTANCE, GlobalSearchScope.allScope(project));
        for (VirtualFile virtualFile : virtualFiles) {
            AbraFile abraFile = (AbraFile) PsiManager.getInstance(project).findFile(virtualFile);
            if (abraFile != null) {
                resp.addAll(abraFile.findAllFuncNameRef(name));
            }
        }
        return resp;
    }
}
