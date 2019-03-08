package reports;

import api.toggle.TimeEntry;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class InvalidEntryException extends RuntimeException {
    @NonNull @Getter
    private TimeEntry entry;

    public InvalidEntryException(TimeEntry entry, String message) {
        super(message);
        this.entry = entry;
    }
}
