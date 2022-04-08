package io.amalgm.nicehash;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.nicehash.connect.Api;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.*;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class MiningRigStats {
  private static final String URL_ROOT = "https://api2.nicehash.com/";
  private static final String ORG_ID = "a4206b80-1ca8-4a12-978d-67a6b81ddd3d";
  private static final String API_KEY = "5d837c1c-4f5a-413c-a4e6-fb11955636e3";
  private static final String API_SECRET = "37926392-7550-408a-b881-5bfd8a80122d60183b60-e4e0-4af7-bb2a-3b12ae7cafbd";
  private static final String RIG_ID = "0-ZG8kMPfGLUWyI-9VL7Xdbw";

  private static final String MINER_STATUS = "minerStatus";
  private static final String POWER_USAGE = "powerUsage";
  private static final String UNPAID_AMOUNT = "unpaidAmount";
  private static final String PROFITABILITY = "profitability";
  private static final String STATUS_TIME = "statusTime";

  private MiningRigStats() throws IOException {
    Api api = new Api(URL_ROOT, ORG_ID, API_KEY, API_SECRET);

    //get server time
    String timeResponse = api.get("api/v2/time");
    JsonObject timeObject = new Gson().fromJson(timeResponse, JsonObject.class);
    String time = timeObject.get("serverTime").getAsString();
    log.debug("server time: {}", time);

    //get mining stats
    String response = api.get("main/api/v2/mining/rig2/" + RIG_ID, true, time);
    JsonObject respObj = new Gson().fromJson(response, JsonObject.class);
    log.debug("mining stats response: {}", respObj.toString());

    Map<String, String> miningStats = new HashMap<>();

    Instant timestamp = Instant.ofEpochMilli(respObj.get(STATUS_TIME).getAsLong());
    miningStats.put(STATUS_TIME, timestamp.toString());
    miningStats.put(MINER_STATUS, respObj.get(MINER_STATUS).getAsString());
    miningStats.put(UNPAID_AMOUNT, respObj.get(UNPAID_AMOUNT).getAsString());
    DecimalFormat df = new DecimalFormat("#.########");
    miningStats.put(PROFITABILITY, df.format(Double.parseDouble(respObj.get(PROFITABILITY).getAsString())));
    miningStats.put(POWER_USAGE,
            respObj.getAsJsonArray("devices").get(1).getAsJsonObject().get(POWER_USAGE).getAsString());

    DateTimeFormatter yearAndMonth = DateTimeFormatter.ofPattern("yyyy_MM");
    LocalDate localDate = LocalDate.ofInstant(timestamp, ZoneId.systemDefault());
    Path output = Paths.get(String.format("mining_stats_%s.dat", yearAndMonth.format(localDate)));
    try {
      Files.createFile(output);
    } catch (FileAlreadyExistsException ignored) {
      // ok if file already exists
    }

    log.info("fetched stats: {}", miningStats);
    String line = new Gson().toJson(miningStats) + System.lineSeparator();
    Files.write(output, line.getBytes(),
            StandardOpenOption.APPEND);
  }

  public static void main(String[] args) throws IOException {
    new MiningRigStats();
  }
}
