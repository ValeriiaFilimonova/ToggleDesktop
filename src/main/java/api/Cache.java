package api;

import java.util.HashMap;
import java.util.Map;

public class Cache {
    private ToggleClient toggleClient = ToggleClient.getInstance();

    private Map<String, Project> projects = new HashMap<>();
    private Map<String, Company> companies = new HashMap<>();


    public Project getProject(String projectId) {
        if (!projects.containsKey(projectId)) {
            Project project = toggleClient.getProjectById(projectId);
            projects.put(projectId, project);
        }

        return projects.get(projectId);
    }

    public Company getCompany(String companyId) {
        if(!companies.containsKey(companyId)) {
            Company company = toggleClient.getCompanyById(companyId);
            companies.put(companyId, company);
        }

        return companies.get(companyId);
    }

    private static Cache cacheInstance = null;

    public static Cache getInstance() {
        if (cacheInstance == null) {
            cacheInstance = new Cache();
        }

        return cacheInstance;
    }
}
