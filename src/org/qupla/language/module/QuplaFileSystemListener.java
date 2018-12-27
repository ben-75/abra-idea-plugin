package org.qupla.language.module;

import com.intellij.openapi.vfs.VirtualFileCopyEvent;
import com.intellij.openapi.vfs.VirtualFileEvent;
import com.intellij.openapi.vfs.VirtualFileListener;
import com.intellij.openapi.vfs.VirtualFileMoveEvent;
import org.qupla.language.QuplaFileType;
import org.jetbrains.annotations.NotNull;

public class QuplaFileSystemListener implements VirtualFileListener {

    private final QuplaModuleManager quplaModuleManager;

    public QuplaFileSystemListener(QuplaModuleManager quplaModuleManager) {
        this.quplaModuleManager = quplaModuleManager;
    }

    @Override
    public void fileCreated(@NotNull VirtualFileEvent event) {
        if(QuplaFileType.INSTANCE.getDefaultExtension().equals(event.getFile().getExtension()) && isInScope(event)){
            quplaModuleManager.invalidate();
        }
    }

    @Override
    public void fileDeleted(@NotNull VirtualFileEvent event) {
        if(QuplaFileType.INSTANCE.getDefaultExtension().equals(event.getFile().getExtension()) && isInScope(event)){
            quplaModuleManager.invalidate();
        }
    }

    @Override
    public void fileMoved(@NotNull VirtualFileMoveEvent event) {
        if(QuplaFileType.INSTANCE.getDefaultExtension().equals(event.getFile().getExtension()) && isInScope(event)){
            quplaModuleManager.invalidate();
        }
    }

    @Override
    public void fileCopied(@NotNull VirtualFileCopyEvent event) {
        if(event.getFile().getExtension().equals(QuplaFileType.INSTANCE.getDefaultExtension()) && isInScope(event)){
            quplaModuleManager.invalidate();

        }
    }

    private boolean isInScope(VirtualFileEvent event){
        return event.getFile().getPath().startsWith(((QuplaModuleManagerImpl)quplaModuleManager).getWorkingDirPath());
    }
}
