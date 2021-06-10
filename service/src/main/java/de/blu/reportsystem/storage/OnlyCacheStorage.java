package de.blu.reportsystem.storage;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.blu.reportsystem.data.Report;
import de.blu.reportsystem.repository.ReportRepository;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Singleton
public final class OnlyCacheStorage implements Storage {

  @Inject @Getter private ReportRepository reportRepository;

  @Override
  public void init() {}

  @Override
  public void load() {}

  @Override
  public List<Report> all() {
    return new ArrayList<>();
  }

  @Override
  public boolean contains(Report targetReport) {
    return this.all().stream().anyMatch(report -> report.getId() == targetReport.getId());
  }

  @Override
  public void insert(Report report) {
    this.getReportRepository().add(report);
  }

  @Override
  public void delete(Report report) {
    Report targetReport = this.getReportRepository().getById(report.getId());
    if (targetReport == null) {
      return;
    }

    this.getReportRepository().remove(targetReport);
  }

  @Override
  public void update(Report report) {
    Report targetReport = this.getReportRepository().getById(report.getId());
    if (targetReport == null) {
      return;
    }

    targetReport.setId(report.getId());
    targetReport.setReportReceiverPlayerId(report.getReportReceiverPlayerId());
    targetReport.setReportReceiverPlayerName(report.getReportReceiverPlayerName());
    targetReport.setReportSenderPlayerId(report.getReportSenderPlayerId());
    targetReport.setReportSenderPlayerName(report.getReportSenderPlayerName());
    targetReport.setReason(report.getReason());
    targetReport.setTime(report.getTime());
    targetReport.setReportState(report.getReportState());
    targetReport.setAdditionalInformation(report.getAdditionalInformation());
  }
}
