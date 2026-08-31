package chapter2.item5.bad;

public final class BadServiceLocatorReportService {
    public interface ReportRepository {
        String load();
    }

    private static ReportRepository repository = () -> "file report";

    private BadServiceLocatorReportService() { }

    public static BadServiceLocatorReportService create() {
        return new BadServiceLocatorReportService();
    }

    public static void register(ReportRepository repository) {
        BadServiceLocatorReportService.repository = repository;
    }

    public String report() {
        return repository.load();
    }
}