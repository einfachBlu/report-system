package de.blu.reportsystem.rest.listener;

import com.google.gson.Gson;
import com.google.inject.Inject;
import de.blu.reportsystem.data.Report;
import de.blu.reportsystem.repository.ReportRepository;
import spark.Request;
import spark.Response;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static spark.Spark.get;

public final class ShowReportsBy implements WebListener {

  @Inject private ReportRepository reportRepository;
  @Inject private Gson gson;

  @Override
  public void setup() {
    get(
        "/report",
        (request, response) -> {
          if (request.queryParams().contains("id")) {
            return this.handleReportIdFilter(request, response);
          } else if (request.queryParams().contains("player")) {
            return this.handlePlayerFilter(request, response);
          }

          response.status(404);
          return "unknown filter";
        });
  }

  private String handleReportIdFilter(Request request, Response response) {
    try {
      int id = Integer.parseInt(request.queryParams("id"));

      Report report = this.reportRepository.getById(id);
      if (report != null) {
        return this.gson.toJson(report);
      }
    } catch (NumberFormatException e) {
      // no integer
    }

    response.status(404);
    return "invalid id";
  }

  private String handlePlayerFilter(Request request, Response response) {
    AtomicReference<String> playerName = new AtomicReference<>(null);
    AtomicReference<UUID> playerId = new AtomicReference<>(null);
    try {
      playerId.set(UUID.fromString(request.queryParams("player")));
    } catch (Exception e) {
      playerName.set(request.queryParams("player"));
    }

    List<Report> reports =
        this.reportRepository.all().stream()
            .filter(
                report -> {
                  if (playerId.get() != null) {
                    if (report.getReportReceiverPlayerId().equals(playerId.get())
                        || report.getReportSenderPlayerId().equals(playerId.get())) {
                      return true;
                    }
                  }
                  if (playerName.get() != null) {
                    if (report.getReportReceiverPlayerName().equals(playerName.get())
                        || report.getReportSenderPlayerName().equals(playerName.get())) {
                      return true;
                    }
                  }

                  return false;
                })
            .collect(Collectors.toList());
    return this.gson.toJson(reports);
  }
}
