package org.qupla.language.module;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.SmartPointerManager;
import com.intellij.psi.SmartPsiElementPointer;
import com.intellij.psi.tree.TokenSet;
import org.qupla.language.psi.*;
import org.qupla.runtime.interpreter.QuplaInterpreterSettingsEditor;

import java.util.*;
import java.util.stream.Collectors;

public class QuplaModule {

    private Project project;
    private String name;
    private VirtualFile root;
    private List<SmartPsiElementPointer<QuplaFile>> quplaFiles;
    private Set<String> importedModuleNames = new HashSet<>();

    public QuplaModule(Project project, String name, VirtualFile root, List<VirtualFile> sources){
        this.project = project;
        this.name = name;
        this.root = root;
        ApplicationManager.getApplication().runReadAction(new Runnable() {
            @Override
            public void run() {
                ArrayList<QuplaFile> files = new ArrayList<>(sources.size());
                for(VirtualFile src:sources){
                    PsiFile psiFile = PsiManager.getInstance(project).findFile(src);
                    if(psiFile instanceof QuplaFile){
                        files.add((QuplaFile) psiFile);
                        Collection<QuplaImportStmt> imports = ((QuplaFile) psiFile).getImportStmts();
                        for(QuplaImportStmt stmt:imports)importedModuleNames.add(stmt.getModuleName().getText());
                    }
                }
                quplaFiles = wrap(files);
            }
        });
    }

    public Project getProject() {
        return project;
    }

    public List<QuplaFile> getModuleFiles(){
        return unwrap(quplaFiles);
    }

    private List<SmartPsiElementPointer<QuplaFile>> wrap(List<QuplaFile> l){
        return l.stream()
                .map( f -> SmartPointerManager.getInstance(project).createSmartPsiElementPointer(f) )
                .collect(Collectors.toList());
    }
    private List<QuplaFile> unwrap(List<SmartPsiElementPointer<QuplaFile>> l){
        return l.stream()
                .map( f -> f.getElement() )
                .collect(Collectors.toList());
    }

    public String getName() {
        return name;
    }

    public Set<String> getImportedModuleNames() {
        return importedModuleNames;
    }

    public QuplaFuncStmt findFuncStmt(String tmpl_name, String func_name) {
        if(tmpl_name==null){
            for(QuplaFile f:getModuleFiles()){
                if(f!=null) {
                    QuplaFuncStmt func = f.getStandaloneFunc(func_name);
                    if (func != null) return func;
                }
            }
        }else{
            for(QuplaFile f:getModuleFiles()){
                if(f!=null){
                    QuplaTemplateStmt tmpl = f.getTemplate(tmpl_name);
                    if (tmpl != null) {
                        for (ASTNode stmt : tmpl.getNode().getChildren(TokenSet.create(QuplaTypes.FUNC_STMT))) {
                            if (((QuplaFuncStmt) stmt.getPsi()).getFuncSignature().getFuncName().getText().equals(func_name)) {
                                return (QuplaFuncStmt) stmt.getPsi();
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public List<QuplaFuncStmt> findAllFuncStmt() {
        ArrayList<QuplaFuncStmt> funcStmts = new ArrayList<>();
        for(QuplaFile quplaFile:getModuleFiles()) {
            if(quplaFile!=null){
                funcStmts.addAll(quplaFile.findAllFuncStmt());
            }
        }
        return funcStmts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QuplaModule that = (QuplaModule) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
