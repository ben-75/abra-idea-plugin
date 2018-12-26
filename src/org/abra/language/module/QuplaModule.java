package org.abra.language.module;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.SmartPointerManager;
import com.intellij.psi.SmartPsiElementPointer;
import org.abra.language.psi.AbraFile;
import org.abra.language.psi.AbraImportStmt;

import java.util.*;
import java.util.stream.Collectors;

public class QuplaModule {

    private Project project;
    private String name;
    private VirtualFile root;
    private List<SmartPsiElementPointer<AbraFile>> abraFiles;
    private Set<String> importedModuleNames = new HashSet<>();

    public QuplaModule(Project project, String name, VirtualFile root, List<VirtualFile> sources){
        this.project = project;
        this.name = name;
        this.root = root;
        ApplicationManager.getApplication().runReadAction(new Runnable() {
            @Override
            public void run() {
                ArrayList<AbraFile> files = new ArrayList<>(sources.size());
                for(VirtualFile src:sources){
                    AbraFile abraFile = (AbraFile) PsiManager.getInstance(project).findFile(src);
                    if(abraFile!=null){
                        files.add(abraFile);
                        Collection<AbraImportStmt> imports = abraFile.getImportStmts();
                        for(AbraImportStmt stmt:imports)importedModuleNames.add(stmt.getModuleName().getText());
                    }
                }
                abraFiles = wrap(files);
            }
        });
    }

    public List<AbraFile> getModuleFiles(){
        return unwrap(abraFiles);
    }

    private List<SmartPsiElementPointer<AbraFile>> wrap(List<AbraFile> l){
        return l.stream()
                .map( f -> SmartPointerManager.getInstance(project).createSmartPsiElementPointer(f) )
                .collect(Collectors.toList());
    }
    private List<AbraFile> unwrap(List<SmartPsiElementPointer<AbraFile>> l){
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
