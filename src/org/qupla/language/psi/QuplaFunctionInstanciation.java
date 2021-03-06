package org.qupla.language.psi;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import org.qupla.ide.ui.QuplaIcons;
import org.qupla.language.UnresolvableTokenException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.HashMap;

public class QuplaFunctionInstanciation implements StructureViewTreeElement {

    private final QuplaFuncStmt funcStmt;
    private final String name;
    private final String locationString;
    public QuplaFunctionInstanciation(QuplaFuncStmt funcStmt, QuplaTypeInstantiation typeInstantiation, QuplaTemplateStmt templateStmt) {
        this.funcStmt = funcStmt;
        this.name = typeInstantiation.getText().substring(1,typeInstantiation.getTextLength()-1);
        QuplaTypeName typeName = (QuplaTypeName) typeInstantiation.getTypeNameRefList().get(0).getReference().resolve();
        if(typeName!=null){
            HashMap<String,Integer> map = new HashMap<>();
            map.put(templateStmt.getPlaceHolderTypeNameList().get(0).getText(), typeName.getResolvedSize());
            for(QuplaTypeStmt localTypeStmt:templateStmt.getTypeStmtList()){
                if(localTypeStmt.getTypeSize()!=null)
                    map.put(localTypeStmt.getTypeName().getText(), QuplaPsiImplUtil.getResolvedSize2(localTypeStmt.getTypeSize().getConstExpr(),map,templateStmt));
            }
            StringBuilder sb = new StringBuilder("( ");


            int i=0;
            for(QuplaFuncParameter p:funcStmt.getFuncSignature().getFuncParameterList()){
                try{
                    int sz = QuplaPsiImplUtil.getResolvedSize2(p.getTypeOrPlaceHolderNameRef(),map,templateStmt);
                    sb.append(sz<=0?p.getTypeOrPlaceHolderNameRef().getText():sz);
                }catch (UnresolvableTokenException e){
                    sb.append("[?]");
                }
                i++;
                if(i<funcStmt.getFuncSignature().getFuncParameterList().size())sb.append(" , ");
            }
            sb.append(" ) -> ");
            if(funcStmt.getFuncSignature().getTypeOrPlaceHolderNameRefList().size()>0){
                try{
                    int sz = QuplaPsiImplUtil.getResolvedSize2(funcStmt.getFuncSignature().getTypeOrPlaceHolderNameRefList().get(0),map,templateStmt);
                    sb.append(sz<=0?funcStmt.getFuncSignature().getTypeOrPlaceHolderNameRefList().get(0).getText():sz);
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
                return QuplaIcons.FUNCTION;
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
