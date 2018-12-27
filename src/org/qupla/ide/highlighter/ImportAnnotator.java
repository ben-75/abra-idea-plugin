package org.qupla.ide.highlighter;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import org.qupla.language.psi.QuplaFile;
import org.qupla.language.psi.QuplaImportStmt;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ImportAnnotator  implements Annotator {

    private Map<QuplaFile, Set<PsiElement>> refCache = new HashMap<>();

    @Override
    public void annotate(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {
        if(element instanceof QuplaImportStmt){
            PsiReference[] references = element.getReferences();
            if(references.length==1) {
                QuplaFile rootImport = (QuplaFile) references[0].resolve();
                Set<PsiElement> resolvedReferences = refCache.get(element.getContainingFile());
                if (resolvedReferences == null) {
                    resolvedReferences = ((QuplaFile) element.getContainingFile()).computeResolvedReferences();
                    refCache.put((QuplaFile) element.getContainingFile(), resolvedReferences);
                }
                boolean usedImport = false;
                boolean rootImportUsed = false;
                List<QuplaFile> importTree = ((QuplaFile) element.getContainingFile()).getImportTree();
                for (PsiElement e : resolvedReferences) {
                    if (importTree.contains(e.getContainingFile())) {
                        usedImport = true;
                        rootImportUsed = e.getContainingFile().equals(rootImport);
                        if (rootImportUsed) break;
                    }
                }

                if (!usedImport) {
                    int endOffset = element.getTextRange().getEndOffset();
                    TextRange range = new TextRange(element.getTextRange().getStartOffset(), endOffset);
                    holder.createWarningAnnotation(range, "Unused import");
                } else {
                    if (!rootImportUsed) {
                        int endOffset = element.getTextRange().getEndOffset();
                        TextRange range = new TextRange(element.getTextRange().getStartOffset(), endOffset);
                        holder.createWarningAnnotation(range, "Import not used directly");
                    }
                }
            }
        }
    }
}
