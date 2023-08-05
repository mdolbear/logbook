package com.mjdsoftware.logbook.csv;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"id",
                    "activityType",
                    "duration",
                    "durationUnits",
                    "activityDetails",
                    "distance",
                    "distanceUnits",
                    "averageWatts",
                    "totalCalories",
                    "averageHeartRate"})
public class ActivityCSVLineDefinition {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("activityType")
    private String activityType;

    @JsonProperty("duration")
    private double duration;

    @JsonProperty("durationUnits")
    private String durationUnits;

    @JsonProperty("activityDetails")
    private String activityDetails;

    @JsonProperty("distance")
    private double distance;

    @JsonProperty("distanceUnits")
    private String distanceUnits;

    @JsonProperty("averageWatts")
    private double averageWatts;

    @JsonProperty("totalCalories")
    private double totalCalories;

    @JsonProperty("averageHeartRate")
    private double averageHeartRate;

}
