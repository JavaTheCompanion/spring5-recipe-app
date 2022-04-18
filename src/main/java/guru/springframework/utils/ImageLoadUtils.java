package guru.springframework.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
public final class ImageLoadUtils {

    public static byte[] loadImage(final Resource imageFile) {
        try {
            return Files.readAllBytes(Path.of(imageFile.getURI()));
        } catch (IOException e) {
            log.error("Error occurred while loading Image File", e);
            throw new RuntimeException(e.getLocalizedMessage());
        }
    }

}
