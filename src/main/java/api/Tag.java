package api;

import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@AllArgsConstructor
public enum Tag {
    COMMUNICATION,
    INVESTIGATION,
    DEVELOPMENT,
    ADMINISTRATION,
    TESTING;

    @JsonValue
    public String toValue() {
        return StringUtils
            .capitalize(super.toString().toLowerCase())
            .intern();
    }
}
