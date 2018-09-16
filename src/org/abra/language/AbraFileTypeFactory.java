package org.abra.language;


import com.intellij.openapi.fileTypes.*;
import org.jetbrains.annotations.NotNull;

public class AbraFileTypeFactory extends FileTypeFactory {
    @Override
    public void createFileTypes(@NotNull FileTypeConsumer fileTypeConsumer) {
        fileTypeConsumer.consume(AbraFileType.INSTANCE, new FileNameMatcher() {
            @Override
            public boolean accept(@NotNull String fileName) {
                return fileName.endsWith(".abra") || fileName.endsWith(".abra.txt");
            }

            @NotNull
            @Override
            public String getPresentableString() {
                return "Abra language file";
            }
        });
    }
}