package chapter2.item5.good;

import java.util.Objects;

public final class GoodInjectedReportService {
    private final ReportRepository repository;

    public GoodInjectedReportService(ReportRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public String report() {
        return repository.load();
    }

    public interface ReportRepository {
        String load();
    }
}
