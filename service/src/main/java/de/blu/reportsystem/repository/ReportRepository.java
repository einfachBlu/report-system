package de.blu.reportsystem.repository;

import com.google.inject.Singleton;
import de.blu.reportsystem.data.Report;

@Singleton
public final class ReportRepository extends Repository<Report> {

  public Report getById(int id) {
    return this.all().stream().filter(report -> report.getId() == id).findFirst().orElse(null);
  }
}
