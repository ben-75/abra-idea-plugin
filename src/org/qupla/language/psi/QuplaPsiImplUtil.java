package org.qupla.language.psi;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ColoredItemPresentation;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiReference;
import com.intellij.psi.tree.TokenSet;
import org.qupla.ide.ui.QuplaIcons;
import org.qupla.language.QuplaFileType;
import org.qupla.language.UnresolvableTokenException;
import org.qupla.language.module.QuplaModuleManager;
import org.qupla.language.psi.impl.*;
import org.qupla.ide.highlighter.QuplaSyntaxHighlighter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.File;
import java.util.*;

public class QuplaPsiImplUtil {


    //====================================================================
    //====================== QuplaTypeStmt ================================
    //====================================================================


    @NotNull
    public static ItemPresentation getPresentation(QuplaTypeStmt element) {
        return new ItemPresentation() {
            @NotNull
            @Override
            public String getPresentableText() {
                QuplaTypeName typeName = element.getTypeName();
                return typeName==null?"?":typeName.getText();
            }

            @NotNull
            @Override
            public String getLocationString() {
                try {
                    int sz = element.getResolvedSize();
                    String txt = null;
                    if(element.getTypeSize()==null){
                        if(element.getTypeAlias()!=null){
                            txt = element.getTypeAlias().getTypeNameRef().getText();
                        }else {
                            txt = "";
                        }
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
                return QuplaIcons.TYPE;
            }
        };
    }

    public static String getName(QuplaTypeStmt element) {
        return element.getTypeName().getText();
    }

    public static PsiElement setName(QuplaTypeStmt element, String newName) {
        ASTNode globalIdNode = element.getTypeName().getNode();
        if (globalIdNode != null) {
            QuplaTypeName typeName = QuplaElementFactory.createQuplaTypeName(element.getProject(), newName);
            ASTNode newKeyNode = typeName.getFirstChild().getNode();
            element.getNode().replaceChild(globalIdNode, newKeyNode);
        }
        return element;
    }

    public static PsiElement getNameIdentifier(QuplaTypeStmt element) {
        return element.getTypeName();
    }

    public static int getResolvedSize(QuplaTypeStmt element){
        if(element.getFieldSpecList().size()>0) {
            int resolvedSize = 0;
            for(QuplaFieldSpec fieldSpec:element.getFieldSpecList()){
                resolvedSize = resolvedSize + fieldSpec.getResolvedSize();
            }
            return resolvedSize;
        }
        QuplaTypeSize typeSize = element.getTypeSize();
        if(typeSize!=null) {
            return typeSize.getResolvedSize();
        }
        QuplaTypeAlias typeAlias = element.getTypeAlias();
        if(typeAlias!=null){
            QuplaTypeName reference = (QuplaTypeName) typeAlias.getTypeNameRef().getReference().resolve();
            if(reference!=null){
                return reference.getResolvedSize();
            }
        }
        return -1;
    }

    public static String getName(QuplaTypeName element) {
        return element.getText();
    }

    public static PsiElement setName(QuplaTypeName element, String newName) {
        ASTNode globalIdNode = element.getNode().getFirstChildNode();
        if (globalIdNode != null) {
            QuplaTypeName typeName = QuplaElementFactory.createQuplaTypeName(element.getProject(), newName);
            ASTNode newKeyNode = typeName.getFirstChild().getNode();
            element.getNode().replaceChild(globalIdNode, newKeyNode);
        }
        return element;
    }

    public static PsiElement getNameIdentifier(QuplaTypeName element) {
        return element;
    }

    public static int getResolvedSize(QuplaTypeName element){
        return ((QuplaTypeStmt)element.getParent()).getResolvedSize();
    }

    public static ItemPresentation getPresentation(QuplaTypeName typeName) {
        return new ColoredItemPresentation() {
            @NotNull
            @Override
            public TextAttributesKey getTextAttributesKey() {
                return QuplaSyntaxHighlighter.QUPLA_TYPE_DECLARATION;
            }

            @Nullable
            @Override
            public String getPresentableText() {
                return typeName.getText();
            }

            @Nullable
            @Override
            public String getLocationString() {
                return ((QuplaTypeStmt) typeName.getParent()).getPresentation().getLocationString();
            }

            @Nullable
            @Override
            public Icon getIcon(boolean unused) {
                return QuplaIcons.TYPE;
            }
        };
    }


    public static ItemPresentation getPresentation(QuplaVarName varName) {
        return new ColoredItemPresentation() {
            @Nullable
            @Override
            public TextAttributesKey getTextAttributesKey() {
                return QuplaSyntaxHighlighter.QUPLA_LOCAL_VAR;
            }

            @Nullable
            @Override
            public String getPresentableText() {
                return varName.getText();
            }

            @Nullable
            @Override
            public String getLocationString() {
                return "(local variable)";
            }

            @Nullable
            @Override
            public Icon getIcon(boolean unused) {
                return QuplaIcons.VECTOR;
            }
        };
    }



    public static ItemPresentation getPresentation(QuplaParamName paramName) {
        return new ColoredItemPresentation() {
            @Nullable
            @Override
            public TextAttributesKey getTextAttributesKey() {
                return QuplaSyntaxHighlighter.QUPLA_LOCAL_VAR;
            }

            @Nullable
            @Override
            public String getPresentableText() {
                return paramName.getText();
            }

            @Nullable
            @Override
            public String getLocationString() {
                return "(function parameter)";
            }

            @Nullable
            @Override
            public Icon getIcon(boolean unused) {
                return QuplaIcons.VECTOR;
            }
        };
    }

    //====================================================================
    //====================== QuplaLutStmt =================================
    //====================================================================

    @NotNull
    public static ItemPresentation getPresentation(QuplaLutStmt element) {
        return new ItemPresentation() {
            @NotNull
            @Override
            public String getPresentableText() {
                return element.getLutName().getText();
            }

            @NotNull
            @Override
            public String getLocationString() {
                List<QuplaLutEntry> entries = element.getLutEntryList();
                if(entries.size()==0 && element.getLutAlias()!=null){
                    QuplaLutName lutName = (QuplaLutName) element.getLutAlias().getLutNameRef().getReference().resolve();
                    if(lutName!=null){
                        entries = ((QuplaLutStmt)lutName.getParent()).getLutEntryList();
                    }
                }
                if(entries!=null && entries.size()>0) {
                    return "(" + entries.get(0).getInputLength() + ") -> " +
                            entries.get(0).getOutputLength();
                }else{
                    return "? -> ?";
                }
            }

            @NotNull
            @Override
            public Icon getIcon(boolean unused) {
                return QuplaIcons.LUT;
            }
        };
    }

    public static String getName(QuplaLutStmt element) {
        return element.getLutName().getText();
    }

    public static PsiElement setName(QuplaLutStmt element, String newName) {
        ASTNode globalIdNode = element.getLutName().getNode();
        if (globalIdNode != null) {
            QuplaLutName lutName = QuplaElementFactory.createQuplaLutName(element.getProject(), newName);
            ASTNode newKeyNode = lutName.getFirstChild().getNode();
            element.getNode().replaceChild(globalIdNode, newKeyNode);
        }
        return element;
    }

    public static PsiElement getNameIdentifier(QuplaLutStmt element) {
        return element.getLutName();
    }

    public static String getName(QuplaLutName element) {
        return element.getText();
    }

    public static PsiElement setName(QuplaLutName element, String newName) {
        ASTNode globalIdNode = element.getNode().getFirstChildNode();
        if (globalIdNode != null) {
            QuplaLutName lutName = QuplaElementFactory.createQuplaLutName(element.getProject(), newName);
            ASTNode newKeyNode = lutName.getFirstChild().getNode();
            element.getNode().replaceChild(globalIdNode, newKeyNode);
        }
        return element;
    }

    public static PsiElement getNameIdentifier(QuplaLutName element) {
        return element;
    }

    public static ItemPresentation getPresentation(QuplaLutName lutName) {
        return new ColoredItemPresentation() {
            @Nullable
            @Override
            public TextAttributesKey getTextAttributesKey() {
                return QuplaSyntaxHighlighter.QUPLA_FCT_DECLARATION;
            }

            @Nullable
            @Override
            public String getPresentableText() {
                return lutName.getText();
            }

            @Nullable
            @Override
            public String getLocationString() {
                return ((QuplaLutStmt) lutName.getParent()).getPresentation().getLocationString();
            }

            @Nullable
            @Override
            public Icon getIcon(boolean unused) {
                return QuplaIcons.LUT;
            }
        };
    }
    //====================================================================
    //====================== QuplaFieldSpec ===============================
    //====================================================================

    @NotNull
    public static ItemPresentation getPresentation(QuplaFieldSpec element) {
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
                return QuplaIcons.VECTOR;
            }
        };
    }

