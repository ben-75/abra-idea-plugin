package org.qupla.language.module;

import com.intellij.psi.PsiFile;
import org.qupla.language.psi.QuplaFile;

import java.util.Collection;
import java.util.List;

public interface QuplaModuleManager {

    String getQuplaSourceRootPath();
    String getFullQuplaSourceRootPath();

    void setQuplaSourceRootPath(String quplaSourceRootPath);

    Collection<QuplaFile> getAllVisibleFiles(QuplaFile file);

    Collection<QuplaModule> allModules();

    void invalidate();

    QuplaModule getModule(String name);

    Collection<QuplaFile> getAllQuplaFiles();

    QuplaFile findQuplaFile(String modulePath);

    List<QuplaModule> getImportedModules(QuplaModule module);
}
