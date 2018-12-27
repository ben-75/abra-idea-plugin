package org.abra.language.psi.stub;

import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import org.abra.language.psi.QuplaImportStmt;

public class ImportStmtStubImpl extends StubBase<QuplaImportStmt> implements ImportStmtStub {

    private String module;

    public ImportStmtStubImpl(StubElement parent, String module) {
        super(parent, QuplaStubElementTypes.IMPORT);
        this.module = module;
    }

    @Override
    public String getModule() {
        return module;
    }
}