    public static int getResolvedSize(QuplaFieldSpec fieldSpec){
        return fieldSpec.getTypeSize().getResolvedSize();
    }

    //====================================================================
    //====================== QuplaFuncStmt ================================
    //====================================================================

    public static int getResolvedSize(QuplaTypeOrPlaceHolderNameRef typeOrPlaceHolderNameRef){
        PsiElement resolved = typeOrPlaceHolderNameRef.getReference().resolve();
        if(resolved instanceof QuplaTypeName){
            return ((QuplaTypeName)resolved).getResolvedSize();
        }
        return -1;
    }
    @NotNull
    public static ItemPresentation getPresentation(QuplaFuncStmt element) {
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
                for(QuplaFuncParameter p:element.getFuncSignature().getFuncParameterList()){
                    try{
                        int sz = p.getTypeOrPlaceHolderNameRef().getResolvedSize();
                        sb.append(sz<=0?p.getTypeOrPlaceHolderNameRef().getText():sz);
                    }catch (UnresolvableTokenException e){
                        sb.append("[?]");
                    }
                    i++;
                    if(i<element.getFuncSignature().getFuncParameterList().size())sb.append(" , ");
                }
                sb.append(" ) -> ");
                if(element.getFuncSignature().getTypeOrPlaceHolderNameRefList().size()>0){
                    try{
                        int sz = element.getFuncSignature().getTypeOrPlaceHolderNameRefList().get(0).getResolvedSize();
                        sb.append(sz<=0?element.getFuncSignature().getTypeOrPlaceHolderNameRefList().get(0).getText():sz);
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
                return QuplaIcons.FUNCTION;
            }
        };
    }

    public static String getName(QuplaFuncStmt element) {
        return element.getFuncSignature().getFuncName().getText();
    }

    public static PsiElement setName(QuplaFuncStmt element, String newName) {
        ASTNode globalIdNode = element.getFuncSignature().getFuncName().getNode();
        if (globalIdNode != null) {
            QuplaFuncName funcName = QuplaElementFactory.createQuplaFuncName(element.getProject(), newName);
            ASTNode newKeyNode = funcName.getFirstChild().getNode();
            element.getNode().replaceChild(globalIdNode, newKeyNode);
        }
        return element;
    }

    public static PsiElement getNameIdentifier(QuplaFuncStmt element) {
        return element.getFuncSignature().getFuncName();
    }

    public static String getName(QuplaFuncName element) {
        return element.getText();
    }

    public static PsiElement setName(QuplaFuncName element, String newName) {
        ASTNode globalIdNode = element.getNode().getFirstChildNode();
        if (globalIdNode != null) {
            QuplaFuncName funcName = QuplaElementFactory.createQuplaFuncName(element.getProject(), newName);
            ASTNode newKeyNode = funcName.getFirstChild().getNode();
            element.getNode().replaceChild(globalIdNode, newKeyNode);
        }
        return element;
    }

    public static PsiElement getNameIdentifier(QuplaFuncName element) {
        return element;
    }


    public static ItemPresentation getPresentation(QuplaFuncName funcName) {

        return new ColoredItemPresentation() {
            @Nullable
            @Override
            public TextAttributesKey getTextAttributesKey() {
                return QuplaSyntaxHighlighter.QUPLA_FCT_DECLARATION;
            }

            @Nullable
            @Override
            public String getPresentableText() {
                return funcName.getText();
            }

            @Nullable
            @Override
            public String getLocationString() {
                if (((QuplaFuncStmt) funcName.getParent().getParent()).isInTemplate()) {
                    return "(template: " + ((QuplaTemplateStmt) funcName.getParent().getParent().getParent()).getTemplateName().getText() + ")";
                }
                return "";
            }

            @Nullable
            @Override
            public Icon getIcon(boolean unused) {
                if (((QuplaFuncStmt) funcName.getParent().getParent()).isInTemplate()) {
                    return QuplaIcons.TEMPLATE;
                }
                return QuplaIcons.FUNCTION;
            }
        };
    }

