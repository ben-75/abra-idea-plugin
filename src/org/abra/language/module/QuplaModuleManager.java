package org.abra.language.module;

import com.intellij.openapi.components.PersistentStateComponent;
import org.abra.language.psi.AbraFile;

import java.util.Collection;
import java.util.Set;

public interface QuplaModuleManager {

    String getQuplaSourceRootPath();
    String getFullQuplaSourceRootPath();

    void setQuplaSourceRootPath(String quplaSourceRootPath);

    Collection<AbraFile> getAllVisibleFiles(AbraFile file);

    Collection<QuplaModule> allModules();

    void invalidate();

    QuplaModule getModule(String name);
}
