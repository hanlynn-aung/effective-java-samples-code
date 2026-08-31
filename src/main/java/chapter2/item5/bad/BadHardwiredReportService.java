package chapter2.item5.bad;

public final class BadHardwiredReportService {
    private final FileReportRepository repository = new FileReportRepository();

    public String report() {
        return repository.load();
    }

    private static final class FileReportRepository {
        String load() { return "file report"; }
    }
}