    public static QuplaDefinition getStatement(QuplaFuncNameRef element){
        PsiElement quplaDefinition = element;
        while(!(quplaDefinition instanceof QuplaDefinition))quplaDefinition = quplaDefinition.getParent();
        return (QuplaDefinition) quplaDefinition;
    }

    public static boolean isInTemplate(QuplaFuncStmt funcStmt){
        return funcStmt.getParent() instanceof QuplaTemplateStmt;
    }
    //====================================================================
    //====================== QuplaTemplateStmt ============================
    //====================================================================

    @NotNull
    public static ItemPresentation getPresentation(QuplaTemplateStmt element) {
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
//                if(element.getFuncStmtList().size()==1) {
//                    QuplaFuncSignature sig = element.getFuncStmtList().get(0).getFuncSignature();
//                    return sig.getFuncName().getText()+"<"+
//                            String.join(",",sig.getConstExprList().stream().map(expr->expr.getText()).collect(Collectors.toList()))
//                            +">" + " -> " + sig.getTypeSize().getText();
//                }
                return "";
            }

            @NotNull
            @Override
            public Icon getIcon(boolean unused) {
                return QuplaIcons.TEMPLATE;
            }
        };
    }

    public static String getName(QuplaTemplateStmt element) {
        return element.getTemplateName().getText();
    }

    public static PsiElement setName(QuplaTemplateStmt element, String newName) {
        ASTNode globalIdNode = element.getTemplateName().getNode();
        if (globalIdNode != null) {
            QuplaTemplateName templateName = QuplaElementFactory.createQuplaTemplateName(element.getProject(), newName);
            ASTNode newKeyNode = templateName.getFirstChild().getNode();
            element.getNode().replaceChild(globalIdNode, newKeyNode);
        }
        return element;
    }

    public static PsiElement getNameIdentifier(QuplaTemplateStmt element) {
        return element.getTemplateName();
    }


    public static String getName(QuplaTemplateName element) {
        return element.getText();
    }

    public static PsiElement setName(QuplaTemplateName element, String newName) {
        ASTNode globalIdNode = element.getNode().getFirstChildNode();
        if (globalIdNode != null) {
            QuplaTemplateName templateName = QuplaElementFactory.createQuplaTemplateName(element.getProject(), newName);
            ASTNode newKeyNode = templateName.getFirstChild().getNode();
            element.getNode().replaceChild(globalIdNode, newKeyNode);
        }
        return element;
    }

    public static PsiElement getNameIdentifier(QuplaTemplateName element) {
        return element;
    }

    //====================================================================
    //====================== QuplaUseStmt =================================
    //====================================================================

    @NotNull
    public static ItemPresentation getPresentation(QuplaUseStmt element) {
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
                return QuplaIcons.USE;
            }
        };
    }

    public static String getName(QuplaUseStmt element) {
        return element.getTemplateNameRef().getText();
    }

    public static PsiElement setName(QuplaUseStmt element, String newName) {
        ASTNode globalIdNode = element.getTemplateNameRef().getNode();
        if (globalIdNode != null) {
            QuplaFuncName funcName = QuplaElementFactory.createQuplaFuncName(element.getProject(), newName);
            ASTNode newKeyNode = funcName.getFirstChild().getNode();
            element.getNode().replaceChild(globalIdNode, newKeyNode);
        }
        return element;
    }

    public static PsiElement getNameIdentifier(QuplaUseStmt element) {
        return element.getTemplateNameRef();
    }

    public static Map<Integer, QuplaTypeNameRef> getResolutionMap(QuplaUseStmt element, int size){
        HashMap<Integer, QuplaTypeNameRef> map = new HashMap<>();
        for(QuplaTypeInstantiation typeInstantiation:element.getTypeInstantiationList()){
            QuplaTypeName typeName = (QuplaTypeName) typeInstantiation.getTypeNameRefList().get(0).getReference().resolve();
            if(((QuplaTypeStmt)typeName.getParent()).getResolvedSize()==size) {
                for(int i=0;i<typeInstantiation.getTypeNameRefList().size();i++){
                    map.put(i, typeInstantiation.getTypeNameRefList().get(i));
                    i++;
                }
            }
        }
        return map;
    }

    public static Map<QuplaPlaceHolderTypeName, QuplaTypeNameRef> getTemplateContextMap(QuplaUseStmt useStmt, int size){
        Map<Integer, QuplaTypeNameRef> resolutionMap = getResolutionMap(useStmt, size);
        QuplaTemplateName templateName = (QuplaTemplateName) useStmt.getTemplateNameRef().getReference().resolve();
        HashMap<QuplaPlaceHolderTypeName, QuplaTypeNameRef> context = new HashMap();
        if(templateName==null)return context;
        QuplaTemplateStmt templateStmt = (QuplaTemplateStmt)templateName.getParent();
        int i=0;
        for(QuplaPlaceHolderTypeName phn:templateStmt.getPlaceHolderTypeNameList()){
            context.put(phn,resolutionMap.get(i));
        }
        return context;
    }

    //====================================================================
    //====================== QuplaUseStmt =================================
    //====================================================================

    @NotNull
    public static ItemPresentation getPresentation(QuplaTypeInstantiation element) {
        return new ItemPresentation() {
            @NotNull
            @Override
            public String getPresentableText() {
                return ((QuplaUseStmt)element.getParent()).getTemplateNameRef().getText()+"<"+element.getTypeNameRefList().get(0).getText()+">";
            }

            @NotNull
            @Override
            public String getLocationString() {
                return "";
            }

            @NotNull
            @Override
            public Icon getIcon(boolean unused) {
                return QuplaIcons.TEMPLATE;
            }
        };
    }

    //====================================================================
    //====================== Range Expr ==================================
    //====================================================================

