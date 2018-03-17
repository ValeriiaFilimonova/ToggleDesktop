import lombok.Data;
import lombok.NonNull;

@Data
public class Company {
    private int id;

    @NonNull
    private String name;

    @Data
    public static class CompanyData {
        private Company data;
    }
}
