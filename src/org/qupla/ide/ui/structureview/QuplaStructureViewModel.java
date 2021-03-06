package org.qupla.ide.ui.structureview;

import com.intellij.ide.structureView.StructureViewModel;
import com.intellij.ide.structureView.StructureViewModelBase;
import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.Sorter;
import com.intellij.psi.PsiFile;
import org.qupla.language.psi.QuplaFile;
import org.jetbrains.annotations.NotNull;

public class QuplaStructureViewModel extends StructureViewModelBase implements
        StructureViewModel.ElementInfoProvider {
    public QuplaStructureViewModel(PsiFile psiFile) {
        super(psiFile, new QuplaStructureViewElement(psiFile));
    }

    @NotNull
    public Sorter[] getSorters() {
        return new Sorter[]{Sorter.ALPHA_SORTER};
    }


    @Override
    public boolean isAlwaysShowsPlus(StructureViewTreeElement element) {
        return false;
    }

    @Override
    public boolean isAlwaysLeaf(StructureViewTreeElement element) {
        return element instanceof QuplaFile;
    }
}