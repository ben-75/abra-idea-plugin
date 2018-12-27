package org.abra.language.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.util.PsiTreeUtil;
import org.abra.language.QuplaFileType;
import org.abra.language.QuplaLanguage;
import org.abra.language.module.QuplaModuleManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.*;


public class QuplaFile extends PsiFileBase {

    public QuplaFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, QuplaLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return QuplaFileType.INSTANCE;
    }

    @Override
    public String toString() {
        return "Abra File";
    }

    @Override
    public Icon getIcon(int flags) {
        return super.getIcon(flags);
    }

    @Override
    public void onContentReload() {
        super.onContentReload();
    }

    public String getImportableFilePath(){
        VirtualFile sourceRoot = QuplaPsiImplUtil.getSourceRoot(getProject(),getVirtualFile());
        return getVirtualFile().getPath().substring(sourceRoot.getPath().length()+1,getVirtualFile().getPath().length()-4);
    }

    public List<QuplaFile> getImportTree(){
        QuplaModuleManager quplaModuleManager = getProject().getComponent(QuplaModuleManager.class);
        ArrayList<QuplaFile> resp = new ArrayList<>();
        for(QuplaFile f:quplaModuleManager.getAllVisibleFiles(this)){
            if(f!=null && !this.equals(f))resp.add(f);
        }
        return resp;
    }

    public boolean isImporting(QuplaFile anotherFile) {
        return getImportTree().contains(anotherFile);
    }

    public List<QuplaFuncStmt> findAllFuncStmt(){
        ArrayList<QuplaFuncStmt> resp = new ArrayList<>();
        for (ASTNode stmt : getNode().getChildren(TokenSet.create(QuplaTypes.FUNC_STMT, QuplaTypes.TEMPLATE_STMT))) {
            if(stmt.getElementType()== QuplaTypes.FUNC_STMT) {
                resp.add((QuplaFuncStmt) stmt.getPsi());
            }else{
                for (ASTNode f_stmt : stmt.getChildren(TokenSet.create(QuplaTypes.FUNC_STMT))) {
                    resp.add((QuplaFuncStmt) f_stmt.getPsi());
                }
            }
        }
        return resp;
    }

    public List<QuplaFuncStmt> findAllFuncStmt(String name){
        ArrayList<QuplaFuncStmt> resp = new ArrayList<>();
        for (ASTNode stmt : getNode().getChildren(TokenSet.create(QuplaTypes.FUNC_STMT, QuplaTypes.TEMPLATE_STMT))) {
            if(stmt.getElementType()== QuplaTypes.FUNC_STMT) {
                if(((QuplaFuncStmt)stmt.getPsi()).getFuncSignature().getFuncName().getText().equals(name)){
                    resp.add((QuplaFuncStmt) stmt.getPsi());
                }
            }else{
                for (ASTNode f_stmt : stmt.getChildren(TokenSet.create(QuplaTypes.FUNC_STMT))) {
                    if(((QuplaFuncStmt)f_stmt.getPsi()).getFuncSignature().getFuncName().getText().equals(name)){
                        resp.add((QuplaFuncStmt) f_stmt.getPsi());
                    }
                }
            }
        }
        return resp;
    }

    public List<QuplaFuncNameRef> findAllFuncNameRef(String name) {
        ArrayList<QuplaFuncNameRef> resp = new ArrayList<>();
        for (QuplaFuncNameRef ref : PsiTreeUtil.findChildrenOfAnyType(this, true, QuplaFuncNameRef.class)) {
            if (name == null || ref.getText().equals(name)) {
                resp.add(ref);
            }
        }
        return resp;
    }

    public List<QuplaFuncName> findAllFuncName(String name) {
        ArrayList<QuplaFuncName> resp = new ArrayList<>();
        for (QuplaFuncName ref : PsiTreeUtil.findChildrenOfAnyType(this, true, QuplaFuncName.class)) {
            if (name == null || ref.getText().equals(name)) {
                resp.add(ref);
            }
        }
        return resp;
    }


    public List<QuplaTypeName> findAllTypeName(String name) {
        ArrayList<QuplaTypeName> resp = new ArrayList<>();
        for (QuplaTypeName ref : PsiTreeUtil.findChildrenOfAnyType(this, true, QuplaTypeName.class)) {
            if (name == null || ref.getText().equals(name)) {
                resp.add(ref);
            }
        }
        return resp;
    }

    public Set<PsiElement> computeResolvedReferences() {
        HashSet<PsiElement> resp = new HashSet<>();
        for(QuplaResolvable ref:PsiTreeUtil.findChildrenOfAnyType(this,true, QuplaResolvable.class)){
            PsiReference reference = ref.getReference();
            if(reference instanceof PsiPolyVariantReference){
                ResolveResult[] results = ((PsiPolyVariantReference) reference).multiResolve(false);
                for(ResolveResult resolveResult:results){
                    resp.add(resolveResult.getElement());
                }
            }else {
                PsiElement resolved = reference.resolve();
                if (resolved != null) {
                    resp.add(resolved);
                }
            }
        }
        return resp;
    }

    public Collection<QuplaFuncStmt> getAllFuncStmts() {
        return PsiTreeUtil.findChildrenOfAnyType(this,true, QuplaFuncStmt.class);
    }

    public Collection<QuplaLutStmt> getAllLutStmts() {
        return PsiTreeUtil.findChildrenOfAnyType(this,true, QuplaLutStmt.class);
    }

    public Collection<QuplaTypeStmt> getAllTypeStmts() {
        ArrayList<QuplaTypeStmt> typeStmts = new ArrayList<>();
        for (ASTNode stmt : getNode().getChildren(TokenSet.create(QuplaTypes.TYPE_STMT))) {
           typeStmts.add ((QuplaTypeStmt)stmt.getPsi());
        }
        return typeStmts;
    }

    public QuplaTemplateStmt getTemplate(String name){
        for (ASTNode stmt : getNode().getChildren(TokenSet.create(QuplaTypes.TEMPLATE_STMT))) {
            if(((QuplaTemplateStmt)stmt.getPsi()).getTemplateName().getText().equals(name)){
                return (QuplaTemplateStmt)stmt.getPsi();
            }
        }
        return null;
    }

    public QuplaFuncStmt getStandaloneFunc(String name){
        for (ASTNode stmt : getNode().getChildren(TokenSet.create(QuplaTypes.FUNC_STMT))) {
            if(((QuplaFuncStmt)stmt.getPsi()).getFuncSignature().getFuncName().getText().equals(name)){
                return (QuplaFuncStmt)stmt.getPsi();
            }
        }
        return null;
    }

    public synchronized List<QuplaFile> getAbraFileScope() {
        ArrayList<QuplaFile> resp = new ArrayList<>();
        int analysed = 0;
        resp.add(this);
        while (analysed < resp.size()) {
            QuplaFile item = resp.get(analysed);
            for (ASTNode stmt : item.getNode().getChildren(TokenSet.create(QuplaTypes.IMPORT_STMT))) {
                List<QuplaFile> importedFiles = QuplaPsiImplUtil.getReferencedFiles((QuplaImportStmt) stmt.getPsi());
                if (importedFiles != null) {
                    for (QuplaFile f : importedFiles) {
                        if (!resp.contains(f)) {
                            resp.add(f);
                        }
                    }
                }
            }
            analysed++;
        }

        return resp;
    }

    public Collection<QuplaImportStmt> getImportStmts() {
        return PsiTreeUtil.findChildrenOfAnyType(this,true, QuplaImportStmt.class);
    }
}