package org.abra.language.module;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.Text;
import org.abra.language.AbraFileType;
import org.abra.language.psi.AbraFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

@State(name = "QuplaModuleManager")
public class QuplaModuleManagerImpl implements QuplaModuleManager, PersistentStateComponent<QuplaModuleManagerImpl.State> {
    private Project project;
    VirtualFile workingDir;
    private State myState;
    private Map<String, QuplaModule> modules = new HashMap<>();

    static class State {
        public String quplaSourceRootPath;
    }

    public QuplaModuleManagerImpl(Project project) {
        this.project = project;
    }


    private void init(){
        workingDir = LocalFileSystem.getInstance().findFileByIoFile(new File(project.getBasePath(), getQuplaSourceRootPath()));
        if(workingDir.isDirectory()){
            for(VirtualFile potentialModule:workingDir.getChildren()){
                if(potentialModule.isDirectory()){
                    QuplaFileVisitor visitor = new QuplaFileVisitor();
                    visitor.collect(potentialModule);
                    if(visitor.collectedFiles.size()>0){
                        System.out.println("Found Qupla module: "+potentialModule.getName()+ " with "+visitor.collectedFiles.size()+" source files");
                        register(new QuplaModule(project, potentialModule.getName(), potentialModule, visitor.collectedFiles));
                    }
                }
            }
        }else{
            //TODO : proper notification
            System.out.println("working dir not a directory:"+workingDir.getPath());
        }
    }

    private void register(QuplaModule quplaModule){
        modules.put(quplaModule.getName(), quplaModule);
    }

    private QuplaModule findModule(AbraFile file){
        if(!file.isPhysical())return null;
        VirtualFile moduleRoot = file.getVirtualFile().getParent();
        if(!moduleRoot.getPath().contains(getQuplaSourceRootPath()))return null;
        while(!moduleRoot.getParent().getPath().endsWith(getQuplaSourceRootPath())){
            moduleRoot = moduleRoot.getParent();
        }
        return modules.get(moduleRoot.getName());
    }

    public Collection<AbraFile> getAllVisibleFiles(AbraFile file){
        return getAllVisibleFiles(findModule(file));
    }

    public Collection<AbraFile> getAllVisibleFiles(QuplaModule module){
        if(module==null) return Collections.EMPTY_LIST;
        ArrayList<AbraFile> resp = new ArrayList<>();
        for(String imported:module.getImportedModuleNames()){
            QuplaModule importedModule = modules.get(imported);
            if(importedModule!=null){
                resp.addAll(getAllVisibleFiles(importedModule));
            }
        }
        resp.addAll(module.getModuleFiles());
        return resp;
    }

    @Nullable
    @Override
    public State getState() {
        return myState;
    }

    @Override
    public void loadState(@NotNull State state) {
        myState = state;
        init();
    }

    @Override
    public void noStateLoaded() {
        myState = new State();
        setQuplaSourceRootPath("src/main/resources");
        init();
    }

    public Project getProject() {
        return project;
    }

    public String getQuplaSourceRootPath() {
        return myState.quplaSourceRootPath;
    }

    public void setQuplaSourceRootPath(String quplaSourceRootPath) {
        if(quplaSourceRootPath!=null && quplaSourceRootPath.endsWith("/")){
            quplaSourceRootPath = quplaSourceRootPath.substring(0,quplaSourceRootPath.length()-1);
        }

        if(quplaSourceRootPath!=null && quplaSourceRootPath.endsWith("\\")){
            quplaSourceRootPath = quplaSourceRootPath.substring(0,quplaSourceRootPath.length()-1);
        }
        myState.quplaSourceRootPath = quplaSourceRootPath;
    }

    private class QuplaFileVisitor {
        private ArrayList<VirtualFile> collectedFiles = new ArrayList();

        public void collect(VirtualFile directory){
            for(VirtualFile f:directory.getChildren()){
                if(f.isDirectory()){
                    collect(f);
                }else{
                    if(AbraFileType.INSTANCE.getDefaultExtension().equals(f.getExtension())){
                        collectedFiles.add(f);
                    }
                }
            }
        }
    }
}
