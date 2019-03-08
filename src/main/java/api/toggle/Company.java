package api.toggle;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.NonNull;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Company {
    private int id;

    @NonNull
    private String name;

    @Data
    public static class CompanyData {
        private Company data;
    }
}
