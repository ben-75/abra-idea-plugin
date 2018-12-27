package org.qupla.ide.ui;

import com.intellij.ide.IconLayerProvider;
import com.intellij.openapi.util.Iconable;
import com.intellij.psi.PsiDirectory;
import org.qupla.language.module.QuplaModuleManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class QuplaIconProvider implements IconLayerProvider {


    @Nullable
    @Override
    public Icon getLayerIcon(@NotNull Iconable element, boolean isLocked) {
        if(element instanceof PsiDirectory) {
            QuplaModuleManager quplaModuleManager = ((PsiDirectory) element).getProject().getComponent(QuplaModuleManager.class);
            if (((PsiDirectory) element).getVirtualFile().getPath().endsWith(quplaModuleManager.getQuplaSourceRootPath())) {
                return QuplaIcons.QUPLA_FOLDER_ROOT;
            }else {
                String fullPath = quplaModuleManager.getFullQuplaSourceRootPath();
                if(fullPath!=null)
                    if(((PsiDirectory) element).getVirtualFile().getPath().startsWith(fullPath) && quplaModuleManager.getModule(((PsiDirectory) element).getName())!=null)
                        return QuplaIcons.QUPLA_MODULE_FOLDER;
            }
        }
        return null;
    }

    @NotNull
    @Override
    public String getLayerDescription() {
        return "Qupla source root";
    }
//
//    @Nullable
//    @Override
//    public Icon getIcon(@NotNull VirtualFile file, int flags, @Nullable Project project) {
//        if(file.getPath().endsWith(project.getComponent(QuplaModuleManager.class).getQuplaSourceRootPath())){
//            return QuplaIcons.QUPLA_MODULE_FOLDER;
//        }
//        return null;
//    }


}
