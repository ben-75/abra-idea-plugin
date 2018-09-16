package org.abra.language.psi;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.indexing.FileBasedIndex;
import org.abra.language.AbraFileType;
import org.abra.language.AbraIcons;
import org.abra.language.psi.impl.*;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.*;

public class AbraPsiImplUtil {


    //====================================================================
    //====================== AbraTypeStmt ================================
    //====================================================================


    public static ItemPresentation getPresentation(AbraTypeStmt element) {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                return element.getTypeName().getText();
            }

            @Nullable
            @Override
            public String getLocationString() {
                return "[" + element.getResolvedSize() + "]";
            }

            @Nullable
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
        int resolvedSize = 0;
        for(AbraTypeSize typeSize:element.getTypeSizeList()){
            resolvedSize += typeSize.getConstExpr().getResolvedSize();
        }
        return resolvedSize;
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
            @Nullable
            @Override
            public String getPresentableText() {
                return element.getLutName().getText();
            }

            @Nullable
            @Override
            public String getLocationString() {
                return "["+element.getTritListList().get(0).getTritList().size()+" -> "+
                        element.getTritListList().get(1).getTritList().size()+"]";
            }

            @Nullable
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
    //====================== AbraFuncStmt ================================
    //====================================================================

    public static ItemPresentation getPresentation(AbraFuncStmt element) {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                return element.getFuncDefinition().getFuncName().getText();
            }

            @Nullable
            @Override
            public String getLocationString() {
                StringBuilder sb = new StringBuilder("( ");
                int i=0;
                for(AbraFuncParameter p:element.getFuncDefinition().getFuncParameterList()){
                    sb.append(p.getTypeSize().getConstExpr().getResolvedSize());
                    i++;
                    if(i<element.getFuncDefinition().getFuncParameterList().size())sb.append(" , ");
                }
                sb.append(" )");
                return sb.toString();
            }

            @Nullable
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
    //====================================================================
    //====================== AbraTemplateStmt ============================
    //====================================================================

    public static ItemPresentation getPresentation(AbraTemplateStmt element) {
        return new ItemPresentation() {
            @Nullable
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

            @Nullable
            @Override
            public String getLocationString() {
                return element.getFuncDefinition().getText().substring(0,element.getFuncDefinition().getText().indexOf("=")).trim();
            }

            @Nullable
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
        return templateStmt.getFuncDefinition().getFuncName().getText()+resolutionMap.get(index).getText();
    }


    public static String getExpandedFunctionParameters(AbraTemplateStmt templateStmt, Map<Integer, AbraTypeNameRef> resolutionMap){
        StringBuilder sb = new StringBuilder();
        int i=0;
        for(AbraFuncParameter p:templateStmt.getFuncDefinition().getFuncParameterList()){
            sb.append(getResolvedSize(p.getTypeSize().getConstExpr(),resolutionMap, templateStmt));
            i++;
            if(i<templateStmt.getFuncDefinition().getFuncParameterList().size())sb.append(" , ");
        }
        return sb.toString();
    }

    //====================================================================
    //====================== AbraUseStmt =================================
    //====================================================================

    public static ItemPresentation getPresentation(AbraUseStmt element) {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                AbraTemplateName templateName = (AbraTemplateName) element.getTemplateNameRef().getReference().resolve();
                if(templateName==null)return element.getTemplateNameRef().getText();
                return getExpandedFunctionName((AbraTemplateStmt) templateName.getParent(), getResolutionMap(element));
            }

            @Nullable
            @Override
            public String getLocationString() {
                AbraTemplateName templateName = (AbraTemplateName) element.getTemplateNameRef().getReference().resolve();
                if(templateName==null)return "?";
                AbraTemplateStmt templateStmt = (AbraTemplateStmt) templateName.getParent();
                StringBuilder sb = new StringBuilder("( ");
                sb.append(getExpandedFunctionParameters(templateStmt, getResolutionMap(element)));
                sb.append(" )");
                return sb.toString();
            }

            @Nullable
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

    //====================================================================
    //====================== Const Stuff =================================
    //====================================================================

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

    public static int getResolvedSize(AbraConstPrimary element){
        if(element.getTrit()!=null){
            if(element.getText().equals("-"))return -1;
            return Integer.valueOf(element.getText());
        }
        if(element.getInteger()!=null){
            return Integer.valueOf(element.getText());
        }
        if(element.getConstExpr()!=null){
            return element.getConstExpr().getResolvedSize();
        }
        PsiElement resolved = element.getTypeOrPlaceHolderNameRef().getReference().resolve();
        if(resolved!=null){
           if(resolved instanceof AbraTypeName){
               return ((AbraTypeStmt)resolved.getParent()).getResolvedSize();
           }
        }
        return 0;
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
        if(element.getConstExpr()!=null){
            return element.getConstExpr().getResolvedSize();
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
        return 0;
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

    public static PsiReference getReference(AbraFieldNameRef fieldNameRef) {
        return null;
    }

    public static PsiReference getReference(AbraTypeNameRef typeNameRef) {
        return new AbraTypePsiReferenceImpl(typeNameRef);
    }

    public static PsiReference getReference(AbraTemplateNameRef templateNameRef) {
        return new AbraTemplatePsiReferenceImpl(templateNameRef);
    }

    public static PsiReference getReference(AbraLutOrParamOrVarNameRef lutOrParamOrVarNameRef) {
        return new AbraLutOrVarOrParamPsiReferenceImpl(lutOrParamOrVarNameRef);
    }

    public static PsiReference getReference(AbraParamOrVarNameRef paramOrVarNameRef) {
        return new AbraVarOrParamPsiReferenceImpl(paramOrVarNameRef);
    }

    public static PsiReference getReference(AbraTypeOrPlaceHolderNameRef typeOrPlaceHolderNameRef) {
        return new AbraTypeOrPlaceHolderPsiReferenceImpl(typeOrPlaceHolderNameRef);
    }

    public static PsiReference getReference(AbraFuncNameRef funcNameRef) {
        return new AbraFuncPsiReferenceImpl(funcNameRef);
    }

    public static PsiReference[] getReferences(AbraImportStmt importFile){
        Collection<VirtualFile> allFiles = FileBasedIndex.getInstance().getContainingFiles(FileTypeIndex.NAME, AbraFileType.INSTANCE, GlobalSearchScope.projectScope(importFile.getProject()));
        List<PsiReference> importedFiles = new ArrayList<>();
        importFile.getFilePath().getText();
        for(VirtualFile f:allFiles){
            if(f.getPath().indexOf(importFile.getFilePath().getText())>-1){
                importedFiles.add(new AbraFileReferencePsiReferenceImpl(importFile, f));
            }
        }
        PsiReference[] arr = new PsiReference[importedFiles.size()];
        return importedFiles.toArray(arr);
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





    public static int getLength(AbraTritList tritList){
        return tritList.getTritList().size();
    }



}
