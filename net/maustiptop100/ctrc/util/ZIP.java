package net.maustiptop100.ctrc.util;

import com.google.common.io.ByteStreams;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.*;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZIP {

    private final Path path;

    public ZIP(Path path) {
        this.path = path;
    }

    public ZIP get(String internal, Consumer<byte[]> callback)
            throws IOException
    {
        Path temp = Files.createTempFile("zip", "");
        try(FileSystem fs = FileSystems.newFileSystem(this.path, null)) {
            Path p = fs.getPath(internal);
            Files.copy(p, temp, StandardCopyOption.REPLACE_EXISTING);
            if(temp.toFile().isFile())
                try(FileInputStream is = new FileInputStream(temp.toFile())) {
                    callback.accept(ByteStreams.toByteArray(is));
                }
        }
        Files.delete(temp);
        return this;
    }

    public ZIP forEach(Consumer<ZipEntry> callback) throws IOException {
        try(ZipInputStream zis = new ZipInputStream(Files.newInputStream(this.path.toFile().toPath()))) {
            ZipEntry entry = zis.getNextEntry();
            while(entry != null) {
                callback.accept(entry);
                entry = zis.getNextEntry();
            }
        }
        return this;
    }

}
