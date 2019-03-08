package api.toggle;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Cache {
    public static Project EMPTY_PROJECT = new Project("No project", "No project");

    private ToggleClient toggleClient = ToggleClient.getInstance();

    private Map<String, Project> projectsMap = new HashMap<>();
    private Map<String, Company> companiesMap = new HashMap<>();

    private Cache() {
        List<Project> projects = toggleClient.getAllProjects();

        for (Project project : projects) {
            projectsMap.put(project.getId(), project);
        }
    }

    public Collection<Project> getAllProjects() {
        return projectsMap.values();
    }

    public Project getProject(String projectId) {
        if (!projectsMap.containsKey(projectId)) {
            Project project = toggleClient.getProjectById(projectId);
            projectsMap.put(projectId, project);
        }

        return projectsMap.get(projectId);
    }

    public Company getCompany(String companyId) {
        if (!companiesMap.containsKey(companyId)) {
            Company company = toggleClient.getCompanyById(companyId);
            companiesMap.put(companyId, company);
        }

        return companiesMap.get(companyId);
    }

    private static Cache cacheInstance = null;

    public static Cache getInstance() {
        if (cacheInstance == null) {
            cacheInstance = new Cache();
        }

        return cacheInstance;
    }
}
