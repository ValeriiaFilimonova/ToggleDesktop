import lombok.*;

@RequiredArgsConstructor
@AllArgsConstructor
public class Project {
    @NonNull
    @Getter @Setter
    private String id;

    @Setter
    private String cid;

    @NonNull
    @Getter @Setter
    private String name;

    @Data
    public static class ProjectData {
        private Project data;
    }

    public Company getCompany() {
        Company company = new Company("company name"); // TODO use company cache
        return company;
    }
}
