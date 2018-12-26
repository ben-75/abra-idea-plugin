package org.abra.language.psi.stub;

import com.intellij.psi.stubs.StubElement;
import org.abra.language.psi.AbraImportStmt;

public interface ImportStmtStub extends StubElement<AbraImportStmt> {
    String getModule();
}
