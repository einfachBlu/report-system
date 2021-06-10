package de.blu.reportsystem.data;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
public class Report {

  private int id;

  // Player who was reported
  private String reportReceiverPlayerName;
  private UUID reportReceiverPlayerId;

  // Player who requested the Report
  private String reportSenderPlayerName;
  private UUID reportSenderPlayerId;

  // Report Information
  private String reason;
  private long time;
  private ReportState reportState = ReportState.NONE;
  private Map<String, String> additionalInformation = new HashMap<>();

  @Override
  public String toString() {
    return new Gson().toJson(this);
  }
}