//    public static int getResolvedSize(QuplaRangeExpr rangeExpr){
//        if(!rangeExpr.hasRangeOperator() && !rangeExpr.hasSmartRange())return 1;
//
//        if(rangeExpr.hasSmartRange()){
//            return rangeExpr.getConstExprList().get(1).getResolvedSize();
//        }
//        if(rangeExpr.hasClosedRange()){
//            return rangeExpr.getConstExprList().get(1).getResolvedSize()-rangeExpr.getConstExprList().get(0).getResolvedSize();
//        }
//        PsiElement leftSibling = rangeExpr;
//        while(!(leftSibling instanceof QuplaResolvable))leftSibling = leftSibling.getPrevSibling();
//        if(leftSibling instanceof QuplaFieldNameRef){
//            return ((QuplaFieldNameRef) leftSibling).getResolvedSize() - rangeExpr.getConstExprList().get(0).getResolvedSize();
//        }
//        if(leftSibling instanceof QuplaParamOrVarNameRef){
//            return ((QuplaParamOrVarNameRef) leftSibling).getResolvedSize() - rangeExpr.getConstExprList().get(0).getResolvedSize();
//        }
//        if(leftSibling instanceof QuplaTypeOrPlaceHolderNameRef){
//            PsiElement resolved = leftSibling.getReference().resolve();
//            if(resolved instanceof QuplaPlaceHolderTypeName){
//                resolved = ContextStack.INSTANCE.resolveInContext((QuplaPlaceHolderTypeName) resolved);
//                if(resolved instanceof QuplaTypeNameRef){
//                    return ((QuplaTypeStmt) ContextStack.INSTANCE.get().peek().get(resolved).getParent()).getTypeSize().getResolvedSize();
//                }
//            }
//            if(resolved instanceof QuplaTypeName){
//                return ((QuplaTypeStmt) resolved.getParent()).getResolvedSize();
//            }
//        }
//        throw new UnresolvableTokenException(rangeExpr.getText());
//    }

    public static boolean hasRangeOperator(QuplaRangeExpr rangeExpr){
        return rangeExpr.getText().contains("..");
    }

    public static boolean hasOpenRange(QuplaRangeExpr rangeExpr){
        return hasRangeOperator(rangeExpr) && rangeExpr.getConstExprList().size()==1;
    }

    public static boolean hasClosedRange(QuplaRangeExpr rangeExpr){
        return hasRangeOperator(rangeExpr) && rangeExpr.getConstExprList().size()==2;
    }

    public static boolean hasSmartRange(QuplaRangeExpr rangeExpr){
        return rangeExpr.getText().contains(":");
    }

    //====================================================================
    //====================== Const Stuff =================================
    //====================================================================

//    public static int getResolvedSize(QuplaSliceExpr sliceExpr){
//        if(sliceExpr.getRangeExpr()==null){
//            if(sliceExpr.getFieldNameRefList().size()==0)
//                return sliceExpr.getParamOrVarNameRef().getResolvedSize();
//            return sliceExpr.getFieldNameRefList().get(sliceExpr.getFieldNameRefList().size()-1).getResolvedSize();
//        }
//        return sliceExpr.getRangeExpr().getResolvedSize();
//    }

    public static boolean hasRangeOperator(QuplaSliceExpr sliceExpr){
        return sliceExpr.getRangeExpr()!=null && sliceExpr.getRangeExpr().hasRangeOperator();
    }

    public static boolean hasOpenRange(QuplaSliceExpr sliceExpr){
        return sliceExpr.getRangeExpr()!=null && sliceExpr.getRangeExpr().hasOpenRange();
    }

    public static boolean hasClosedRange(QuplaSliceExpr sliceExpr){
        return sliceExpr.getRangeExpr()!=null && sliceExpr.getRangeExpr().hasClosedRange();
    }



    public static int getResolvedSize(QuplaInteger integer){
        return Integer.valueOf(integer.getText());
    }

    public static int getResolvedSize(QuplaTypeExpr typeExpr){
        int r = 0;
        for(QuplaFieldNameRef fieldNameRef:typeExpr.getFieldNameRefList()){
            r += fieldNameRef.getResolvedSize();
        }
        return r;
    }

    public static int getResolvedSize(QuplaFieldNameRef fieldNameRef) throws UnresolvableTokenException {
        QuplaFieldName resolved = (QuplaFieldName)fieldNameRef.getReference().resolve();
        if(resolved!=null){
            return ((QuplaFieldSpec)resolved.getParent()).getTypeSize().getResolvedSize();
        }
        throw new UnresolvableTokenException(fieldNameRef.getText());
    }

    public static int getResolvedSize(QuplaTypeSize typeSize){
        return typeSize.getConstExpr().getResolvedSize();
    }

    public static int getResolvedSize(QuplaParamName paramName){
        return ((QuplaFuncParameter)paramName.getParent()).getTypeOrPlaceHolderNameRef().getResolvedSize();
    }

    public static int getResolvedSize(QuplaLutName lutName){
        return ((QuplaLutStmt)lutName.getParent()).getLutEntryList().get(0).getOutputLength();
    }

//    public static int getResolvedSize(QuplaVarName varName){
//        if(varName.getParent() instanceof QuplaAssignExpr) {
//            if (((QuplaAssignExpr) varName.getParent()).getTypeSize() != null) {
//                return ((QuplaAssignExpr) varName.getParent()).getTypeSize().getResolvedSize();
//            }
//            return ((QuplaAssignExpr) varName.getParent()).getMergeExpr().getResolvedSize();
//        }
//        return ((QuplaStateExpr) varName.getParent()).getTypeSize().getResolvedSize();
//    }
//
//    public static int getResolvedSize(QuplaReturnExpr returnExpr){
//        return returnExpr.getMergeExpr().getResolvedSize();
//    }

