package api;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

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

    @Override
    public String toString() {
        return toValue();
    }

    public static String[] getAllAsStrings() {
        return Arrays.stream(Tag.values()).map(Tag::toString).toArray(String[]::new);
    }
}
