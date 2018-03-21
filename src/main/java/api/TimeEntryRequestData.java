package api;

import lombok.Data;

@Data
public class TimeEntryRequestData {
    private final TimeEntry time_entry;

    static TimeEntryRequestData getStartRequest() {
        TimeEntry timeEntry = TimeEntry.builder().created_with("toggle desktop").build();
        return new TimeEntryRequestData(timeEntry);
    }
}
