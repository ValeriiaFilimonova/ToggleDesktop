package common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import lombok.SneakyThrows;

public class LocalSettingsStorage {
    private static String SETTINGS_FILE = ".toggle-desktop-settings";

    private Map<String, String> settings = null;

    public String get(LocalSettings key) {
        if (settings == null) {
            readSettingsFile();
        }

        return settings.get(key.toString());
    }

    @SneakyThrows(IOException.class)
    public void put(LocalSettings key, String value) {
        FileWriter fileWriter = new FileWriter(new File(SETTINGS_FILE));
        fileWriter.write(String.format("%s=%s", key, value));
        fileWriter.close();

        settings.put(key.toString(), value);
    }

    @SneakyThrows(IOException.class)
    private void readSettingsFile() {
        try {
            Scanner scanner = new Scanner(new File(SETTINGS_FILE));
            settings = new HashMap<>();

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] split = line.split("=", 2);

                settings.put(split[0], split[1]);
            }
        } catch (FileNotFoundException e) {
            File file = new File(SETTINGS_FILE);
            boolean created = file.createNewFile();

            if (created) {
                settings = new HashMap<>();
            }
        }
    }

    private static LocalSettingsStorage instance = null;

    public static LocalSettingsStorage getInstance() {
        if (instance == null) {
            instance = new LocalSettingsStorage();
        }

        return instance;
    }

    public static enum LocalSettings {
        TOGGLE_API_TOKEN,
        KEY_VALUE_STORAGE_TOKEN
    }
}
