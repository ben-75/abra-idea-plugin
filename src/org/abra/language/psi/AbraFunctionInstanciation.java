package org.abra.language.psi;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import org.abra.language.AbraIcons;
import org.abra.language.UnresolvableTokenException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.HashMap;

public class AbraFunctionInstanciation implements StructureViewTreeElement {

    private final AbraFuncStmt funcStmt;
    private final AbraTypeInstantiation typeInstantiation;
    private final AbraTemplateStmt templateStmt;
    private final String name;
    private final String locationString;
    public AbraFunctionInstanciation(AbraFuncStmt funcStmt, AbraTypeInstantiation typeInstantiation, AbraTemplateStmt templateStmt) {
        this.funcStmt = funcStmt;
        this.typeInstantiation = typeInstantiation;
        this.templateStmt = templateStmt;
        this.name = typeInstantiation.getText().substring(1,typeInstantiation.getTextLength()-1);
        AbraTypeName typeName = (AbraTypeName) typeInstantiation.getTypeNameRefList().get(0).getReference().resolve();
        if(typeName!=null){
            HashMap<String,Integer> map = new HashMap<>();
            map.put(templateStmt.getPlaceHolderTypeNameList().get(0).getText(), typeName.getResolvedSize());
            for(AbraTypeStmt localTypeStmt:templateStmt.getTypeStmtList()){
                map.put(localTypeStmt.getTypeName().getText(),AbraPsiImplUtil.getResolvedSize2(localTypeStmt.getTypeSize().getConstExpr(),map,templateStmt));
            }
            StringBuilder sb = new StringBuilder("( ");


            int i=0;
            for(AbraFuncParameter p:funcStmt.getFuncSignature().getFuncParameterList()){
                try{
                    int sz = AbraPsiImplUtil.getResolvedSize2(p.getTypeSize().getConstExpr(),map,templateStmt);
                    sb.append(sz<=0?p.getTypeSize().getConstExpr().getText():sz);
                }catch (UnresolvableTokenException e){
                    sb.append("[?]");
                }
                i++;
                if(i<funcStmt.getFuncSignature().getFuncParameterList().size())sb.append(" , ");
            }
            sb.append(" ) -> ");
            if(funcStmt.getFuncSignature().getTypeSize()!=null){
                try{
                    int sz = AbraPsiImplUtil.getResolvedSize2(funcStmt.getFuncSignature().getTypeSize().getConstExpr(),map,templateStmt);
                    sb.append(sz<=0?funcStmt.getFuncSignature().getTypeSize().getConstExpr().getText():sz);
                }catch (UnresolvableTokenException e){
                    sb.append("[?]");
                }
            }


            locationString = sb.toString();
        }else{
            locationString ="unresolved";
        }
    }

    @NotNull
    @Override
    public ItemPresentation getPresentation() {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                return funcStmt.getFuncSignature().getFuncName().getText()+"<"+ name +">";
            }

            @Nullable
            @Override
            public String getLocationString() {
                return locationString;
            }

            @Nullable
            @Override
            public Icon getIcon(boolean unused) {
                return AbraIcons.FUNCTION;
            }
        };
    }

    @NotNull
    @Override
    public TreeElement[] getChildren() {
        return EMPTY_ARRAY;
    }

    @Override
    public Object getValue() {
        return funcStmt.getFuncSignature().getFuncName().getText()+"<"+ name +">";
    }

    @Override
    public void navigate(boolean requestFocus) {
        funcStmt.navigate(requestFocus);
    }

    @Override
    public boolean canNavigate() {
        return true;
    }

    @Override
    public boolean canNavigateToSource() {
        return true;
    }
}
