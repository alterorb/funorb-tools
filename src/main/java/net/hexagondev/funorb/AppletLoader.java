package net.hexagondev.funorb;

import com.google.gson.Gson;

import javax.swing.JFrame;
import javax.swing.WindowConstants;
import java.applet.Applet;
import java.applet.AppletContext;
import java.applet.AppletStub;
import java.awt.Dimension;
import java.io.FileReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AppletLoader implements AppletStub {

    private final GameMetadata gameMetadata;

    public AppletLoader(GameMetadata gameMetadata) {
        this.gameMetadata = gameMetadata;
    }

    public void initialize() throws Exception {
        Path gamepackPath = Paths.get("funorb", gameMetadata.getInternalName(), "gamepack.jar");
        URLClassLoader classLoader = new URLClassLoader(new URL[] {gamepackPath.toUri().toURL()});

        String mainClassName = gameMetadata.getMainClass();
        Class<?> mainClass = classLoader.loadClass(mainClassName.substring(0, mainClassName.indexOf('.')));
        Applet applet = (Applet) mainClass.newInstance();

        JFrame frame = new JFrame(gameMetadata.getName());
        frame.add(applet);
        frame.setVisible(true);
        frame.setMinimumSize(new Dimension(Integer.parseInt(gameMetadata.getParameter("width")) + 16, Integer.parseInt(gameMetadata.getParameter("height")) + 39));
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        applet.setStub(this);
        applet.init();
        applet.start();
    }

    @Override
    public String getParameter(String paramName) {
        return gameMetadata.getParameter(paramName);
    }

    @Override
    public URL getDocumentBase() {
        try {
            return new URL("http://" + gameMetadata.getBaseUrl());
        } catch (final MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public URL getCodeBase() {
        try {
            return new URL("http://" + gameMetadata.getBaseUrl());
        } catch (final MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public AppletContext getAppletContext() {
        return null;
    }

    @Override
    public void appletResize(int width, int height) {
    }

    public static void main(String[] args) throws Exception {
        Gson gson = new Gson();

        try (FileReader reader = new FileReader("funorb/escapevector/metadata.json")) {
            GameMetadata gameMetadata = gson.fromJson(reader, GameMetadata.class);

            AppletLoader loader = new AppletLoader(gameMetadata);
            loader.initialize();
        }
    }
}
