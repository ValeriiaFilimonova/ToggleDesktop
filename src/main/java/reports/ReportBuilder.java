package reports;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

import api.toggle.Company;
import api.toggle.Project;
import api.toggle.TimeEntry;
import api.toggle.ToggleClient;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class ReportBuilder implements AutoCloseable {
    private static String FILE_NAME = "Reports.xls";

    @Getter
    private List<InvalidEntryException> errors = new ArrayList<>();

    private Workbook workbook;

    private Sheet sheet;

    @SneakyThrows
    public static void build() {
        try (ReportBuilder reportBuilder = new ReportBuilder()) {
            reportBuilder.prepareRows();

            if (reportBuilder.errors.size() > 0) {
                throw new BuildReportException(reportBuilder.errors);
            }

            reportBuilder.adjustColumnsWidth();
            reportBuilder.writeToFile();
        }
    }

    @SneakyThrows
    private ReportBuilder() {
        workbook = new HSSFWorkbook(getClass().getClassLoader().getResourceAsStream(FILE_NAME));
        sheet = workbook.getSheetAt(0);
    }

    @Override
    public void close() throws Exception {
        workbook.close();
    }

    @SneakyThrows
    private void prepareRows() {
        ToggleClient toggleClient = ToggleClient.getInstance();
        List<TimeEntry> timeEntries = toggleClient.getTimeEntriesForMonth();

        for (TimeEntry entry : timeEntries) {
            try {
                ReportRow reportRow = buildRow(entry);
                addRow(reportRow);
            } catch (InvalidEntryException error) {
                errors.add(error);
            }
        }
    }

    private ReportRow buildRow(TimeEntry entry) {
        // TODO warn + default project
        if (entry.getPid() == null) {
            throw new InvalidEntryException(entry, "Has no project set");
        }

        if (entry.getDescription() == null) {
            throw new InvalidEntryException(entry, "Has empty description");
        }

        // TODO warn + if more than 1
        if (entry.getTags() == null || entry.getTags().length == 0) {
            throw new InvalidEntryException(entry, "Is not marked with tags");
        }

        Project project = entry.getProject();
        Company company = project != null ? project.getCompany() : null;

        return ReportRow.builder()
            .project(company, project, entry.getTags()[0])
            .effort(entry.getStart(), entry.getStop())
            .description(entry.getDescription())
            .date(entry.getStart())
            .build();
    }

    private void addRow(ReportRow reportRow) {
        Row row = sheet.createRow(sheet.getLastRowNum() + 1);

        createCellAndSetValue(row, reportRow.getProject());
        createCellAndSetValue(row, reportRow.getEffort());
        createCellAndSetValue(row, reportRow.getDescription());
        createCellAndSetValue(row, reportRow.getDate());
        createCellAndSetValue(row, reportRow.getDate());
    }

    private void adjustColumnsWidth() {
        Row row = sheet.getRow(sheet.getFirstRowNum());

        for (short i = row.getFirstCellNum(); i <= row.getLastCellNum(); i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void writeToFile() throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(FILE_NAME);
        workbook.write(fileOutputStream);
        fileOutputStream.close();
    }

    private void createCellAndSetValue(Row row, String value) {
        createCell(row).setCellValue(value);
    }

    private void createCellAndSetValue(Row row, Date value) {
        createCell(row).setCellValue(value);
    }

    private void createCellAndSetValue(Row row, double value) {
        createCell(row).setCellValue(value);
    }

    private Cell createCell(Row row) {
        short cellNumber = row.getLastCellNum() < 0 ? 0 : row.getLastCellNum();
        return row.createCell(cellNumber);
    }

    @RequiredArgsConstructor
    public static class BuildReportException extends RuntimeException {
        @Getter @NonNull
        private List<InvalidEntryException> errors;
    }
}
