package org.abra.language.psi.stub;

import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import org.abra.language.psi.AbraImportStmt;

public class ImportStmtStubImpl extends StubBase<AbraImportStmt> implements ImportStmtStub {

    private String module;

    public ImportStmtStubImpl(StubElement parent, String module) {
        super(parent, AbraStubElementTypes.IMPORT);
        this.module = module;
    }

    @Override
    public String getModule() {
        return module;
    }
}
