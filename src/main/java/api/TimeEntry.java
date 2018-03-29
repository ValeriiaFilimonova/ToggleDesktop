package api;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import lombok.*;
import org.apache.commons.lang3.time.DateUtils;

@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TimeEntry implements Comparable<TimeEntry>, Cloneable {
    public final static String SHORT_DATE_FORMAT = "yyyy-MM-dd";
    public final static String FULL_DATE_FORMAT = "yyyy-MM-dd HH:mm";

    @Getter @Setter
    private String id;

    @Getter @Setter
    private String pid;

    @Setter
    private String description;

    @Setter
    private Tag[] tags;

    @Getter @Setter
    @JsonFormat(pattern = ToggleClient.DATE_FORMAT)
    private Date start;

    @Getter @Setter
    @JsonFormat(pattern = ToggleClient.DATE_FORMAT)
    private Date stop;

    @Getter @Setter
    private int duration;

    @Getter @Setter
    private String created_with;

    public String getDescription() {
        if (description == null) {
            return "";
        }
        return description;
    }

    public Tag[] getTags() {
        if (tags == null) {
            return new Tag[] {};
        }

        return tags;
    }

    @JsonIgnore
    public Project getProject() {
        if (pid == null) {
            return null;
        }
        return Cache.getInstance().getProject(pid);
    }

    @JsonIgnore
    public String getDate() {
        return new SimpleDateFormat(TimeEntry.SHORT_DATE_FORMAT).format(start);
    }

    @JsonIgnore
    public String getFullDate() {
        return new SimpleDateFormat(TimeEntry.FULL_DATE_FORMAT).format(start);
    }

    @JsonIgnore
    public boolean todaysEntry() {
        return DateUtils.isSameDay(start, Calendar.getInstance().getTime());
    }

    @Override
    public int compareTo(TimeEntry entry) {
        if (getStart().before(entry.getStart())) {
            return -1;
        }
        if (getStart().after(entry.getStart())) {
            return 1;
        }
        return 0;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof TimeEntry) {
            return ((TimeEntry) object).getId().equals(id);
        }
        return false;
    }

    public static String formatDuration(int durationInSeconds) {
        int durationInMinutes = durationInSeconds / 60; // TODO use Joda
        int hours = durationInMinutes / 60;
        int minutes = durationInMinutes - hours * 60;
        return String.format("%dh %02dmin", hours, minutes);
    }

    @SneakyThrows
    public TimeEntry clone() {
        return (TimeEntry) super.clone();
    }

    @Data
    public static class TimeEntryResponseData {
        private TimeEntry data;
    }
}
