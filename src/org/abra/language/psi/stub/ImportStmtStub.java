package org.abra.language.psi.stub;

import com.intellij.psi.stubs.StubElement;
import org.abra.language.psi.QuplaImportStmt;

public interface ImportStmtStub extends StubElement<QuplaImportStmt> {
    String getModule();
}
