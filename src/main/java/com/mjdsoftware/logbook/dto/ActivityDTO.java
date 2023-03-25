package com.mjdsoftware.logbook.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mjdsoftware.logbook.domain.entities.ActivityType;
import com.mjdsoftware.logbook.domain.entities.DurationUnits;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@AllArgsConstructor
public class ActivityDTO {

    private Long id;

    @NotNull
    private ActivityType activityType;

    @NotNull
    private DurationUnits durationUnits;

    private double duration;
    private long version;

    @NotNull
    private String activityDetails;

    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private List<CommentDTO> comments;

    /**
     * Answer a default instance
     */
    public ActivityDTO() {

        this.setComments(new ArrayList<CommentDTO>());
    }

    /**
     * Answer a constructor without last field
     * @param anId long
     * @param aType ActivityType
     * @param aDuration double
     * @param aUnits DurationUnits
     * @param aVersion long
     * @param anActivityDetails String
     */
    public ActivityDTO(Long anId,
                       ActivityType aType,
                       double aDuration,
                       DurationUnits aUnits,
                       long aVersion,
                       String anActivityDetails) {

        this();
        this.setId(anId);
        this.setActivityType(aType);
        this.setDuration(aDuration);
        this.setDurationUnits(aUnits);
        this.setVersion(aVersion);
        this.setActivityDetails(anActivityDetails);

    }


    /**
     * Add a comment to me
     * @param aComment Comment
     */
    @JsonIgnore
    public void addComment(CommentDTO aComment) {

        this.getComments().add(aComment);
    }



}
