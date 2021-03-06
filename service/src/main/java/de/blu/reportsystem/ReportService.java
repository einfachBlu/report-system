package de.blu.reportsystem;

import com.google.gson.Gson;
import com.google.inject.*;
import de.blu.reportsystem.rest.RESTInitializer;
import de.blu.reportsystem.storage.LocalStorage;
import de.blu.reportsystem.storage.Storage;
import de.blu.reportsystem.util.ReportWebExecutor;

@Singleton
public final class ReportService {
  public static void main(String[] args) {
    Injector injector =
        Guice.createInjector(
            new AbstractModule() {
              @Override
              protected void configure() {
                bind(Gson.class).toInstance(new Gson().newBuilder().setPrettyPrinting().create());

                // Current Storage
                bind(Storage.class).to(LocalStorage.class);
              }
            });
    ReportService reportService = injector.getInstance(ReportService.class);
    injector.injectMembers(reportService);
    reportService.startup();
  }

  @Inject private Injector injector;

  public void startup() {
    System.out.println("Report Service started");
    System.out.println("Possible REST API Calls:");

    System.out.println("");

    System.out.println("GET:");
    // System.out.println("\t- http://localhost:8080/status");
    // System.out.println("\t- http://localhost:8080/help");
    System.out.println("\t- http://localhost:8080/reports");
    System.out.println("\t- http://localhost:8080/report?id=%reportId%");
    System.out.println("\t- http://localhost:8080/report?player=%uuid%");

    System.out.println("PUT:");
    System.out.println("\t- http://localhost:8080/report");

    System.out.println("PATCH:");
    System.out.println("\t- http://localhost:8080/report");

    System.out.println("DELETE:");
    System.out.println("\t- http://localhost:8080/report?id=%reportId%");

    System.out.println("");

    // Storage
    Storage storage = this.injector.getInstance(Storage.class);
    storage.init();
    storage.load();

    this.injector.getInstance(RESTInitializer.class).init();

    // Testing
    ReportWebExecutor reportWebExecutor = new ReportWebExecutor();

    /*
    String content = reportWebExecutor.getRequest("http://localhost:8080");
    System.out.println(content);

    // Test async
    reportWebExecutor
        .getReportByIdAsync(
            "http://localhost:8080",
            1,
            report -> {
              if (report == null) {
                System.out.println("Report is null");
                return;
              }

              System.out.println(report);
            });

    // Create Report
    System.out.println(
        "Report created: "
            + reportWebExecutor.createReport(
                "http://localhost:8080",
                UUID.randomUUID(),
                "Hacker123",
                UUID.randomUUID(),
                "Supporter123",
                "HACKING"));

    // Show Report
    System.out.println(
        "Show Report (should be created): "
            + reportWebExecutor.getReportById("http://localhost:8080", 1));

    // Delete Report
    System.out.println(
        "Report deleted: " + reportWebExecutor.deleteReport("http://localhost:8080", 1));

    // Show Report
    System.out.println(
        "Show Report (should be deleted): "
            + reportWebExecutor.getReportById("http://localhost:8080", 1));
     */
  }
}
