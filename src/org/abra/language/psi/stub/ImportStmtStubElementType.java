package org.abra.language.psi.stub;

import com.intellij.lang.LighterAST;
import com.intellij.lang.LighterASTNode;
import com.intellij.lang.LighterASTTokenNode;
import com.intellij.psi.impl.source.tree.LightTreeUtil;
import com.intellij.psi.stubs.*;
import com.intellij.util.CharTable;
import org.abra.language.AbraLanguage;
import org.abra.language.psi.AbraImportStmt;
import org.abra.language.psi.AbraTypes;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class ImportStmtStubElementType  extends ILightStubElementType<ImportStmtStub, AbraImportStmt> {
    ImportStmtStubElementType() {
        super("IMPORT", AbraLanguage.INSTANCE);
    }

    @Override
    public AbraImportStmt createPsi(@NotNull final ImportStmtStub stub) {
        return null;//new AbraImportStmtImpl(stub, this);
//        return new AbraImportStmtImpl(stub, this);
    }

    @Override
    @NotNull
    public ImportStmtStub createStub(@NotNull final AbraImportStmt psi, final StubElement parentStub) {
        return new ImportStmtStubImpl(parentStub, psi.getFilePath());
    }

    @Override
    @NotNull
    public String getExternalId() {
        return "properties.prop";
    }

    @Override
    public void serialize(@NotNull final ImportStmtStub stub, @NotNull final StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getModule());
    }

    @Override
    @NotNull
    public ImportStmtStub deserialize(@NotNull final StubInputStream dataStream, final StubElement parentStub) throws IOException {
        return new ImportStmtStubImpl(parentStub, dataStream.readNameString());
    }

    @Override
    public void indexStub(@NotNull final ImportStmtStub stub, @NotNull final IndexSink sink) {
    }

    @NotNull
    @Override
    public ImportStmtStub createStub(@NotNull LighterAST tree, @NotNull LighterASTNode node, @NotNull StubElement parentStub) {
        LighterASTNode keyNode = LightTreeUtil.firstChildOfType(tree, node, AbraTypes.IDENTIFIER);
        String key = intern(tree.getCharTable(), keyNode);
        return new ImportStmtStubImpl(parentStub, key);
    }

    public static String intern(@NotNull CharTable table, @NotNull LighterASTNode node) {
        assert node instanceof LighterASTTokenNode : node;
        return table.intern(((LighterASTTokenNode)node).getText()).toString();
    }
}
