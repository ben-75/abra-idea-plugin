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
import com.intellij.psi.PsiReference;
import org.abra.language.AbraFileType;
import org.abra.language.AbraIcons;
import org.abra.language.UnresolvableTokenException;
import org.abra.language.psi.impl.*;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.File;
import java.util.*;

public class AbraPsiImplUtil {


    //====================================================================
    //====================== AbraTypeStmt ================================
    //====================================================================


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
                return "[" + element.getResolvedSize() + "]";
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
        if(element.getStaticTypeSize()==null) {
            int resolvedSize = 0;
            for(AbraFieldSpec fieldSpec:element.getFieldSpecList()){
                resolvedSize =+ fieldSpec.getStaticTypeSize().getResolvedSize();
            }
            return resolvedSize;
        }
        return element.getStaticTypeSize().getResolvedSize();
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

    //====================================================================
    //====================== AbraLutStmt =================================
    //====================================================================

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
                return "["+element.getStaticTypeSize().getResolvedSize()+"]";
            }

            @NotNull
            @Override
            public Icon getIcon(boolean unused) {
                return AbraIcons.VECTOR;
            }
        };
    }

    //====================================================================
    //====================== AbraFuncStmt ================================
    //====================================================================

    public static ItemPresentation getPresentation(AbraFuncStmt element) {
        return new ItemPresentation() {
            @NotNull
            @Override
            public String getPresentableText() {
                return element.getFuncDefinition().getFuncName().getText();
            }

            @NotNull
            @Override
            public String getLocationString() {
                StringBuilder sb = new StringBuilder("( ");
                int i=0;
                for(AbraFuncParameter p:element.getFuncDefinition().getFuncParameterList()){
                    sb.append(p.getTypeSize().getConstExpr().getResolvedSize());
                    i++;
                    if(i<element.getFuncDefinition().getFuncParameterList().size())sb.append(" , ");
                }
                sb.append(" ) -> ");
                if(element.getFuncDefinition().getTypeSize()!=null){
                    sb.append(element.getFuncDefinition().getTypeSize().getConstExpr().getResolvedSize());
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
        return element.getFuncDefinition().getFuncName().getText();
    }

    public static PsiElement setName(AbraFuncStmt element, String newName) {
        ASTNode globalIdNode = element.getFuncDefinition().getFuncName().getNode();
        if (globalIdNode != null) {
            AbraFuncName funcName = AbraElementFactory.createAbraFuncName(element.getProject(), newName);
            ASTNode newKeyNode = funcName.getFirstChild().getNode();
            element.getNode().replaceChild(globalIdNode, newKeyNode);
        }
        return element;
    }

    public static PsiElement getNameIdentifier(AbraFuncStmt element) {
        return element.getFuncDefinition().getFuncName();
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
    //====================================================================
    //====================== AbraTemplateStmt ============================
    //====================================================================

    public static ItemPresentation getPresentation(AbraTemplateStmt element) {
        return new ItemPresentation() {
            @NotNull
            @Override
            public String getPresentableText() {
                StringBuilder sb = new StringBuilder("<");
                int i=0;
                for(PsiElement p:element.getPlaceHolderNameList()){
                    i++;
                    sb.append(p.getText());
                    if(i<element.getPlaceHolderNameList().size())sb.append(",");
                }
                sb.append(">");
                return element.getTemplateName().getText()+sb.toString();
            }

            @NotNull
            @Override
            public String getLocationString() {
                if(element.getFuncDefinition().getTypeSize()==null)
                    return element.getFuncDefinition().getText().substring(0,element.getFuncDefinition().getText().indexOf("=")).trim();
                return element.getFuncDefinition().getText().substring(element.getFuncDefinition().getTypeSize().getStartOffsetInParent()+element.getFuncDefinition().getTypeSize().getTextLength()+1
                        ,element.getFuncDefinition().getText().indexOf("=")).trim()+ " -> "+element.getFuncDefinition().getTypeSize().getText().substring(1,element.getFuncDefinition().getTypeSize().getTextLength()-1);
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


    public static String getExpandedFunctionName(AbraTemplateStmt templateStmt, Map<Integer, AbraTypeNameRef> resolutionMap){
        int index = getPlaceHolderIndex(templateStmt.getFuncDefinition().getTypeOrPlaceHolderNameRef());
        if(index==-1) return "";
        return templateStmt.getFuncDefinition().getFuncName().getText()+"<"+resolutionMap.get(index).getText()+">";
    }


    private static String getExpandedFunctionParameters(AbraTemplateStmt templateStmt, Map<Integer, AbraTypeNameRef> resolutionMap){
        StringBuilder sb = new StringBuilder();
        int i=0;
        for(AbraFuncParameter p:templateStmt.getFuncDefinition().getFuncParameterList()){
            sb.append(getResolvedSize(p.getTypeSize().getConstExpr(),resolutionMap, templateStmt));
            i++;
            if(i<templateStmt.getFuncDefinition().getFuncParameterList().size())sb.append(" , ");
        }
        return sb.toString();
    }

    private static String getExpandedReturnType(AbraTemplateStmt templateStmt, Map<Integer, AbraTypeNameRef> resolutionMap){
        return ""+getResolvedSize(templateStmt.getFuncDefinition().getTypeSize().getConstExpr(), resolutionMap, templateStmt);
    }

    //====================================================================
    //====================== AbraUseStmt =================================
    //====================================================================

    public static ItemPresentation getPresentation(AbraUseStmt element) {
        return new ItemPresentation() {
            @NotNull
            @Override
            public String getPresentableText() {
                AbraTemplateName templateName = (AbraTemplateName) element.getTemplateNameRef().getReference().resolve();
                if(templateName==null)return element.getTemplateNameRef().getText();
                return getExpandedFunctionName((AbraTemplateStmt) templateName.getParent(), getResolutionMap(element));
            }

            @NotNull
            @Override
            public String getLocationString() {
                AbraTemplateName templateName = (AbraTemplateName) element.getTemplateNameRef().getReference().resolve();
                if(templateName==null)return "?";
                AbraTemplateStmt templateStmt = (AbraTemplateStmt) templateName.getParent();
                return "( " + getExpandedFunctionParameters(templateStmt, getResolutionMap(element)) +
                        " ) -> " + getExpandedReturnType(templateStmt, getResolutionMap(element));
            }

            @NotNull
            @Override
            public Icon getIcon(boolean unused) {
                return AbraIcons.FUNCTION;
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

    public static Map<Integer, AbraTypeNameRef> getResolutionMap(AbraUseStmt element){
        HashMap<Integer, AbraTypeNameRef> map = new HashMap<>();
        int i=0;
        for(AbraTypeNameRef typeNameRef:element.getTypeNameRefList()){
            map.put(i,typeNameRef);
            i++;
        }
        return map;
    }

    public static Map<AbraPlaceHolderName,AbraTypeNameRef> getTemplateContextMap(AbraUseStmt useStmt){
        Map<Integer, AbraTypeNameRef> resolutionMap = getResolutionMap(useStmt);
        AbraTemplateName templateName = (AbraTemplateName) useStmt.getTemplateNameRef().getReference().resolve();
        HashMap<AbraPlaceHolderName,AbraTypeNameRef> context = new HashMap();
        if(templateName==null)return context;
        AbraTemplateStmt templateStmt = (AbraTemplateStmt)templateName.getParent();
        int i=0;
        for(AbraPlaceHolderName phn:templateStmt.getPlaceHolderNameList()){
            context.put(phn,resolutionMap.get(i));
        }
        return context;
    }
    //====================================================================
    //====================== Const Stuff =================================
    //====================================================================

    public static int getResolvedSize(AbraMergeExpr element){
        return element.getConcatExprList().get(0).getResolvedSize();
    }

    public static int getResolvedSize(AbraConcatExpr element){
        int r = 0;
        for(AbraSliceExpr slice : element.getSliceExprList()){
            r+= slice.getResolvedSize();
        }
        for(AbraFuncExpr func : element.getFuncExprList()){
            r+= func.getResolvedSize();
        }
        for(AbraInteger integer : element.getIntegerList()){
            r+= integer.getResolvedSize();
        }
        for(AbraLutExpr lutExpr : element.getLutExprList()){
            r+= lutExpr.getResolvedSize();
        }
        for(AbraMergeExpr mergeExpr : element.getMergeExprList()){
            r+= mergeExpr.getResolvedSize();
        }
        for(AbraTypeExpr typeExpr : element.getTypeExprList()){
            r+= typeExpr.getResolvedSize();
        }
        return r;
    }

    public static int getResolvedSize(AbraSliceExpr sliceExpr){
        if(sliceExpr.getRangeExpr()==null){
            if(sliceExpr.getFieldNameRefList().size()==0)
                return sliceExpr.getParamOrVarNameRef().getResolvedSize();
            return sliceExpr.getFieldNameRefList().get(sliceExpr.getFieldNameRefList().size()-1).getResolvedSize();
        }
        if(!sliceExpr.hasRangeOperator())return 1;
        if(sliceExpr.hasClosedRange()){
            return sliceExpr.getRangeExpr().getConstExprList().get(1).getResolvedSize()-sliceExpr.getRangeExpr().getConstExprList().get(0).getResolvedSize();
        }
        PsiElement leftSibling = sliceExpr;
        while(!(leftSibling instanceof AbraResolvable))leftSibling = leftSibling.getPrevSibling();
        if(leftSibling instanceof AbraFieldNameRef){
            return ((AbraFieldNameRef) leftSibling).getResolvedSize() - sliceExpr.getRangeExpr().getConstExprList().get(0).getResolvedSize();
        }
        if(leftSibling instanceof AbraParamOrVarNameRef){
            return ((AbraParamOrVarNameRef) leftSibling).getResolvedSize() - sliceExpr.getRangeExpr().getConstExprList().get(0).getResolvedSize();
        }
        if(leftSibling instanceof AbraTypeOrPlaceHolderNameRef){
            PsiElement resolved = leftSibling.getReference().resolve();
            if(resolved instanceof AbraPlaceHolderName){
                resolved = ContextStack.INSTANCE.resolveInContext((AbraPlaceHolderName) resolved);
                if(resolved instanceof AbraPlaceHolderName){
                    return ((AbraTypeStmt) ContextStack.INSTANCE.get().peek().get(resolved).getParent()).getStaticTypeSize().getResolvedSize();
                }
            }
            if(resolved instanceof AbraTypeName){
                return ((AbraTypeStmt) resolved.getParent()).getResolvedSize();
            }
        }
        throw new UnresolvableTokenException(sliceExpr.getText());
    }

    public static boolean hasRangeOperator(AbraSliceExpr sliceExpr){
        return sliceExpr.getRangeExpr()!=null && sliceExpr.getRangeExpr().hasRangeOperator();
    }

    public static boolean hasOpenRange(AbraSliceExpr sliceExpr){
        return sliceExpr.hasRangeOperator() && sliceExpr.getRangeExpr().hasOpenRange();
    }

    public static boolean hasClosedRange(AbraSliceExpr sliceExpr){
        return sliceExpr.hasRangeOperator() && sliceExpr.getRangeExpr().hasClosedRange();
    }

    public static boolean hasRangeOperator(AbraRangeExpr rangeExpr){
        return rangeExpr.getText().contains("..");
    }

    public static boolean hasOpenRange(AbraRangeExpr rangeExpr){
        return rangeExpr.hasRangeOperator() && rangeExpr.getConstExprList().size()==1;
    }

    public static boolean hasClosedRange(AbraRangeExpr rangeExpr){
        return rangeExpr.hasRangeOperator() && rangeExpr.getConstExprList().size()==2;
    }

    public static int getResolvedSize(AbraParamOrVarNameRef paramOrVarNameRef){
        PsiElement resolved = paramOrVarNameRef.getReference().resolve();
        if(resolved instanceof AbraVarName){
            return ((AbraVarName)resolved).getResolvedSize();
        }
        if(resolved instanceof AbraParamName){
            return ((AbraParamName)resolved).getResolvedSize();
        }
        throw new UnresolvableTokenException(paramOrVarNameRef.getText());
    }

    public static int getResolvedSize(AbraFuncExpr funcExpr){
        PsiElement element = funcExpr.getFuncNameRef().getReference().resolve();
        if(element instanceof AbraFuncName){
            int funcResolvedSize = ((AbraFuncDefinition) element.getParent()).getFuncBody().getMergeExpr().getResolvedSize();
            if (funcExpr.hasPostSlice()) {
                if(!funcExpr.getRangeExpr().hasRangeOperator())return 1;
                else if(funcExpr.getRangeExpr().hasClosedRange())return (funcExpr.getRangeExpr().getConstExprList().get(1).getResolvedSize()-funcExpr.getRangeExpr().getConstExprList().get(0).getResolvedSize());
                else return funcResolvedSize-funcExpr.getRangeExpr().getConstExprList().get(0).getResolvedSize();
            }
            return funcResolvedSize;
        }
        if(element instanceof AbraTemplateNameRef){
            ContextStack.INSTANCE.push(getTemplateContextMap((AbraUseStmt) element.getParent()));
            try {


                if (funcExpr.hasPostSlice()) {
                    if(!funcExpr.getRangeExpr().hasRangeOperator())return 1;
                    else if(funcExpr.getRangeExpr().hasClosedRange())return (funcExpr.getRangeExpr().getConstExprList().get(1).getResolvedSize()-funcExpr.getRangeExpr().getConstExprList().get(0).getResolvedSize());
                }
                int funcResolvedSize = ((AbraTemplateStmt)((AbraUseStmt) element.getParent()).getTemplateNameRef().getReference().resolve().getParent()).getFuncDefinition().getFuncBody().getMergeExpr().getResolvedSize();
                if(funcExpr.hasPostSlice()){
                    return funcResolvedSize-funcExpr.getRangeExpr().getConstExprList().get(0).getResolvedSize();
                }
                return funcResolvedSize;
            }finally {
                ContextStack.INSTANCE.pop();
            }

        }
        throw new UnresolvableTokenException(funcExpr.getText());
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
            return ((AbraFieldSpec)resolved.getParent()).getStaticTypeSize().getResolvedSize();
        }
        throw new UnresolvableTokenException(fieldNameRef.getText());
    }

    public static int getResolvedSize(AbraTypeSize typeSize){
        return typeSize.getConstExpr().getResolvedSize();
    }
    public static int getResolvedSize(AbraStaticTypeSize typeSize){
        return typeSize.getStaticConstExpr().getResolvedSize();
    }

    public static int getResolvedSize(AbraParamName paramName){
        return ((AbraFuncParameter)paramName.getParent()).getTypeSize().getResolvedSize();
    }

    public static int getResolvedSize(AbraLutName lutName){
        return ((AbraLutStmt)lutName.getParent()).getLutEntryList().get(0).getTritListList().get(1).getLength();
    }

    public static int getResolvedSize(AbraVarName varName){
        if(((AbraAssignExpr)varName.getParent()).getTypeSize()!=null){
            return ((AbraAssignExpr)varName.getParent()).getTypeSize().getResolvedSize();
        }
        return ((AbraAssignExpr)varName.getParent()).getMergeExpr().getResolvedSize();
    }

    public static int getResolvedSize(AbraLutExpr element){
        AbraLutOrParamOrVarNameRef ref = element.getLutOrParamOrVarNameRef();
        PsiElement resolved = ref.getReference().resolve();
        if(resolved instanceof AbraLutName){
            return ((AbraLutName)resolved).getResolvedSize();
        }
        if(resolved instanceof AbraParamName){
            return ((AbraParamName)resolved).getResolvedSize();
        }
        if(resolved instanceof AbraVarName){
            return ((AbraVarName)resolved).getResolvedSize();
        }
        throw new UnresolvableTokenException(element.getText());
    }

    public static int getResolvedSize(AbraConstExpr element){
        if(element.getConstOperator()==null){
            return element.getConstTerm().getResolvedSize();
        }
        int lhs = element.getConstTerm().getResolvedSize();
        int rhs = element.getConstExpr().getResolvedSize();
        if(element.getConstOperator().getText().equals("+")){
            return lhs+rhs;
        }
        return lhs-rhs;
    }
    public static int getResolvedSize(AbraStaticConstExpr element){
        if(element.getConstOperator()==null){
            return element.getStaticConstTerm().getResolvedSize();
        }
        int lhs = element.getStaticConstTerm().getResolvedSize();
        int rhs = element.getStaticConstExpr().getResolvedSize();
        if(element.getConstOperator().getText().equals("+")){
            return lhs+rhs;
        }
        return lhs-rhs;
    }

    public static int getResolvedSize(AbraConstTerm element){
        if(element.getTermOperator()==null){
            return element.getConstPrimary().getResolvedSize();
        }
        int lhs = element.getConstPrimary().getResolvedSize();
        int rhs = element.getConstTerm().getResolvedSize();
        if(element.getTermOperator().getText().equals("*")){
            return lhs*rhs;
        }else if(element.getTermOperator().getText().equals("%")) {
            return lhs % rhs;
        }
        return lhs / rhs;
    }

    public static int getResolvedSize(AbraStaticConstTerm element){
        if(element.getTermOperator()==null){
            return element.getStaticConstPrimary().getResolvedSize();
        }
        int lhs = element.getStaticConstPrimary().getResolvedSize();
        int rhs = element.getStaticConstTerm().getResolvedSize();
        if(element.getTermOperator().getText().equals("*")){
            return lhs*rhs;
        }else if(element.getTermOperator().getText().equals("%")) {
            return lhs % rhs;
        }
        return lhs / rhs;
    }

    public static int getResolvedSize(AbraConstPrimary element){
        if(element.getTrit()!=null){
            if(element.getText().equals("-"))return -1;
            return Integer.valueOf(element.getText());
        }
        if(element.getInteger()!=null){
            return Integer.valueOf(element.getText());
        }
        if(element.getMergeExpr()!=null){
            return element.getMergeExpr().getResolvedSize();
        }
        PsiElement resolved = element.getTypeOrPlaceHolderNameRef().getReference().resolve();
        if(resolved!=null){
           if(resolved instanceof AbraTypeName){
               return ((AbraTypeStmt)resolved.getParent()).getResolvedSize();
           }
            if(resolved instanceof AbraPlaceHolderName){
                PsiElement resolvedInContext = ContextStack.INSTANCE.resolveInContext((AbraPlaceHolderName) resolved);
                if(resolvedInContext instanceof AbraPlaceHolderName){
                    if(ContextStack.INSTANCE.isEmpty())return -3;
                    return ((AbraTypeStmt)ContextStack.INSTANCE.get().peek().get(resolvedInContext).getReference().resolve().getParent()).getStaticTypeSize().getResolvedSize();
                }
                return ((AbraTypeStmt)resolved.getParent()).getResolvedSize();
            }
        }
        throw new UnresolvableTokenException(element.getText()+ " in file "+element.getContainingFile().getName());
    }

    public static int getResolvedSize(AbraStaticConstPrimary element) throws UnresolvableTokenException {
        if(element.getInteger()!=null){
            return Integer.valueOf(element.getText());
        }
        PsiElement resolved = element.getTypeNameRef().getReference().resolve();
        if(resolved instanceof AbraTypeName){
            return ((AbraTypeStmt)resolved.getParent()).getResolvedSize();
        }
        throw new UnresolvableTokenException(element.getTypeNameRef().getText());
    }


    public static int getResolvedSize(AbraConstExpr element, Map<Integer, AbraTypeNameRef> resolutionMap, AbraTemplateStmt templateStmt){
        if(element.getConstOperator()==null){
            return getResolvedSize(element.getConstTerm(), resolutionMap, templateStmt);
        }
        int lhs = getResolvedSize(element.getConstTerm(), resolutionMap, templateStmt);
        int rhs = getResolvedSize(element.getConstExpr(), resolutionMap, templateStmt);
        if(element.getConstOperator().getText().equals("+")){
            return lhs+rhs;
        }
        return lhs-rhs;
    }

    public static int getResolvedSize(AbraConstTerm element, Map<Integer, AbraTypeNameRef> resolutionMap, AbraTemplateStmt templateStmt){
        if(element.getTermOperator()==null){
            return getResolvedSize(element.getConstPrimary(), resolutionMap, templateStmt);
        }
        int lhs = getResolvedSize(element.getConstPrimary(), resolutionMap, templateStmt);
        int rhs = getResolvedSize(element.getConstTerm(), resolutionMap, templateStmt);
        if(element.getTermOperator().getText().equals("*")){
            return lhs*rhs;
        }else if(element.getTermOperator().getText().equals("%")) {
            return lhs % rhs;
        }
        return lhs / rhs;
    }

    public static int getResolvedSize(AbraConstPrimary element, Map<Integer, AbraTypeNameRef> resolutionMap, AbraTemplateStmt templateStmt){
        if(element.getTrit()!=null){
            if(element.getText().equals("-"))return -1;
            return Integer.valueOf(element.getText());
        }
        if(element.getInteger()!=null){
            return Integer.valueOf(element.getText());
        }
        if(element.getMergeExpr()!=null){
            return element.getMergeExpr().getResolvedSize();
        }
        PsiElement resolved = element.getTypeOrPlaceHolderNameRef().getReference().resolve();
        if(resolved!=null){
            if(resolved instanceof AbraTypeName){
                return ((AbraTypeStmt)resolved.getParent()).getResolvedSize();
            }
            if(resolved instanceof AbraPlaceHolderName){
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
        for(AbraPlaceHolderName abraPlaceHolderName:stmt.getPlaceHolderNameList()){
            if(abraPlaceHolderName.getText().equals(txt))return i;
            i++;
        }
        return -1;
    }

    public static String getName(AbraPlaceHolderName element) {
        return element.getText();
    }

    public static PsiElement setName(AbraPlaceHolderName element, String newName) {
        ASTNode globalIdNode = element.getNode().getFirstChildNode();
        if (globalIdNode != null) {
            AbraPlaceHolderName templateName = AbraElementFactory.createAbraPlaceHolderName(element.getProject(), newName);
            ASTNode newKeyNode = templateName.getFirstChild().getNode();
            element.getNode().replaceChild(globalIdNode, newKeyNode);
        }
        return element;
    }

    public static PsiElement getNameIdentifier(AbraPlaceHolderName element) {
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
    public static PsiReference[] getReferences(AbraImportStmt importStmt){
        List<PsiReference> importedFiles = new ArrayList<>();
        VirtualFile srcRoot = importStmt.getSourceRoot();
        VirtualFile target = null;
        try{
            target = LocalFileSystem.getInstance().findFileByIoFile(new File(srcRoot.getPath(),importStmt.getFilePath().getText()));
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
            target = LocalFileSystem.getInstance().findFileByIoFile(new File(srcRoot.getPath(),importStmt.getFilePath().getText()+".abra"));
            if(target!=null) {
                importedFiles.add(new AbraFileReferencePsiReferenceImpl(importStmt, target));
            }
        }
        PsiReference[] arr = new PsiReference[importedFiles.size()];
        return importedFiles.toArray(arr);
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
    //====================================================================
    //====================== FuncParams ==================================
    //====================================================================

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

    public static boolean hasRangeOperator(AbraFuncExpr funcExpr){
        return hasPostSlice(funcExpr) && funcExpr.getText().substring(funcExpr.getRangeExpr().getConstExprList().get(0).getStartOffsetInParent()).contains("..");
    }

    public static boolean hasOpenRange(AbraFuncExpr funcExpr){
        return hasPostSlice(funcExpr) &&funcExpr.hasRangeOperator() && funcExpr.getRangeExpr().getConstExprList().size()==1;
    }

    public static boolean hasClosedRange(AbraFuncExpr funcExpr){
        return hasPostSlice(funcExpr) && funcExpr.hasRangeOperator() && funcExpr.getRangeExpr().getConstExprList().size()==2;
    }

    public static boolean hasPostSlice(AbraFuncExpr funcExpr){
        return funcExpr.getRangeExpr()!=null;
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
        return tritList.getTritList().size();
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


    public static class ContextStack extends ThreadLocal<Stack<Map<AbraPlaceHolderName, AbraTypeNameRef>>>{

        public static ContextStack INSTANCE = new ContextStack();

        public void push(Map<AbraPlaceHolderName, AbraTypeNameRef> aContext){
            Stack<Map<AbraPlaceHolderName, AbraTypeNameRef>> stack = get();
            if(stack==null){
                stack = new Stack<>();
                set(stack);
            }
            stack.push(aContext);
        }

        public void pop(){
            get().pop();
        }

        public PsiElement resolveInContext(AbraPlaceHolderName elementToResolve){
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
}
