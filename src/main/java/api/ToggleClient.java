package api;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

import java.text.SimpleDateFormat;
import java.util.*;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.MediaType;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ToggleClient {
    public final static String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssXXX";

    private final static String API_TOKEN = "80c1d6324f26fae2fb29646be7686cf1";

    private final static String API_URL = "https://www.toggl.com/api/v8/";
    private final static String TIME_ENTRIES_PATH = "time_entries";
    private final static String PROJECTS_PATH = "projects/";
    private final static String COMPANIES_PATH = "clients/";

    private SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT);

    @NonNull
    private Client jerseyClient;

    public Company getCompanyById(String companyId) {
        WebResource resource = jerseyClient.resource(API_URL + COMPANIES_PATH + companyId);
        ClientResponse response = performGetRequest(resource);

        Company.CompanyData companyData = response.getEntity(Company.CompanyData.class);
        return companyData.getData();
    }

    public Project getProjectById(String projectId) {
        WebResource resource = jerseyClient.resource(API_URL + PROJECTS_PATH + projectId);
        ClientResponse response = performGetRequest(resource);
        Project.ProjectData projectData = response.getEntity(Project.ProjectData.class);
        return projectData.getData();
    }

    public List<TimeEntry> getTimeEntriesBeforeDate(Date endDate, int count) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(endDate);
        calendar.add(Calendar.DATE, -count);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        WebResource resource = jerseyClient
            .resource(API_URL + TIME_ENTRIES_PATH)
            .queryParam("start_date", dateFormatter.format(calendar.getTime()))
            .queryParam("end_date", dateFormatter.format(endDate));
        ClientResponse response = performGetRequest(resource);
        return response.getEntity(new GenericType<List<TimeEntry>>() {});
    }

    public List<TimeEntry> getMonthTimeEntries() {
        WebResource resource = jerseyClient
            .resource(API_URL + TIME_ENTRIES_PATH)
            .queryParam("start_date", getMonthDateString());
        ClientResponse response = performGetRequest(resource);
        return response.getEntity(new GenericType<List<TimeEntry>>() {});
    }

    private String getMonthDateString() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        return dateFormatter.format(calendar.getTime());
    }

    private ClientResponse performGetRequest(WebResource resource) {
        ClientResponse response = resource.accept(MediaType.APPLICATION_JSON_TYPE).get(ClientResponse.class);

        if (response.getStatus() != 200) {
            throw new BadRequestException(
                String.format("Status %d: %s", response.getStatus(), response.getStatusInfo().getReasonPhrase())
            );
        }

        return response;
    }

    private static ToggleClient toggleClientInstance = null;

    public static ToggleClient getInstance() {
        if (toggleClientInstance == null) {
            ClientConfig clientConfig = new DefaultClientConfig(JacksonJsonProvider.class);

            Client client = Client.create(clientConfig);
            client.addFilter(new HTTPBasicAuthFilter(API_TOKEN, "api_token"));

            toggleClientInstance = new ToggleClient(client);
        }

        return toggleClientInstance;
    }
}
