package org.abra.language.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.util.PsiTreeUtil;
import org.abra.language.AbraFileType;
import org.abra.language.AbraLanguage;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.*;
import java.util.stream.Collectors;


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

    private List<SmartPsiElementPointer<AbraFile>> cache;
    public List<AbraFile> getImportTree(List<AbraFile> importsTree){
        if(true){
            List<AbraFile> scope = getAbraFileScope();
            return scope.subList(1,scope.size());
        }
        if(cache!=null)return unwrap(cache);
        cache = wrap(_getImportTree(importsTree));
        //log.info("Caching "+cache.size()+" imported files for "+getName());
        return importsTree;
    }

    private List<SmartPsiElementPointer<AbraFile>> wrap(List<AbraFile> l){
        return l.stream()
                .map( f -> SmartPointerManager.getInstance(getProject()).createSmartPsiElementPointer(f) )
                .collect(Collectors.toList());
    }
    private List<AbraFile> unwrap(List<SmartPsiElementPointer<AbraFile>> l){
        return l.stream()
                .map( f -> f.getElement() )
                .collect(Collectors.toList());
    }

    public List<AbraFile> _getImportTree(List<AbraFile> importsTree){
        for(ASTNode stmt:getNode().getChildren(TokenSet.create(AbraTypes.IMPORT_STMT))){
            PsiReference[] importedFiles = stmt.getPsi().getReferences();
            for (PsiReference psiRef : importedFiles) {
                AbraFile anAbraFile = (AbraFile)psiRef.resolve();
                if(anAbraFile!=null){
                    if(!importsTree.contains(anAbraFile)) {
                        importsTree.add(anAbraFile);
                        anAbraFile._getImportTree(importsTree);
                    }
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

    public List<AbraFuncNameRef> findAllFuncNameRef(String name) {
        ArrayList<AbraFuncNameRef> resp = new ArrayList<>();
        for (AbraFuncNameRef ref : PsiTreeUtil.findChildrenOfAnyType(this, true, AbraFuncNameRef.class)) {
            if (name == null || ref.getText().equals(name)) {
                resp.add(ref);
            }
        }
        return resp;
    }

    public List<AbraFuncName> findAllFuncName(String name) {
        ArrayList<AbraFuncName> resp = new ArrayList<>();
        for (AbraFuncName ref : PsiTreeUtil.findChildrenOfAnyType(this, true, AbraFuncName.class)) {
            if (name == null || ref.getText().equals(name)) {
                resp.add(ref);
            }
        }
        return resp;
    }


    public List<AbraTypeName> findAllTypeName(String name) {
        ArrayList<AbraTypeName> resp = new ArrayList<>();
        for (AbraTypeName ref : PsiTreeUtil.findChildrenOfAnyType(this, true, AbraTypeName.class)) {
            if (name == null || ref.getText().equals(name)) {
                resp.add(ref);
            }
        }
        return resp;
    }

    public Set<PsiElement> computeResolvedReferences() {
        HashSet<PsiElement> resp = new HashSet<>();
        for(AbraResolvable ref:PsiTreeUtil.findChildrenOfAnyType(this,true,AbraResolvable.class)){
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

    public Collection<AbraFuncStmt> getAllFuncStmts() {
        return PsiTreeUtil.findChildrenOfAnyType(this,true,AbraFuncStmt.class);
    }

    public Collection<AbraLutStmt> getAllLutStmts() {
        return PsiTreeUtil.findChildrenOfAnyType(this,true,AbraLutStmt.class);
    }

    public AbraTemplateStmt getTemplate(String name){
        for (ASTNode stmt : getNode().getChildren(TokenSet.create(AbraTypes.TEMPLATE_STMT))) {
            if(((AbraTemplateStmt)stmt.getPsi()).getTemplateName().getText().equals(name)){
                return (AbraTemplateStmt)stmt.getPsi();
            }
        }
        return null;
    }

    public AbraFuncStmt getStandaloneFunc(String name){
        for (ASTNode stmt : getNode().getChildren(TokenSet.create(AbraTypes.FUNC_STMT))) {
            if(((AbraFuncStmt)stmt.getPsi()).getFuncSignature().getFuncName().getText().equals(name)){
                return (AbraFuncStmt)stmt.getPsi();
            }
        }
        return null;
    }

    public void invalidateCache() {
        cache = null;
    }


    private static final Key KEY_ABRA_SCOPE = new Key("AbraScope");

    public synchronized List<AbraFile> getAbraFileScope() {
//        ArrayList<AbraFile> resp = (ArrayList<AbraFile>) getUserData(KEY_ABRA_SCOPE);
//        if (resp == null) {
            ArrayList<AbraFile> resp = new ArrayList<>();
            int analysed = 0;
            resp.add(this);
            while (analysed < resp.size()) {
                AbraFile item = resp.get(analysed);
                for (ASTNode stmt : item.getNode().getChildren(TokenSet.create(AbraTypes.IMPORT_STMT))) {
                    List<AbraFile> importedFiles = AbraPsiImplUtil.getReferencedFiles((AbraImportStmt) stmt.getPsi());//.getReferencedFiles();
//                    List<AbraFile> importedFiles = ((AbraImportStmt) stmt.getPsi()).getReferencedFiles();//.getReferencedFiles();
                    if (importedFiles != null) {
                        for (AbraFile f : importedFiles) {
                            if (!resp.contains(f)) {
                                resp.add(f);
                            }
                        }
                    }
                }
                analysed++;
            }

//            putUserData(KEY_ABRA_SCOPE, resp);
//        }

//        StringBuilder sb = new StringBuilder("Scope for " + getShortName());
//        for (AbraFile f : resp) {
//            sb.append(",").append(f.getShortName());
//        }
//
//        System.out.println(sb);

        return resp;
    }

    private String getShortName() {
        return getImportableFilePath();
    }
}