package org.qupla.language.module;

import org.qupla.language.psi.QuplaFile;

import java.util.Collection;

public interface QuplaModuleManager {

    String getQuplaSourceRootPath();
    String getFullQuplaSourceRootPath();

    void setQuplaSourceRootPath(String quplaSourceRootPath);

    Collection<QuplaFile> getAllVisibleFiles(QuplaFile file);

    Collection<QuplaModule> allModules();

    void invalidate();

    QuplaModule getModule(String name);

    Collection<QuplaFile> getAllQuplaFiles();
}
