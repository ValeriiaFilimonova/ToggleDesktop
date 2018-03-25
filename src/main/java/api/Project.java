package api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.*;

@RequiredArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Project {
    @NonNull
    @Getter @Setter
    private String id;

    @Setter
    private String cid;

    @Setter
    private String color;

    @NonNull
    @Getter @Setter
    private String name;

    @Data
    public static class ProjectData {
        private Project data;
    }

    public String getColorId() {
        return color;
    }

    public Company getCompany() {
        if (cid == null) {
            return null;
        }
        return Cache.getInstance().getCompany(cid);
    }

    @JsonIgnore
    public String getCompanyName() {
        Company company = getCompany();

        if (company == null) {
            return null;
        }

        return company.getName();
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Project && ((Project) object).getId().equals(id)) {
            return true;
        }
        return false;
    }
}
