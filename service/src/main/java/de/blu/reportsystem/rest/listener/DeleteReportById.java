package de.blu.reportsystem.rest.listener;

import com.google.gson.Gson;
import com.google.inject.Inject;
import de.blu.reportsystem.data.Report;
import de.blu.reportsystem.repository.ReportRepository;
import de.blu.reportsystem.storage.Storage;

import static spark.Spark.delete;

public final class DeleteReportById implements WebListener {

  @Inject private ReportRepository reportRepository;
  @Inject private Gson gson;
  @Inject private Storage storage;

  @Override
  public void setup() {
    delete(
        "/report",
        (request, response) -> {
          if (request.queryParams().contains("id")) {
            try {
              int id = Integer.parseInt(request.queryParams("id"));

              Report report = this.reportRepository.getById(id);
              if (report != null) {
                this.storage.delete(report);
                return "";
              }
            } catch (NumberFormatException e) {
              // no integer
            }
          }

          response.status(404);
          return "invalid id";
        });
  }
}
