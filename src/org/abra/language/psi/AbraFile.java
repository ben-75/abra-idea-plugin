package org.abra.language.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiReference;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.util.PsiTreeUtil;
import org.abra.language.AbraFileType;
import org.abra.language.AbraLanguage;
import org.jetbrains.annotations.NotNull;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;


public class AbraFile extends PsiFileBase {

    public AbraFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, AbraLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return AbraFileType.INSTANCE;
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
        VirtualFile sourceRoot = AbraPsiImplUtil.getSourceRoot(getProject(),getVirtualFile());
        return getVirtualFile().getPath().substring(sourceRoot.getPath().length()+1,getVirtualFile().getPath().length()-5);
    }

    public List<AbraFile> getImportTree(List<AbraFile> importsTree){
        for(ASTNode stmt:getNode().getChildren(TokenSet.create(AbraTypes.IMPORT_STMT))){
            PsiReference[] importedFiles = AbraPsiImplUtil.getReferences((AbraImportStmt) stmt.getPsi());
            for (PsiReference psiRef : importedFiles) {
                AbraFile anAbraFile = (AbraFile)psiRef.resolve();
                if(anAbraFile!=null && !importsTree.contains(anAbraFile)){
                    importsTree.add(anAbraFile);
                    anAbraFile.getImportTree(importsTree);
                }
            }
        }
        return importsTree;
    }

    public boolean isImporting(AbraFile anotherFile) {
        return getImportTree(new ArrayList<>()).contains(anotherFile);
    }

    public List<AbraFuncStmt> findAllFuncStmt(){
        ArrayList<AbraFuncStmt> resp = new ArrayList<>();
        for (ASTNode stmt : getNode().getChildren(TokenSet.create(AbraTypes.FUNC_STMT, AbraTypes.TEMPLATE_STMT))) {
            if(stmt.getElementType()==AbraTypes.FUNC_STMT) {
                resp.add((AbraFuncStmt) stmt.getPsi());
            }else{
                for (ASTNode f_stmt : stmt.getChildren(TokenSet.create(AbraTypes.FUNC_STMT))) {
                    resp.add((AbraFuncStmt) f_stmt.getPsi());
                }
            }
        }
        return resp;
    }

    public List<AbraFuncStmt> findAllFuncStmt(String name){
        ArrayList<AbraFuncStmt> resp = new ArrayList<>();
        for (ASTNode stmt : getNode().getChildren(TokenSet.create(AbraTypes.FUNC_STMT, AbraTypes.TEMPLATE_STMT))) {
            if(stmt.getElementType()==AbraTypes.FUNC_STMT) {
                if(((AbraFuncStmt)stmt.getPsi()).getFuncSignature().getFuncName().getText().equals(name)){
                    resp.add((AbraFuncStmt) stmt.getPsi());
                }
            }else{
                for (ASTNode f_stmt : stmt.getChildren(TokenSet.create(AbraTypes.FUNC_STMT))) {
                    if(((AbraFuncStmt)f_stmt.getPsi()).getFuncSignature().getFuncName().getText().equals(name)){
                        resp.add((AbraFuncStmt) f_stmt.getPsi());
                    }
                }
            }
        }
        return resp;
    }

    public List<AbraFuncNameRef> findAllFuncNameRef(String name){
        ArrayList<AbraFuncNameRef> resp = new ArrayList<>();
        for(AbraFuncNameRef ref:PsiTreeUtil.findChildrenOfAnyType(this,true,AbraFuncNameRef.class)){
            if(ref.getText().equals(name)){
                resp.add(ref);
            }
        }
        return resp;
    }
}