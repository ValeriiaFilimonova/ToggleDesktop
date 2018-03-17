import java.util.Date;

import lombok.*;

@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class TimeEntry {
    @Getter @Setter
    private int id;

    @Setter
    private int pid;

    @Getter @Setter
    private String description;

    @Getter @Setter
    private Tag[] tags;

    @Getter @Setter
    private Date start;

    @Getter @Setter
    private Date stop;

    @Setter
    private int duration;

    public Project getProject() {
        Project project = new Project("project id", "project name"); // TODO use project cache
        return project;
    }
}
