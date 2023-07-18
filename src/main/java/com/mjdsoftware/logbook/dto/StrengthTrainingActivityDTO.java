package com.mjdsoftware.logbook.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mjdsoftware.logbook.domain.entities.ActivityType;
import com.mjdsoftware.logbook.domain.entities.DurationUnits;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class StrengthTrainingActivityDTO extends ActivityDTO {


    /**
     * Answer a default instance
     */
    public StrengthTrainingActivityDTO() {

        super();

    }

    public StrengthTrainingActivityDTO(Long id,
                                       @NotNull ActivityType activityType,
                                       @NotNull DurationUnits durationUnits,
                                       double duration,
                                       long version,
                                       @NotNull String activityDetails,
                                       List<CommentDTO> comments) {

        super(id, activityType, durationUnits, duration, version, activityDetails, comments);

    }
}
