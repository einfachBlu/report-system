package de.blu.reportsystem.storage;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.blu.reportsystem.data.Report;
import de.blu.reportsystem.repository.ReportRepository;
import lombok.Getter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Singleton
public final class LocalStorage implements Storage {

  @Inject @Getter private ReportRepository reportRepository;
  @Inject @Getter private Gson gson;

  private File configFile;

  @Override
  public void init() {
    this.configFile = new File(this.getRootDirectory(), "local_storage.json");

    if (!this.configFile.exists()) {
      try {
        this.configFile.createNewFile();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public void load() {
    // Load from Config Storage File
    List<Report> reports = this.all();
    System.out.println("Loaded Reports: " + Arrays.toString(reports.toArray()));

    // Cache them in the Repository
    this.getReportRepository().clear();
    this.getReportRepository().addAll(reports);
  }

  @Override
  public List<Report> all() {
    try (FileReader fileReader = new FileReader(this.configFile)) {
      Report[] reports = this.gson.fromJson(fileReader, Report[].class);
      if (reports == null) {
        return new ArrayList<>();
      }

      return Arrays.asList(reports);
    } catch (IOException e) {
      e.printStackTrace();
    }

    return new ArrayList<>();
  }

  @Override
  public boolean contains(Report targetReport) {
    return this.all().stream().anyMatch(report -> report.getId() == targetReport.getId());
  }

  @Override
  public void insert(Report report) {
    this.getReportRepository().add(report);

    this.save();
  }

  @Override
  public void delete(Report report) {
    Report targetReport = this.getReportRepository().getById(report.getId());
    if (targetReport == null) {
      return;
    }

    this.getReportRepository().remove(targetReport);
    this.save();
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
    targetReport.setReportEditingPlayerId(report.getReportEditingPlayerId());
    targetReport.setReportEditingPlayerName(report.getReportEditingPlayerName());
    targetReport.setReason(report.getReason());
    targetReport.setTime(report.getTime());
    targetReport.setReportState(report.getReportState());
    targetReport.setAdditionalInformation(report.getAdditionalInformation());

    this.save();
  }

  private void save() {
    try {
      try (FileWriter fileWriter = new FileWriter(this.configFile)) {
        this.gson.toJson(this.getReportRepository().all(), fileWriter);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private File getRootDirectory() {
    File directory = null;

    try {
      directory =
          new File(
                  LocalStorage.class
                      .getProtectionDomain()
                      .getCodeSource()
                      .getLocation()
                      .toURI()
                      .getPath())
              .getParentFile();
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }

    if (directory != null && !directory.isDirectory()) {
      if (!directory.mkdir()) {
        throw new NullPointerException("Couldn't create root directory!");
      }
    }

    return directory;
  }
}
