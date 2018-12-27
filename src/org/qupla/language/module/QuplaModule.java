package org.qupla.language.module;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.SmartPointerManager;
import com.intellij.psi.SmartPsiElementPointer;
import org.qupla.language.psi.QuplaFile;
import org.qupla.language.psi.QuplaImportStmt;

import java.util.*;
import java.util.stream.Collectors;

public class QuplaModule {

    private Project project;
    private String name;
    private VirtualFile root;
    private List<SmartPsiElementPointer<QuplaFile>> abraFiles;
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
                    QuplaFile quplaFile = (QuplaFile) PsiManager.getInstance(project).findFile(src);
                    if(quplaFile !=null){
                        files.add(quplaFile);
                        Collection<QuplaImportStmt> imports = quplaFile.getImportStmts();
                        for(QuplaImportStmt stmt:imports)importedModuleNames.add(stmt.getModuleName().getText());
                    }
                }
                abraFiles = wrap(files);
            }
        });
    }

    public List<QuplaFile> getModuleFiles(){
        return unwrap(abraFiles);
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
}
