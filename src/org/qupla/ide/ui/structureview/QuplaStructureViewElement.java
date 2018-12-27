package org.qupla.ide.ui.structureview;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.SortableTreeElement;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.qupla.language.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class QuplaStructureViewElement implements StructureViewTreeElement, SortableTreeElement {
    private final NavigatablePsiElement element;
    private final String typeName;

    public QuplaStructureViewElement(NavigatablePsiElement element) {
        this(element,null);
    }
    public QuplaStructureViewElement(NavigatablePsiElement element, String typeName) {
        this.element = element;
        this.typeName = typeName;
    }
    @Override
    public Object getValue() {
        return element;
    }

    @Override
    public void navigate(boolean requestFocus) {
        element.navigate(requestFocus);
    }

    @Override
    public boolean canNavigate() {
        return element.canNavigate();
    }

    @Override
    public boolean canNavigateToSource() {
        return element.canNavigateToSource();
    }

    @NotNull
    @Override
    public String getAlphaSortKey() {
        String name = element.getName();
        return name != null ? name : "";
    }

    @NotNull
    @Override
    public ItemPresentation getPresentation() {
        ItemPresentation presentation = element.getPresentation();
        return presentation != null ? presentation : new PresentationData();
    }

    @NotNull
    @Override
    public TreeElement[] getChildren() {
        if (element instanceof QuplaFile) {
            QuplaDefinition[] quplaDeclarations = PsiTreeUtil.getChildrenOfType(element, QuplaDefinition.class);
            if(quplaDeclarations!=null) {
                List<TreeElement> treeElements = new ArrayList<TreeElement>(quplaDeclarations.length);
                for (QuplaDefinition quplaDefinition : quplaDeclarations) {
                    treeElements.add(new QuplaStructureViewElement(quplaDefinition));
                }
                return treeElements.toArray(new TreeElement[treeElements.size()]);
            }
        } else if (element instanceof QuplaTypeStmt && ((QuplaTypeStmt)element).getFieldSpecList().size()>0) {
                List<TreeElement> treeElements = new ArrayList<TreeElement>(((QuplaTypeStmt)element).getFieldSpecList().size());
                for(QuplaFieldSpec field : ((QuplaTypeStmt)element).getFieldSpecList()){
                    treeElements.add(new QuplaStructureViewElement(field));
                }
                return treeElements.toArray(new TreeElement[treeElements.size()]);
        } else if (element instanceof QuplaTemplateStmt && ((QuplaTemplateStmt)element).getTypeStmtList().size()+((QuplaTemplateStmt)element).getFuncStmtList().size()>0) {
            List<TreeElement> treeElements = new ArrayList<TreeElement>(((QuplaTemplateStmt)element).getTypeStmtList().size()+((QuplaTemplateStmt)element).getFuncStmtList().size());
            for(QuplaTypeStmt typeStmt : ((QuplaTemplateStmt)element).getTypeStmtList()){
                treeElements.add(new QuplaStructureViewElement(typeStmt));
            }
            for(QuplaFuncStmt funcStmt : ((QuplaTemplateStmt)element).getFuncStmtList()){
                treeElements.add(new QuplaStructureViewElement(funcStmt));
            }
            return treeElements.toArray(new TreeElement[treeElements.size()]);
        } else if (element instanceof QuplaUseStmt && ((QuplaUseStmt)element).getTypeInstantiationList().size()>0) {
            QuplaTemplateName tmplName = (QuplaTemplateName) ((QuplaUseStmt)element).getTemplateNameRef().getReference().resolve();
            if(tmplName!=null) {
                QuplaTemplateStmt tmpl = (QuplaTemplateStmt) tmplName.getParent();
                int childCount = ((QuplaUseStmt) element).getTypeInstantiationList().size();
                List<TreeElement> treeElements = new ArrayList<TreeElement>(childCount);
                for (QuplaTypeInstantiation typeInstanciation : ((QuplaUseStmt) element).getTypeInstantiationList()) {
                    treeElements.add(new QuplaStructureViewElement(typeInstanciation));
                }
                return treeElements.toArray(new TreeElement[treeElements.size()]);
            }
        } else if (element instanceof QuplaTypeInstantiation) {
            QuplaTemplateName tmplName = (QuplaTemplateName) ((QuplaUseStmt)element.getParent()).getTemplateNameRef().getReference().resolve();
            if(tmplName!=null) {
                QuplaTemplateStmt tmpl = (QuplaTemplateStmt) tmplName.getParent();
                int childCount = tmpl.getFuncStmtList().size();
                List<TreeElement> treeElements = new ArrayList<TreeElement>(childCount);
                for (QuplaFuncStmt funcStmt : tmpl.getFuncStmtList()) {
                    treeElements.add(new QuplaFunctionInstanciation(funcStmt, (QuplaTypeInstantiation) element, tmpl));
                }
                return treeElements.toArray(new TreeElement[treeElements.size()]);
            }
        }
        return EMPTY_ARRAY;
    }
}