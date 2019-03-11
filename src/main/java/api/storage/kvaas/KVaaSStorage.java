package api.storage.kvaas;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

import java.util.List;

import api.storage.KeyValueStorage;
import api.toggle.Project;
import common.TokenEncryptor;
import lombok.NonNull;

public class KVaaSStorage implements KeyValueStorage {

    private static String API_URL = "https://api.keyvalue.xyz";

    @NonNull
    private Client jerseyClient = Client.create();

    // TODO temp
    private String apiToken = "9ee94f59";//TokenEncryptor.decrypt("HpytShinvqNDONDAkA6TmR/SvUiK65UI");

    @Override
    public String getToggleApiToken() {
        WebResource resource = jerseyClient
            .resource(String.format("%s/%s/%s", API_URL, apiToken, "toggle.token"));
        String response = resource
//            .accept(MediaType.APPLICATION_JSON_TYPE)
            .get(String.class);
        return null;
    }

    @Override
    public void putToggleApiToken(String token) {

    }

    @Override
    public Project getDefaultProject() {
        return null;
    }

    @Override
    public List<String> getIgnoreSpellList() {
        return null;
    }
}
