package api;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.*;

@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TimeEntry {
    public final static String SHORT_DATE_FORMAT = "yyyy-MM-dd";

    @Getter @Setter
    private int id;

    @Setter
    private String pid;

    @Getter @Setter
    private String description;

    @Getter @Setter
    private Tag[] tags;

    @Getter @Setter
    @JsonFormat(pattern = ToggleClient.DATE_FORMAT)
    private Date start;

    @Getter @Setter
    @JsonFormat(pattern = ToggleClient.DATE_FORMAT)
    private Date stop;

    @Getter @Setter
    private int duration;

    public Project getProject() {
        if (pid == null) {
            return null;
        }
        return Cache.getInstance().getProject(pid);
    }

    public String getDate() {
        return new SimpleDateFormat(TimeEntry.SHORT_DATE_FORMAT).format(start);
    }

    public static String formatDuration(int durationInSeconds) {
        int durationInMinutes = durationInSeconds / 60;
        int hours = durationInMinutes / 60;
        int minutes = durationInMinutes - hours * 60;
        return String.format("%dh %02dmin", hours, minutes);
    }
}
