package net.hexagondev.funorb;

public class LaunchSingleGame {

    public static void main(String[] args) throws Exception {
        GameMetadata gameMetadata = GameList.getGameMetadataByInternalName("monkeypuzzle2");
        AppletLoader loader = new AppletLoader(gameMetadata);
        loader.launch();
    }
}
