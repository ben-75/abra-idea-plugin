package org.qupla.language.module;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.*;
import com.intellij.util.concurrency.AppExecutorUtil;
import org.qupla.language.QuplaFileType;
import org.qupla.language.psi.QuplaFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.concurrent.TimeUnit.SECONDS;

@State(name = "QuplaModuleManager")
public class QuplaModuleManagerImpl implements QuplaModuleManager, PersistentStateComponent<QuplaModuleManagerImpl.State> {
    private Project project;
    VirtualFile workingDir;
    private State myState;
    private Map<String, QuplaModule> modules = new HashMap<>();
    private boolean startupNotificationRequired = true;
    private boolean isValid = false;
    private boolean workingDirHasChanged = false;

    public String getWorkingDirPath() {
        return workingDir==null?"null":workingDir.getPath();
    }

    static class State {
        public String quplaSourceRootPath;
    }

    public QuplaModuleManagerImpl(Project project) {
        this.project = project;
    }


    @Override
    public synchronized void invalidate() {
        isValid = false;
    }

    @Override
    public synchronized Collection<QuplaFile> getAllVisibleFiles(QuplaFile file){
        checkValidity();
        return getAllVisibleFiles(findModule(file));
    }

    @Override
    public synchronized Collection<QuplaFile> getAllQuplaFiles(){
        checkValidity();
        ArrayList<QuplaFile> resp = new ArrayList<>();
        for(QuplaModule module:modules.values()){
            resp.addAll(module.getModuleFiles());
        }
        return resp;
    }

    @Override
    public synchronized Collection<QuplaModule> allModules() {
        checkValidity();
        return modules.values();
    }

    @Override
    public QuplaModule getModule(String name) {
        checkValidity();
        return modules.get(name);
    }

    @Nullable
    @Override
    public synchronized State getState() {
        return myState;
    }

    @Override
    public synchronized void loadState(@NotNull State state) {
        myState = state;
        init();
    }

    @Override
    public synchronized void noStateLoaded() {
        myState = new State();
        setQuplaSourceRootPath("src/main/resources");
        init();
    }

    public Project getProject() {
        return project;
    }

    public synchronized String getQuplaSourceRootPath() {
        return myState.quplaSourceRootPath;
    }

    public String getFullQuplaSourceRootPath(){
        if(workingDir!=null)return workingDir.getPath();
        return  null;
    }
    public synchronized void setQuplaSourceRootPath(String quplaSourceRootPath) {
        if(quplaSourceRootPath!=null && quplaSourceRootPath.endsWith("/")){
            quplaSourceRootPath = quplaSourceRootPath.substring(0,quplaSourceRootPath.length()-1);
        }

        if(quplaSourceRootPath!=null && quplaSourceRootPath.endsWith("\\")){
            quplaSourceRootPath = quplaSourceRootPath.substring(0,quplaSourceRootPath.length()-1);
        }
        workingDirHasChanged = myState.quplaSourceRootPath==null?(quplaSourceRootPath!=null):(!myState.quplaSourceRootPath.equals(quplaSourceRootPath));
        myState.quplaSourceRootPath = quplaSourceRootPath;
        if(workingDirHasChanged)isValid=false;
    }

    private synchronized void checkValidity(){
        if(isValid && !workingDirHasChanged)return;
        modules.clear();
        if(workingDirHasChanged){
            if (!findWorkingDir()) return;
        }
        populateModuleMap();
        isValid=true;
    }

    private boolean findWorkingDir() {
        workingDir = LocalFileSystem.getInstance().findFileByIoFile(new File(project.getBasePath(), getQuplaSourceRootPath()));
        if (workingDir == null || !workingDir.exists() || !workingDir.isDirectory()) {
            new Notification(
                    "Qupla",
                    "Qupla sources",
                    "The root directory for qupla sources cannot be found.<br>Expected location is '" + new File(project.getBasePath(), getQuplaSourceRootPath()).getAbsolutePath() + "'",
                    NotificationType.WARNING).notify(project);
            return false;
        }

        workingDirHasChanged = false;
        return true;
    }

