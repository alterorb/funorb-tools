package net.hexagondev.funorb;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.log4j.Log4j2;
import net.hexagondev.funorb.GameMetadata.GameMetadataBuilder;
import org.apache.http.client.fluent.Request;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
public class FunorbArchiver {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final String URL_GAME_LIST_PAGE = "http://www.funorb.com/gamelist.ws";
    private static final Pattern GAME_NAME_PATTERN = Pattern.compile("funorb.com/(\\w+)/play.ws");
    private static final Path DIRECTORY = Paths.get("funorb");

    public static void main(String[] args) throws IOException {

        if (!Files.exists(DIRECTORY)) {
            Files.createDirectories(DIRECTORY);
        }

        Document document = Jsoup.connect(URL_GAME_LIST_PAGE).get();
        Elements elements = document.select(".buttonPlaySmall");

        List<String> gameLinks = elements.eachAttr("href");

        LOGGER.info("Found {} games to be archived", gameLinks.size());

        gameLinks.parallelStream().forEach(gameLink -> {
            try {
                archive(gameLink);
            } catch (IOException e) {
                LOGGER.error(e);
            }
        });
    }

    private static void archive(String gameLink) throws IOException {
        Matcher matcher = GAME_NAME_PATTERN.matcher(gameLink);
        matcher.find();

        String internalName = matcher.group(1);

        LOGGER.debug("Starting to archive the game {}", internalName);

        Path archiveDirectory = DIRECTORY.resolve(internalName);

        if (!Files.exists(archiveDirectory)) {
            Files.createDirectories(archiveDirectory);
        }

        Document document = Jsoup.connect(gameLink).get();
        Element gameDefinition = document.selectFirst("#theGameScreen");

        LOGGER.debug("Building the metadata...");

        GameMetadataBuilder builder = GameMetadata.builder().internalName(internalName);
        Element applet = gameDefinition.selectFirst("applet");
        builder.name(applet.attr("name")).mainClass(applet.attr("code"));

        gameDefinition.select("param").forEach(element -> builder.parameter(element.attr("name"), element.attr("value")));

        String baseUrl = document.location();
        URL url = new URL(baseUrl);
        builder.baseUrl(url.getHost());
        GameMetadata gameMetadata = builder.build();

        try (BufferedWriter writer = Files.newBufferedWriter(archiveDirectory.resolve("metadata.json"))) {
            GSON.toJson(gameMetadata, writer);
        }

        String archiveName = gameMetadata.getParameter("archive");
        String gamepackUrl =  baseUrl.substring(0, baseUrl.length() - 7) + archiveName;

        LOGGER.debug("Downloading gamepack from {}", gamepackUrl);

        Path gamepackPath = archiveDirectory.resolve("gamepack.jar");

        long startTime = System.currentTimeMillis();
        Request.Get(gamepackUrl).execute().saveContent(gamepackPath.toFile());

        LOGGER.debug("Finished archiving {} in {}ms", gameMetadata.getName(), (System.currentTimeMillis() - startTime));
    }
}
