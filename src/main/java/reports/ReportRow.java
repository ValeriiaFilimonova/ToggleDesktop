package reports;

import java.util.Date;
import java.util.StringJoiner;

import api.toggle.Company;
import api.toggle.Project;
import api.toggle.Tag;
import lombok.*;

@Getter
@Builder(builderClassName = "RowBuilder")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ReportRow {
    public static String DATE_FORMAT = "MM/dd/yyyy";

    @NonNull
    private String project;

    private double effort;

    @NonNull
    private String description;

    @NonNull
    private Date date;

    public static class RowBuilder {
        private static double MILLISECONDS_IN_HOUR = 60 * 60 * 1000;

        public RowBuilder project(Company company, @NonNull Project project, @NonNull Tag tag) {
            StringJoiner joiner = new StringJoiner(".");

            if (company != null) {
                joiner.add(company.getName());
            }
            joiner.add(project.getName());
            joiner.add(tag.toValue());

            this.project = joiner.toString();

            return this;
        }

        public RowBuilder effort(Date startDate, Date endDate) {
            long effortInMS = endDate.getTime() - startDate.getTime();
            double effortShare = effortInMS / MILLISECONDS_IN_HOUR;
            this.effort = Math.round(effortShare * 10) / 10.0;
            return this;
        }
    }
}