import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@AllArgsConstructor
public enum Tag {
    COMMUNICATION,
    INVESTIGATION,
    DEVELOPMENT,
    ADMINISTRATION,
    TESTING;

    public String toValue() {
        return StringUtils
            .capitalize(super.toString().toLowerCase())
            .intern();
    }
}
