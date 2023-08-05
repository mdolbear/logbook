package com.mjdsoftware.logbook.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mjdsoftware.logbook.domain.entities.ActivityType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActivityExportRequest {

    private long startTimeEpoch;
    private long endTimeEpoch;

    private ActivityType activityType;

    @NotNull
    private String exportFilename;


    /**
     * Answer whether start and end dates are valid
     * @return boolean
     */
    @JsonIgnore
    public boolean isDateRangeValid() {

        Date tempStartDate;
        Date tempEndDate;

        tempStartDate = new Date(this.getStartTimeEpoch());
        tempEndDate = new Date(this.getEndTimeEpoch());

        return tempStartDate.before(tempEndDate);

    }

}
