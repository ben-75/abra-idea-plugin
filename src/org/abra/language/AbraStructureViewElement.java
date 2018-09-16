package org.abra.language;

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

public class AbraStructureViewElement implements StructureViewTreeElement, SortableTreeElement {
    private NavigatablePsiElement element;

    public AbraStructureViewElement(NavigatablePsiElement element) {
        this.element = element;
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

    @Override
    public TreeElement[] getChildren() {
        if (element instanceof AbraFile) {
            AbraDefinition[] abraDeclarations = PsiTreeUtil.getChildrenOfType(element, AbraDefinition.class);
            if(abraDeclarations!=null) {
                List<TreeElement> treeElements = new ArrayList<TreeElement>(abraDeclarations.length);
                for (AbraDefinition abraDeclaration : abraDeclarations) {
                    treeElements.add(new AbraStructureViewElement((AbraDefinition) abraDeclaration));
                }
                return treeElements.toArray(new TreeElement[treeElements.size()]);
            }
//        } else if (element instanceof AbraTypedef && ((AbraTypedef)element).getTritstructure()!=null) {
//                List<TreeElement> treeElements = new ArrayList<TreeElement>(((AbraTypedef)element).getTritstructure().getTritfields().getFielddefList().size());
//                for(AbraFielddef field : ((AbraTypedef)element).getTritstructure().getTritfields().getFielddefList()){
//                    treeElements.add(new AbraStructureViewElement((AbraFielddef) field));
//                }
//                return treeElements.toArray(new TreeElement[treeElements.size()]);
        }
        return EMPTY_ARRAY;
    }
}