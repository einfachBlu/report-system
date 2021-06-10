package de.blu.reportsystem.rest.listener;

import com.google.gson.Gson;
import com.google.inject.Inject;
import de.blu.reportsystem.data.Report;
import de.blu.reportsystem.data.ReportState;
import de.blu.reportsystem.repository.ReportRepository;
import de.blu.reportsystem.storage.Storage;

import static spark.Spark.patch;

public final class UpdateReport implements WebListener {

  @Inject private ReportRepository reportRepository;
  @Inject private Gson gson;
  @Inject private Storage storage;

  @Override
  public void setup() {
    patch(
        "/report",
        (request, response) -> {
          if (request.body().isEmpty()) {
            return "no body";
          }

          Report report = this.gson.fromJson(request.body(), Report.class);
          this.storage.update(report);

          return this.gson.toJson(report);
        });
  }
}
