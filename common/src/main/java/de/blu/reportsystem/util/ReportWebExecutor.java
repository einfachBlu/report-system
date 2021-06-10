package de.blu.reportsystem.util;

import com.google.gson.Gson;
import de.blu.reportsystem.data.Report;
import de.blu.reportsystem.exception.ServiceUnreachableException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ReportWebExecutor extends WebExecutor {

  public List<Report> getReports(String url) throws ServiceUnreachableException {
    String content = this.getRequest(url + "/reports");

    if (content.isEmpty()) {
      return new ArrayList<>();
    }

    Report[] reports = new Gson().fromJson(content, Report[].class);
    return Arrays.asList(reports);
  }

  public List<Report> getReportsByPlayer(String url, UUID playerId)
      throws ServiceUnreachableException {
    String content = this.getRequest(url + "/report?player=" + playerId.toString());

    if (content.isEmpty()) {
      return new ArrayList<>();
    }

    Report[] reports = new Gson().fromJson(content, Report[].class);
    return Arrays.asList(reports);
  }

  public List<Report> getReportsByPlayer(String url, String playeName)
      throws ServiceUnreachableException {
    String content = this.getRequest(url + "/report?player=" + playeName);

    if (content.isEmpty()) {
      return new ArrayList<>();
    }

    Report[] reports = new Gson().fromJson(content, Report[].class);
    return Arrays.asList(reports);
  }

  public Report getReportById(String url, int id) throws ServiceUnreachableException {
    String content = this.getRequest(url + "/report?id=" + id);

    if (content.isEmpty()) {
      return null;
    }

    Report report = new Gson().fromJson(content, Report.class);
    return report;
  }

  public Report createReport(
      String url,
      UUID reportReceiverId,
      String reportReceiverName,
      UUID reportSenderId,
      String reportSenderName,
      String reason)
      throws ServiceUnreachableException {
    Report report = new Report();
    report.setReportReceiverPlayerId(reportReceiverId);
    report.setReportReceiverPlayerName(reportReceiverName);
    report.setReportSenderPlayerId(reportSenderId);
    report.setReportSenderPlayerName(reportSenderName);
    report.setReportEditingPlayerId(null);
    report.setReportEditingPlayerName(null);
    report.setReason(reason);

    String body = new Gson().toJson(report);
    String content = this.putRequest(url + "/report", body);

    if (content.isEmpty()) {
      return null;
    }

    return new Gson().fromJson(content, Report.class);
  }

  public Report deleteReport(String url, int reportId) throws ServiceUnreachableException {
    String content = this.deleteRequest(url + "/report?id=" + reportId);

    if (content.isEmpty()) {
      return null;
    }

    return new Gson().fromJson(content, Report.class);
  }

  public void updateReport(String url, Report updatedReport) throws ServiceUnreachableException {
    this.patchRequest(url + "/report", new Gson().toJson(updatedReport));
  }
}
