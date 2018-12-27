package org.abra.language.module;

import org.abra.language.psi.AbraFile;

import java.util.Collection;

public interface QuplaModuleManager {

    String getQuplaSourceRootPath();
    String getFullQuplaSourceRootPath();

    void setQuplaSourceRootPath(String quplaSourceRootPath);

    Collection<AbraFile> getAllVisibleFiles(AbraFile file);

    Collection<QuplaModule> allModules();

    void invalidate();

    QuplaModule getModule(String name);

    Collection<AbraFile> getAllAbraFiles();
}
