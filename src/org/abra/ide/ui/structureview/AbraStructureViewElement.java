package org.abra.ide.ui.structureview;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.SortableTreeElement;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.abra.language.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Compute the structure view for focused Abra file
 */
public class AbraStructureViewElement implements StructureViewTreeElement, SortableTreeElement {

    private final NavigatablePsiElement element;
    private final String typeName;

    public AbraStructureViewElement(NavigatablePsiElement element) {
        this(element,null);
    }

    public AbraStructureViewElement(NavigatablePsiElement element, String typeName) {
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
        if (element instanceof AbraFile) {
            AbraDefinition[] abraDeclarations = PsiTreeUtil.getChildrenOfType(element, AbraDefinition.class);
            if(abraDeclarations!=null) {
                List<TreeElement> treeElements = new ArrayList<TreeElement>(abraDeclarations.length);
                for (AbraDefinition abraDeclaration : abraDeclarations) {
                    treeElements.add(new AbraStructureViewElement(abraDeclaration));
                }
                return treeElements.toArray(new TreeElement[treeElements.size()]);
            }
        } else if (element instanceof AbraTypeStmt && ((AbraTypeStmt)element).getFieldSpecList().size()>0) {
                List<TreeElement> treeElements = new ArrayList<TreeElement>(((AbraTypeStmt)element).getFieldSpecList().size());
                for(AbraFieldSpec field : ((AbraTypeStmt)element).getFieldSpecList()){
                    treeElements.add(new AbraStructureViewElement(field));
                }
                return treeElements.toArray(new TreeElement[treeElements.size()]);
        } else if (element instanceof AbraTemplateStmt && ((AbraTemplateStmt)element).getTypeStmtList().size()+((AbraTemplateStmt)element).getFuncStmtList().size()>0) {
            List<TreeElement> treeElements = new ArrayList<TreeElement>(((AbraTemplateStmt)element).getTypeStmtList().size()+((AbraTemplateStmt)element).getFuncStmtList().size());
            for(AbraTypeStmt typeStmt : ((AbraTemplateStmt)element).getTypeStmtList()){
                treeElements.add(new AbraStructureViewElement(typeStmt));
            }
            for(AbraFuncStmt funcStmt : ((AbraTemplateStmt)element).getFuncStmtList()){
                treeElements.add(new AbraStructureViewElement(funcStmt));
            }
            return treeElements.toArray(new TreeElement[treeElements.size()]);
        } else if (element instanceof AbraUseStmt && ((AbraUseStmt)element).getTypeInstantiationList().size()>0) {
            AbraTemplateName tmplName = (AbraTemplateName) ((AbraUseStmt)element).getTemplateNameRef().getReference().resolve();
            if(tmplName!=null) {
                AbraTemplateStmt tmpl = (AbraTemplateStmt) tmplName.getParent();
                int childCount = ((AbraUseStmt) element).getTypeInstantiationList().size();
                List<TreeElement> treeElements = new ArrayList<TreeElement>(childCount);
                for (AbraTypeInstantiation typeInstanciation : ((AbraUseStmt) element).getTypeInstantiationList()) {
                    treeElements.add(new AbraStructureViewElement(typeInstanciation));
                }
                return treeElements.toArray(new TreeElement[treeElements.size()]);
            }
        } else if (element instanceof AbraTypeInstantiation) {
            AbraTemplateName tmplName = (AbraTemplateName) ((AbraUseStmt)element.getParent()).getTemplateNameRef().getReference().resolve();
            if(tmplName!=null) {
                AbraTemplateStmt tmpl = (AbraTemplateStmt) tmplName.getParent();
                int childCount = tmpl.getFuncStmtList().size();
                List<TreeElement> treeElements = new ArrayList<TreeElement>(childCount);
                for (AbraFuncStmt funcStmt : tmpl.getFuncStmtList()) {
                    treeElements.add(new AbraFunctionInstanciation(funcStmt, (AbraTypeInstantiation) element, tmpl));
                }
                return treeElements.toArray(new TreeElement[treeElements.size()]);
            }
        }
        return EMPTY_ARRAY;
    }
}