    private synchronized void init(){
        if(!new QuplaFileDetector().detect())return;
        if (!findWorkingDir()) return;
        populateModuleMap();
        isValid = true;

        if(startupNotificationRequired){
            VirtualFileManager.getInstance().addVirtualFileListener(new QuplaFileSystemListener(this));


            AppExecutorUtil.getAppScheduledExecutorService().scheduleWithFixedDelay(new Runnable() {
                @Override
                public void run() {
                    synchronized (QuplaModuleManagerImpl.this){
                        populateModuleMap();
                    }
                }
            }, 15, 10L , SECONDS);


            startupNotificationRequired = false;
            StringBuilder modulesList = new StringBuilder();
            for(String s:modules.keySet()){
                modulesList.append(s).append(", ");
            }
            new Notification(
                    "Qupla",
                    "Qupla sources",
                    "Qupla modules detected: "+modulesList.toString().substring(0,modulesList.length()-2),
                    NotificationType.INFORMATION).notify(project);
        }
    }

    private void populateModuleMap() {
        int fileCount = 0;
        for(VirtualFile potentialModule:workingDir.getChildren()){
            if(potentialModule.isDirectory()){
                ArrayList<VirtualFile> quplaFiles = new QuplaFileVisitor().collect(potentialModule);
                if(quplaFiles.size()>0){
                    register(new QuplaModule(project, potentialModule.getName(), potentialModule, quplaFiles));
                }
            }
        }
    }

    private synchronized void register(QuplaModule quplaModule){
        modules.put(quplaModule.getName(), quplaModule);
    }

    private synchronized QuplaModule findModule(QuplaFile file){
        if(!file.isPhysical())return null;
        VirtualFile moduleRoot = file.getVirtualFile().getParent();
        if(!moduleRoot.getPath().contains(getQuplaSourceRootPath()))return null;
        while(!moduleRoot.getParent().getPath().endsWith(getQuplaSourceRootPath())){
            moduleRoot = moduleRoot.getParent();
        }
        return modules.get(moduleRoot.getName());
    }

    private synchronized Collection<QuplaFile> getAllVisibleFiles(QuplaModule module){
        if(module==null) return Collections.EMPTY_LIST;
        ArrayList<QuplaFile> resp = new ArrayList<>();
        for(String imported:module.getImportedModuleNames()){
            QuplaModule importedModule = modules.get(imported);
            if(importedModule!=null){
                resp.addAll(getAllVisibleFiles(importedModule));
            }
        }
        resp.addAll(module.getModuleFiles());
        return resp;
    }

    private class QuplaFileVisitor {
        public ArrayList<VirtualFile> collect(VirtualFile directory){
            final ArrayList<VirtualFile> collectedFiles = new ArrayList();
            VfsUtilCore.visitChildrenRecursively(directory, new VirtualFileVisitor<Object>() {
                @Override
                public boolean visitFile(@NotNull VirtualFile file) {
                    if(QuplaFileType.INSTANCE.getDefaultExtension().equals(file.getExtension())){
                        collectedFiles.add(file);
                    }
                    return true;
                }
            });
            return collectedFiles;
        }
    }

    private class QuplaFileDetector {

        public boolean detect(){
            if(project.getBaseDir()==null)return false;
            final AtomicBoolean qplFound = new AtomicBoolean(false);
            VfsUtilCore.visitChildrenRecursively(project.getBaseDir(), new VirtualFileVisitor<Object>() {
                @Override
                public boolean visitFile(@NotNull VirtualFile file) {
                    if(QuplaFileType.INSTANCE.getDefaultExtension().equals(file.getExtension())){
                        qplFound.set(true);
                    }
                    return !qplFound.get();
                }
            });
            return qplFound.get();
        }
    }


}