//    public static int getResolvedSize(QuplaConcatExpr concatExpr){
//        int r = 0;
//        for(QuplaPostfixExpr postfixExpr:concatExpr.getPostfixExprList()){
//            r = r+postfixExpr.getResolvedSize();
//        }
//        return r;
//    }

    public static int getResolvedSize(QuplaLutExpr element){
        PsiElement resolved = element.getLutNameRef().getReference().resolve();
        if(resolved instanceof QuplaLutName){
            return ((QuplaLutStmt)resolved.getParent()).getLutEntryList().get(0).getOutputLength();
        }
        throw new UnresolvableTokenException(element.getText());
    }

    public static int getResolvedSize(QuplaLutOrSliceExpr lutOrSliceExpr){
        PsiElement resolved = lutOrSliceExpr.getLutOrParamOrVarNameRef().getReference().resolve();
        if(resolved instanceof QuplaLutName){
            return ((QuplaLutStmt)resolved.getParent()).getLutEntryList().get(0).getOutputLength();
        }
//        if(resolved instanceof QuplaVarName){
//            return ((QuplaVarName)resolved).getResolvedSize();
//        }
//        if(resolved instanceof QuplaParamName){
//            return ((QuplaParamName)resolved).getResolvedSize();
//        }
//        throw new UnresolvableTokenException(lutOrSliceExpr.getText());
        return 1;
    }

    public static boolean isTypeOrPlaceHolderNameRef(QuplaConstExpr element){
        if(element.getConstTermList().size()==1){
            if(element.getConstTermList().get(0).getConstFactorList().size()==1){
                if(element.getConstTermList().get(0).getConstFactorList().get(0).getTypeOrPlaceHolderNameRef()!=null){
                    return element.getText().equals(element.getConstTermList().get(0).getConstFactorList().get(0).getTypeOrPlaceHolderNameRef().getText());
                }
            }
        }
        return false;
    }

    public static QuplaTypeOrPlaceHolderNameRef getTypeOrPlaceHolderNameRef(QuplaConstExpr element){
        if(isTypeOrPlaceHolderNameRef(element)){
            return element.getConstTermList().get(0).getConstFactorList().get(0).getTypeOrPlaceHolderNameRef();
        }
        return null;
    }


    public static int getResolvedSize(QuplaConstExpr element){
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

    public static int getResolvedSize(QuplaConstExpr element, Map<Integer, QuplaTypeNameRef> resolutionMap, QuplaTemplateStmt templateStmt){
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

    public static int getResolvedSize(QuplaConstTerm element){
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

    public static int getResolvedSize(QuplaConstFactor element){
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
           if(resolved instanceof QuplaTypeName){
               return ((QuplaTypeStmt)resolved.getParent()).getResolvedSize();
           }
            if(resolved instanceof QuplaPlaceHolderTypeName){
                PsiElement resolvedInContext = ContextStack.INSTANCE.resolveInContext((QuplaPlaceHolderTypeName) resolved);
                if(resolvedInContext instanceof QuplaPlaceHolderTypeName){
                    if(ContextStack.INSTANCE.isEmpty())return -3;
                    return ((QuplaTypeStmt)ContextStack.INSTANCE.get().peek().get(resolvedInContext).getReference().resolve().getParent()).getTypeSize().getResolvedSize();
                }
                if(resolvedInContext instanceof QuplaTypeNameRef){
                    PsiElement e = resolvedInContext.getReference().resolve();
                    return e==null?-4:((QuplaTypeStmt)e.getParent()).getResolvedSize();
                }
                return ((QuplaTypeStmt)resolvedInContext.getParent()).getResolvedSize();
            }
        }
        throw new UnresolvableTokenException(element.getText()+ " in file "+element.getContainingFile().getName());
    }

    public static int getResolvedSize(QuplaNumber number){
        return Integer.valueOf(number.getText());
    }

    public static int getResolvedSize(QuplaConstTerm element, Map<Integer, QuplaTypeNameRef> resolutionMap, QuplaTemplateStmt templateStmt){
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

    public static int getResolvedSize(QuplaConstFactor element, Map<Integer, QuplaTypeNameRef> resolutionMap, QuplaTemplateStmt templateStmt){
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
            if(resolved instanceof QuplaTypeName){
                return ((QuplaTypeStmt)resolved.getParent()).getResolvedSize();
            }
            if(resolved instanceof QuplaPlaceHolderTypeName){
                String tag = resolved.getText();
                int index = getPlaceHolderIndex(tag, templateStmt);
                QuplaTypeNameRef typeNameRef = resolutionMap.get(index);
                QuplaTypeName typeName = (QuplaTypeName) typeNameRef.getReference().resolve();
                if(typeName!=null){
                    return ((QuplaTypeStmt)typeName.getParent()).getResolvedSize();
                }
            }
        }
        throw new UnresolvableTokenException(element.getText());
    }


    public static int getResolvedSize2(QuplaTypeOrPlaceHolderNameRef element, Map<String, Integer> resolutionMap, QuplaTemplateStmt templateStmt){
        if(!resolutionMap.containsKey(element.getText())){
            return -1;
        }
        return resolutionMap.get(element.getText());
    }

    public static int getResolvedSize2(QuplaConstExpr element, Map<String, Integer> resolutionMap, QuplaTemplateStmt templateStmt){
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

    public static int getResolvedSize2(QuplaConstTerm element, Map<String, Integer> resolutionMap, QuplaTemplateStmt templateStmt){
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

    public static int getResolvedSize2(QuplaConstFactor element, Map<String, Integer> resolutionMap, QuplaTemplateStmt templateStmt){
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
            if(resolved instanceof QuplaTypeName)return ((QuplaTypeName)resolved).getResolvedSize();
            return -1;
        }
        throw new UnresolvableTokenException(element.getText());
    }

    //====================================================================
    //====================== PlaceHolder==================================
    //====================================================================

    public static QuplaTemplateStmt getTemplateStatement(QuplaTypeOrPlaceHolderNameRef typeOrPlaceHolderNameRef){
        PsiElement stmt = typeOrPlaceHolderNameRef;
        while(!(stmt instanceof QuplaFile) && !(stmt instanceof QuplaTemplateStmt)) stmt = stmt.getParent();
        return stmt instanceof QuplaTemplateStmt ? (QuplaTemplateStmt)stmt : null;
    }

    public static int getPlaceHolderIndex(QuplaTypeOrPlaceHolderNameRef typeOrPlaceHolderNameRef){
        QuplaTemplateStmt stmt = getTemplateStatement(typeOrPlaceHolderNameRef);
        if(stmt==null) return -1;
        return getPlaceHolderIndex(typeOrPlaceHolderNameRef.getText(), stmt);
    }

    private static int getPlaceHolderIndex(String txt, QuplaTemplateStmt stmt) {
        int i = 0;
        for(QuplaPlaceHolderTypeName quplaPlaceHolderTypeName:stmt.getPlaceHolderTypeNameList()){
            if(quplaPlaceHolderTypeName.getText().equals(txt))return i;
            i++;
        }
        return -1;
    }

    public static String getName(QuplaPlaceHolderTypeName element) {
        return element.getText();
    }

    public static PsiElement setName(QuplaPlaceHolderTypeName element, String newName) {
        ASTNode globalIdNode = element.getNode().getFirstChildNode();
        if (globalIdNode != null) {
            QuplaPlaceHolderTypeName templateName = QuplaElementFactory.createQuplaPlaceHolderName(element.getProject(), newName);
            ASTNode newKeyNode = templateName.getFirstChild().getNode();
            element.getNode().replaceChild(globalIdNode, newKeyNode);
        }
        return element;
    }

    public static PsiElement getNameIdentifier(QuplaPlaceHolderTypeName element) {
        return element;
    }
    //====================================================================
    //====================== References ==================================
    //====================================================================

    @NotNull
    public static PsiReference getReference(QuplaFieldNameRef fieldNameRef) {
        return new QuplaFieldPsiReferenceImpl(fieldNameRef);
    }

    @NotNull
    public static PsiReference getReference(QuplaTypeNameRef typeNameRef) {
        return new QuplaTypePsiReferenceImpl(typeNameRef);
    }

    @NotNull
    public static PsiReference getReference(QuplaTemplateNameRef templateNameRef) {
        return new QuplaTemplatePsiReferenceImpl(templateNameRef);
    }

    @NotNull
    public static PsiReference getReference(QuplaLutOrParamOrVarNameRef lutOrParamOrVarNameRef) {
        return new QuplaLutOrVarOrParamPsiReferenceImpl(lutOrParamOrVarNameRef);
    }

    @NotNull
    public static PsiReference getReference(QuplaLutNameRef lutNameRef) {
        return new QuplaLutPsiReferenceImpl(lutNameRef);
    }

    @NotNull
    public static PsiReference getReference(QuplaParamOrVarNameRef paramOrVarNameRef) {
        return new QuplaVarOrParamPsiReferenceImpl(paramOrVarNameRef);
    }

    @NotNull
    public static PsiReference getReference(QuplaTypeOrPlaceHolderNameRef typeOrPlaceHolderNameRef) {
        return new QuplaTypeOrPlaceHolderPsiReferenceImpl(typeOrPlaceHolderNameRef);
    }

    @NotNull
    public static PsiReference getReference(QuplaFuncNameRef funcNameRef) {
        return new QuplaFuncPsiReferenceImpl(funcNameRef);
    }

    @NotNull
    public static PsiReference getReference(QuplaVarName varName) {
        return new QuplaStateVarPsiReferenceImpl(varName);
    }


    @NotNull
    public static PsiReference[] getReferences(QuplaImportStmt importStmt){
        List<PsiReference> importedFiles = new ArrayList<>();
        VirtualFile srcRoot = importStmt.getSourceRoot();
        VirtualFile target = null;
        try{
            target = LocalFileSystem.getInstance().findFileByIoFile(new File(srcRoot.getPath(),importStmt.getFilePath()+".qpl"));
        }catch (NullPointerException e){
            //ignore : filename is not valid.
        }
        if(target!=null) {
            importedFiles.add(new QuplaFileReferencePsiReferenceImpl(importStmt, target));
        }else {

            try {
                target = LocalFileSystem.getInstance().findFileByIoFile(new File(srcRoot.getPath(), importStmt.getFilePath()));
            } catch (NullPointerException e) {
                //ignore : filename is not valid.
            }
            if (target != null) {
                VirtualFile[] children = target.getChildren();
                for (VirtualFile child : children) {
                    if (!child.isDirectory() && child.getFileType() == QuplaFileType.INSTANCE) {
                        importedFiles.add(new QuplaFileReferencePsiReferenceImpl(importStmt, child));
                    }
                }
            }
        }
        PsiReference[] arr = new PsiReference[importedFiles.size()];
        return importedFiles.toArray(arr);
    }

    public static final Key REF_FILES_KEY = new Key("RefFilesKey");

    public static List<QuplaFile> getReferencedFiles(QuplaImportStmt importStmt) {
            List<QuplaFile> resp = (List<QuplaFile>) importStmt.getUserData(REF_FILES_KEY);
            if (resp == null) {
                resp = new ArrayList<>();
                PsiReference[] importedFiles = QuplaPsiImplUtil.getReferences(importStmt);
                for (PsiReference psiRef : importedFiles) {
                    QuplaFile anQuplaFile = (QuplaFile) psiRef.resolve();
                    if (anQuplaFile != null) {
                        resp.add(anQuplaFile);
                    }
                }
                importStmt.putUserData(REF_FILES_KEY, resp);
            }
            return resp;
    }


    public static String getFilePath(QuplaImportStmt importStmt){
        return importStmt.getText().substring(7).trim();
    }
    public static VirtualFile getSourceRoot(QuplaImportStmt importStmt){
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

    public static QuplaFile findFileForPath(Project project, String path){
        QuplaFile resp = null;
        List<VirtualFile> allRoots = getAllSourceRoot(project);
        for(VirtualFile vf:allRoots){
            VirtualFile tmp = vf.findFileByRelativePath(path+".qpl");
            if(tmp!=null && tmp.exists()){
                return (QuplaFile) PsiManager.getInstance(project).findFile(tmp);
            }
        }
        return null;
    }
    //====================================================================
    //====================== FuncParams ==================================
    //====================================================================

    public static QuplaFuncSignature getFuncSignature(QuplaFuncBody funcBody){
        return ((QuplaFuncStmt)funcBody.getParent()).getFuncSignature();
    }
    public static String getName(QuplaParamName element) {
        return element.getText();
    }

    public static PsiElement setName(QuplaParamName element, String newName) {
        ASTNode globalIdNode = element.getNode().getFirstChildNode();
        if (globalIdNode != null) {
            QuplaParamName paramName = QuplaElementFactory.createQuplaParamName(element.getProject(), newName);
            ASTNode newKeyNode = paramName.getFirstChild().getNode();
            element.getNode().replaceChild(globalIdNode, newKeyNode);
        }
        return element;
    }

    public static PsiElement getNameIdentifier(QuplaParamName element) {
        return element;
    }

    public static QuplaFuncExpr getFuncExpr(QuplaFuncNameRef funcNameRef){
        return (QuplaFuncExpr) funcNameRef.getParent();
    }

    public static QuplaDefinition getStatment(QuplaFuncExpr funcExpr){
        PsiElement quplaDefinition = funcExpr;
        while(!(quplaDefinition instanceof QuplaDefinition))quplaDefinition = quplaDefinition.getParent();
        return (QuplaDefinition) quplaDefinition;
    }

    public static boolean isInTemplateStatement(QuplaFuncExpr funcExpr){
        return getStatment(funcExpr) instanceof QuplaTemplateStmt;
    }

    public static boolean isInFuncStatement(QuplaFuncExpr funcExpr){
        return getStatment(funcExpr) instanceof QuplaFuncStmt;
    }

    public static String getTypeLabelWithBrackets(QuplaFuncSignature funcSignature){
        if (funcSignature.getTypeOrPlaceHolderNameRefList().size()==0) return "";
        return funcSignature.getText().substring(funcSignature.getFuncName().getStartOffsetInParent(),
                                                     funcSignature.getTypeOrPlaceHolderNameRefList().get(funcSignature.getTypeOrPlaceHolderNameRefList().size()-1).getStartOffsetInParent()+funcSignature.getTypeOrPlaceHolderNameRefList().get(funcSignature.getTypeOrPlaceHolderNameRefList().size()-1).getTextLength())
                +">";
    }

    //====================================================================
    //====================== QuplaField ==================================
    //====================================================================

    public static String getName(QuplaFieldName element) {
        return element.getText();
    }

    public static PsiElement setName(QuplaFieldName element, String newName) {
        ASTNode globalIdNode = element.getNode().getFirstChildNode();
        if (globalIdNode != null) {
            QuplaFieldName paramName = QuplaElementFactory.createQuplaFieldName(element.getProject(), newName);
            ASTNode newKeyNode = paramName.getFirstChild().getNode();
            element.getNode().replaceChild(globalIdNode, newKeyNode);
        }
        return element;
    }

    public static PsiElement getNameIdentifier(QuplaFieldName element) {
        return element;
    }

    //====================================================================
    //====================== LocalVars ===================================
    //====================================================================

    public static String getName(QuplaVarName element) {
        return element.getText();
    }

    public static PsiElement setName(QuplaVarName element, String newName) {
        ASTNode globalIdNode = element.getNode().getFirstChildNode();
        if (globalIdNode != null) {
            QuplaVarName varName = QuplaElementFactory.createQuplaVarName(element.getProject(), newName);
            ASTNode newKeyNode = varName.getFirstChild().getNode();
            element.getNode().replaceChild(globalIdNode, newKeyNode);
        }
        return element;
    }

    public static PsiElement getNameIdentifier(QuplaVarName element) {
        return element;
    }

    //====================================================================
    //====================== LUT =========================================
    //====================================================================

    public static int getLength(QuplaTritList tritList){
        return 1+tritList.getTextLength()-tritList.getText().replaceAll(",","").length();
    }

    public static int getInputLength(QuplaLutEntry lutEntry){
        return lutEntry.getTritListList().get(0).getLength();
    }

    public static int getOutputLength(QuplaLutEntry lutEntry){
        return lutEntry.getTritListList().get(1).getLength();
    }


    //====================================================================
    //=============== Helper for UseStatement resolution =================
    //====================================================================


    public static class ContextStack extends ThreadLocal<Stack<Map<QuplaPlaceHolderTypeName, QuplaTypeNameRef>>>{

        public static final ContextStack INSTANCE = new ContextStack();

        public void push(Map<QuplaPlaceHolderTypeName, QuplaTypeNameRef> aContext){
            Stack<Map<QuplaPlaceHolderTypeName, QuplaTypeNameRef>> stack = get();
            if(stack==null){
                stack = new Stack<>();
                set(stack);
            }
            stack.push(aContext);
        }

        public void pop(){
            get().pop();
        }

        public PsiElement resolveInContext(QuplaPlaceHolderTypeName elementToResolve){
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


    public static QuplaFuncStmt getFuncWithNameInTemplate(String aName, QuplaTemplateStmt templateStmt){
        if(templateStmt!=null) {
            for (QuplaFuncStmt func : templateStmt.getFuncStmtList()) {
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
    public static List<SizesInstantiation> match(QuplaFuncExpr funcExpr, QuplaFuncStmt funcStmt){
        if(! funcExpr.getFuncNameRef().getText().equals(funcStmt.getFuncSignature().getFuncName().getText())){
            return null;
        }
        List<SizesInstantiation> resp = new ArrayList<>();
        if(funcExpr.getTypeOrPlaceHolderNameRefList().size()==0){
            if(funcStmt.getFuncSignature().getTypeOrPlaceHolderNameRefList().size()==0){
                return resp;
            }
            return null;
        }
        List<SizesInstantiation> funcStmtSizesInst = null;
        if(funcStmt.getParent() instanceof QuplaTemplateStmt) {
            funcStmtSizesInst = typesToSizesInstantiations(
                    funcStmt.getFuncSignature().getTypeOrPlaceHolderNameRefList(), (QuplaTemplateStmt) funcStmt.getParent(), funcStmt.getAllTypeInstantiation());
        }else{
            SizesInstantiation sizesInstantiation = new SizesInstantiation();
            for(QuplaTypeOrPlaceHolderNameRef constExpr:funcStmt.getFuncSignature().getTypeOrPlaceHolderNameRefList()){
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

    public static QuplaFuncStmt getFuncStmt(QuplaFuncExpr funcExpr){
        PsiElement e = funcExpr;
        while(!(e instanceof QuplaFuncStmt))e = e.getParent();
        return (QuplaFuncStmt) e;
    }

    public static List<SizesInstantiation> getAllSizesInstantiation(QuplaFuncExpr funcExpr){
        if(funcExpr.getTypeOrPlaceHolderNameRefList().size()==0){
            return new ArrayList<>();
        }
        if(!funcExpr.isInTemplate()){
            SizesInstantiation sizesInstantiation = new SizesInstantiation();
            for(QuplaTypeOrPlaceHolderNameRef typeOrPlaceHolderNameRef:funcExpr.getTypeOrPlaceHolderNameRefList()){
                sizesInstantiation.add(typeOrPlaceHolderNameRef.getResolvedSize());
            }
            return Collections.singletonList(sizesInstantiation);
        }
        QuplaTemplateStmt templateStmt = (QuplaTemplateStmt) funcExpr.getFuncStmt().getParent();
        QuplaTypeInstantiation[] allTemplateInstantiations = templateStmt.getAllTypeInstantiation();
        ArrayList<SizesInstantiation> resp = typesToSizesInstantiations(funcExpr.getTypeOrPlaceHolderNameRefList(), templateStmt, allTemplateInstantiations);
        return resp;
    }

    public static boolean isInTemplate(QuplaFuncExpr funcExpr){
        PsiElement e = funcExpr.getParent();
        while(! ((e instanceof QuplaFuncStmt)||(e instanceof QuplaTestStmt)))
            e = e.getParent();
        if(e instanceof QuplaTestStmt) return false;
        if(e instanceof QuplaFuncStmt) return isInTemplate((QuplaFuncStmt)e);
        return false;

    }
    @NotNull
    public static ArrayList<SizesInstantiation> typesToSizesInstantiations(List<QuplaTypeOrPlaceHolderNameRef> typeOrPlaceHolderNameRefs, QuplaTemplateStmt templateStmt, QuplaTypeInstantiation[] allTemplateInstantiations) {
        ArrayList<SizesInstantiation> resp = new ArrayList<>();
        for(QuplaTypeInstantiation typeInstantiation:allTemplateInstantiations){
            SizesInstantiation sizesInstantiation = new SizesInstantiation();
            for(QuplaTypeOrPlaceHolderNameRef typeOrPlaceHolderNameRef:typeOrPlaceHolderNameRefs){
                sizesInstantiation.add(getResolvedSize2(typeOrPlaceHolderNameRef,getResolutionMap(templateStmt,typeInstantiation),templateStmt));
            }
            resp.add(sizesInstantiation);
        }
        return resp;
    }

    public static Map<String,Integer> getResolutionMap(QuplaTemplateStmt templateStmt, QuplaTypeInstantiation typeInstantiation){
        HashMap<String,Integer> map = new HashMap<>();
        int i=0;
        for(QuplaTypeNameRef typeNameRef:typeInstantiation.getTypeNameRefList()) {
            QuplaTypeName typeName = (QuplaTypeName) typeNameRef.getReference().resolve();
            if(typeName!=null) {
                map.put(templateStmt.getPlaceHolderTypeNameList().get(i).getText(), typeName.getResolvedSize());
            }else {
                return Collections.EMPTY_MAP;
            }
            i++;
        }
        for(QuplaTypeStmt typeStmt:templateStmt.getTypeStmtList()){
            if(typeStmt.getTypeSize()!=null) {
                map.put(typeStmt.getTypeName().getText(), getResolvedSize2(typeStmt.getTypeSize().getConstExpr(), map, templateStmt));
            }
        }
        return map;
    }
    public static QuplaTypeInstantiation[] getAllTypeInstantiation(QuplaTemplateStmt templateStmt){
        Collection<QuplaFile> allQuplaFiles = templateStmt.getProject().getComponent(QuplaModuleManager.class).getAllVisibleFiles((QuplaFile) templateStmt.getContainingFile());
        ArrayList<QuplaTypeInstantiation> allTypeInstantiation = new ArrayList<>();
        for(QuplaFile quplaFile : allQuplaFiles){
            if(quplaFile.equals(templateStmt.getContainingFile()) || quplaFile.isImporting((QuplaFile) templateStmt.getContainingFile())){
                for (ASTNode stmt : quplaFile.getNode().getChildren(TokenSet.create(QuplaTypes.USE_STMT))) {
                        if (((QuplaUseStmt) stmt.getPsi()).getTemplateNameRef().getText().equals(templateStmt.getTemplateName().getText())) {
                            allTypeInstantiation.addAll(((QuplaUseStmt) stmt.getPsi()).getTypeInstantiationList());
                        }
                    }
                }
        }
        QuplaTypeInstantiation[] arr = new QuplaTypeInstantiation[allTypeInstantiation.size()];
        allTypeInstantiation.toArray(arr);
        return arr;
    }

    public static QuplaTypeInstantiation[] getAllTypeInstantiation(QuplaFuncStmt funcStmt){
        if(funcStmt.getParent() instanceof QuplaTemplateStmt){
                return getAllTypeInstantiation((QuplaTemplateStmt) funcStmt.getParent());
            }
        return new QuplaTypeInstantiation[]{};
    }

    public static List<QuplaFuncStmt> findAllFuncStmt(Project project, String name){
        ArrayList<QuplaFuncStmt> resp = new ArrayList<>();
        for(QuplaFile quplaFile :project.getComponent(QuplaModuleManager.class).getAllQuplaFiles()){
            resp.addAll(quplaFile.findAllFuncStmt(name));
        }
        return resp;
    }

    public static List<QuplaFuncNameRef> findAllFuncNameRef(Project project, String name){
        ArrayList<QuplaFuncNameRef> resp = new ArrayList<>();
        for(QuplaFile quplaFile :project.getComponent(QuplaModuleManager.class).getAllQuplaFiles()){
            resp.addAll(quplaFile.findAllFuncNameRef(name));
        }
        return resp;
    }

    public static List<QuplaFuncName> findAllFuncName(Project project, String name, List<QuplaFile> files) {
        ArrayList<QuplaFuncName> resp = new ArrayList<>();
        if (files == null) {
            for(QuplaFile quplaFile :project.getComponent(QuplaModuleManager.class).getAllQuplaFiles()){
                resp.addAll(quplaFile.findAllFuncName(name));
            }
        } else {
            for (QuplaFile f : files) {
                resp.addAll(f.findAllFuncName(name));
            }
        }
        return resp;
    }

    public static List<QuplaTypeName> findAllTypeName(Project project, String name, Collection<QuplaFile> files) {
        ArrayList<QuplaTypeName> resp = new ArrayList<>();
        if (files == null) {
            for(QuplaFile quplaFile :project.getComponent(QuplaModuleManager.class).getAllQuplaFiles()){
                resp.addAll(quplaFile.findAllTypeName(name));
            }
        } else {
            for (QuplaFile f : files) {
                resp.addAll(f.findAllTypeName(name));
            }
        }
       return resp;
    }

    public static List<QuplaTypeName> findAllConcreteTypeName(Project project, String name, Collection<QuplaFile> files) {
        ArrayList<QuplaTypeName> resp = new ArrayList<>();
        if (files == null) {
            for(QuplaFile quplaFile :project.getComponent(QuplaModuleManager.class).getAllQuplaFiles()){
                resp.addAll(quplaFile.findAllConcreteTypeName(name));
            }
        } else {
            for (QuplaFile f : files) {
                resp.addAll(f.findAllConcreteTypeName(name));
            }
        }
        return resp;
    }


}
