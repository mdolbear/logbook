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
public class UnMonitoredAerobicActivityDTO extends ActivityDTO {

    private double distance;

    @NotNull
    private DistanceUnits distanceUnits;

    /**
     * Answer a default instance
     */
    public UnMonitoredAerobicActivityDTO() {
        super();
    }

    /**
     * Answer an instance with the following arguments
     * @param id Long
     * @param activityType ActivityType
     * @param durationUnits DurationUnits
     * @param duration double
     * @param version long
     * @param activityDetails String
     * @param comments List
     * @param distance double
     * @param distanceUnits DistanceUnits
     */
    public UnMonitoredAerobicActivityDTO(Long id,
                                         @NotNull ActivityType activityType,
                                         @NotNull DurationUnits durationUnits,
                                         double duration,
                                         long version,
                                         @NotNull String activityDetails,
                                         List<CommentDTO> comments,
                                         double distance,
                                         DistanceUnits distanceUnits) {

        super(id, activityType, durationUnits, duration, version, activityDetails, comments);
        this.setDistance(distance);
        this.setDistanceUnits(distanceUnits);

    }

}
