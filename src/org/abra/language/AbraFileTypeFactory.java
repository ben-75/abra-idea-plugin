package org.abra.language;


import com.intellij.openapi.fileTypes.*;
import org.jetbrains.annotations.NotNull;

public class AbraFileTypeFactory extends FileTypeFactory {
    @Override
    public void createFileTypes(@NotNull FileTypeConsumer fileTypeConsumer) {
        fileTypeConsumer.consume(AbraFileType.INSTANCE);
    }
}