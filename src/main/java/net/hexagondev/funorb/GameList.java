package net.hexagondev.funorb;

import com.google.gson.Gson;
import lombok.extern.log4j.Log4j2;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Log4j2
public class GameList {

    private static final Path GAMES_DIRECTORY = Paths.get("funorb");
    private static List<GameMetadata> games;

    public static GameMetadata getGameMetadataByInternalName(String internalName) {
        if (games == null) {
            init();
        }
        return games.stream().filter(gameMetadata -> Objects.equals(gameMetadata.getInternalName(), internalName)).findFirst().orElse(null);
    }

    public static List<GameMetadata> getGames() {
        if (games == null) {
            init();
        }
        return games;
    }

    private static void init() {

        if (games != null) {
            throw new IllegalStateException("We've already loaded the local games list!");
        }

        games = new ArrayList<>();
        Gson gson = new Gson();

        try {
            Files.walkFileTree(GAMES_DIRECTORY, new SimpleFileVisitor<Path>() {

                @Override
                public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) {

                    if (Objects.equals(path.getFileName().toString(), "metadata.json")) {
                        try (BufferedReader reader = Files.newBufferedReader(path)) {
                            GameMetadata gameMetadata = gson.fromJson(reader, GameMetadata.class);

                            games.add(gameMetadata);
                        } catch (IOException e) {
                            LOGGER.error("Failed to parse game metadata at {}, cause={}", path, e.getMessage());
                        }
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            LOGGER.error("Failed to list local games!", e);
        }
    }
}
