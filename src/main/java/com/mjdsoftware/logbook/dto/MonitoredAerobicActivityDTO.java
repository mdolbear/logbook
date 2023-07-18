package com.mjdsoftware.logbook.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mjdsoftware.logbook.domain.entities.ActivityType;
import com.mjdsoftware.logbook.domain.entities.DistanceUnits;
import com.mjdsoftware.logbook.domain.entities.DurationUnits;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class MonitoredAerobicActivityDTO extends ActivityDTO {

    private double distance;

    @NotNull
    private DistanceUnits distanceUnits;

    private double averageWatts;
    private double totalCalories;
    private double averageHeartRate;

    /**
     * Answer a default instance
     */
    public MonitoredAerobicActivityDTO() {
        super();
    }

    /**
     * Answer an instance on the following arguments
     * @param id Long
     * @param activityType ActivityType
     * @param durationUnits DurationUnits
     * @param duration double
     * @param version long
     * @param activityDetails String
     * @param comments List
     * @param distance double
     * @param distanceUnits DistanceUnits
     * @param averageWatts double
     * @param totalCalories double
     * @param averageHeartRate double
     */
    public MonitoredAerobicActivityDTO(Long id,
                                       @NotNull ActivityType activityType,
                                       @NotNull DurationUnits durationUnits,
                                       double duration,
                                       long version,
                                       @NotNull String activityDetails,
                                       List<CommentDTO> comments,
                                       double distance,
                                       @NotNull DistanceUnits distanceUnits,
                                       double averageWatts,
                                       double totalCalories,
                                       double averageHeartRate) {

        super(id, activityType, durationUnits, duration, version, activityDetails, comments);
        this.setDistance(distance);
        this.setDistanceUnits(distanceUnits);
        this.setAverageWatts(averageWatts);
        this.setTotalCalories(totalCalories);
        this.setAverageHeartRate(averageHeartRate);

    }
}
