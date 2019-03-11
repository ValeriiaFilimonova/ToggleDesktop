package api.storage;

import java.util.List;

import api.toggle.Project;

public interface KeyValueStorage {
    String getToggleApiToken();

    void putToggleApiToken(String token);

    Project getDefaultProject();

    List<String> getIgnoreSpellList();
}
