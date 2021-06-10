package de.blu.reportsystem.rest.listener;

import com.google.gson.Gson;
import com.google.inject.Inject;
import de.blu.reportsystem.repository.ReportRepository;

import static spark.Spark.get;

public final class ListReports implements WebListener {

  @Inject private ReportRepository reportRepository;
  @Inject private Gson gson;

  @Override
  public void setup() {
    get("/reports", (request, response) -> gson.toJson(this.reportRepository.all()));
  }
}
