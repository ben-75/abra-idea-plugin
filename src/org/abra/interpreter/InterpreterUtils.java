package org.abra.interpreter;

import com.intellij.openapi.editor.Document;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

public class InterpreterUtils {


    public static String getErrorLocationString(PsiElement element){
        return "File: "+element.getContainingFile().getVirtualFile().getPath()+" at line "+getLineNumber(element);
    }

    public static int getLineNumber(PsiElement element){
        PsiFile containingFile = element.getContainingFile();
        FileViewProvider fileViewProvider = containingFile.getViewProvider();
        Document document = fileViewProvider.getDocument();
        int textOffset = element.getTextOffset();
        return document.getLineNumber(textOffset);
    }
}
