package org.abra.language;


import com.intellij.openapi.fileTypes.*;
import org.jetbrains.annotations.NotNull;

public class QuplaFileTypeFactory extends FileTypeFactory {
    @Override
    public void createFileTypes(@NotNull FileTypeConsumer fileTypeConsumer) {
        fileTypeConsumer.consume(QuplaFileType.INSTANCE);
    }
}