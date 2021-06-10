package de.blu.reportsystem.storage;

import de.blu.reportsystem.data.Report;
import de.blu.reportsystem.repository.ReportRepository;

import java.util.List;
import java.util.OptionalInt;

public interface Storage {

  /**
   * Get the next id based on the existing report ids
   *
   * @return id of the next report
   */
  default int getNextReportId() {
    OptionalInt highestId = this.getReportRepository().all().stream().mapToInt(Report::getId).max();
    return highestId.orElse(0) + 1;
  }

  /**
   * Initializes the Storage. e.g. connecting to the SQL/NoSQL Database
   * or creates the Configs in case of a local storage
   */
  void init();

  /**
   * Load the Reports from the Storage and cache them in the local Repository
   *
   * @see ReportRepository
   */
  void load();

  /**
   * Check wether a Report exist in the Storage
   *
   * @param report the Report
   * @return true if exist, otherwise false
   */
  boolean contains(Report report);

  /**
   * Get a list of all Reports in the Storage
   *
   * @return a list with all Reports stored in the Storage
   */
  List<Report> all();

  /**
   * Get a Report in the Storage with the specified id
   *
   * @param id the id of the report to search for
   * @return report if found, otherwise null
   */
  default Report getById(int id){
    return this.all().stream().filter(report -> report.getId() == id).findFirst().orElse(null);
  }

  /**
   * Insert a Report into the Storage
   *
   * @param report the Report
   */
  void insert(Report report);

  /**
   * Delete a Report from the Storage
   *
   * @param report the Report
   */
  void delete(Report report);

  /**
   * Update a Report into the Storage The Id will be used as a reference to the previous Report
   *
   * @param report the Report with the new data
   */
  void update(Report report);

  /**
   * Get the Repository which contains all locally cached Report Information
   *
   * @return the Repository
   */
  ReportRepository getReportRepository();
}
