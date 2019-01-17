package org.qupla.runtime.debugger;

import com.intellij.debugger.SourcePosition;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import org.qupla.language.module.QuplaModuleManager;

public class QuplaCallStackItem {

    private String modulePath;
    private int lineNumber;  //1 based index (end user friendly)
    private int colNumber;   //1 based index (end user friendly)
    private String expr;
    private String operation;
    private Project project;

    private SourcePosition sourcePosition;

    public QuplaCallStackItem(Project project, String operation, String expr, int lineNumber, int colNumber, String modulePath) {
        this.expr = expr;
        this.lineNumber = lineNumber;
        this.colNumber = colNumber;
        this.modulePath = modulePath;
        this.operation = operation;
        this.project = project;
    }

    public SourcePosition getSourcePosition() {
        if(sourcePosition==null){
            PsiFile quplaFile = project.getComponent(QuplaModuleManager.class).findQuplaFile(modulePath);
            if(quplaFile!=null) {
                Document quplaDoc = PsiDocumentManager.getInstance(project).getDocument(quplaFile);
                int offset = quplaDoc.getLineStartOffset(lineNumber - 1)+colNumber-1;
                sourcePosition = SourcePosition.createFromOffset(quplaFile, offset);
            }
        }
        return sourcePosition;
    }

    @Override
    public String toString() {
        return modulePath==null?expr:"<html>"+expr + "<font color=#808080>&nbsp;&nbsp;("+lineNumber+", " + modulePath.substring(modulePath.lastIndexOf("/")+1) + ")</font></html>";
    }
}
