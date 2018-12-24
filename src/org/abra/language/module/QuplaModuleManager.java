package org.abra.language.module;

import com.intellij.openapi.components.PersistentStateComponent;
import org.abra.language.psi.AbraFile;

import java.util.Collection;

public interface QuplaModuleManager {

    String getQuplaSourceRootPath();

    void setQuplaSourceRootPath(String quplaSourceRootPath);

    Collection<AbraFile> getAllVisibleFiles(AbraFile file);
}
