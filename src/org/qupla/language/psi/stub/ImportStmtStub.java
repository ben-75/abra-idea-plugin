package org.qupla.language.psi.stub;

import com.intellij.psi.stubs.StubElement;
import org.qupla.language.psi.QuplaImportStmt;

public interface ImportStmtStub extends StubElement<QuplaImportStmt> {
    String getModule();
}
