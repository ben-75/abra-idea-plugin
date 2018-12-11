package org.abra.ide.highlighter;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import org.abra.language.psi.AbraFile;
import org.abra.language.psi.AbraImportStmt;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Check that import statement are used.
 * This can produce 2 kinds of warning :
 * 1. import is not used at all
 * 2. import is not used directly (but due to the recusive imports in Abra : some code imported recursively is used)
 *
 * Note that wildcard import statement aren't checked.
 */
public class ImportAnnotator  implements Annotator {

    private Map<AbraFile, Set<PsiElement>> refCache = new HashMap<>();

    @Override
    public void annotate(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {
        if(element instanceof AbraImportStmt){
            PsiReference[] references = element.getReferences();
            if(references.length==1) {
                AbraFile rootImport = (AbraFile) references[0].resolve();
                Set<PsiElement> resolvedReferences = refCache.get(element.getContainingFile());
                if (resolvedReferences == null) {
                    resolvedReferences = ((AbraFile) element.getContainingFile()).computeResolvedReferences();
                    refCache.put((AbraFile) element.getContainingFile(), resolvedReferences);
                }
                boolean usedImport = false;
                boolean rootImportUsed = false;
                List<AbraFile> importTree = ((AbraFile) element.getContainingFile()).getImportTree(new ArrayList<>());
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
