package com.mjdsoftware.logbook.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@AllArgsConstructor
public class LogbookEntryDTO {

    private Long id;
    private Calendar activityDate;
    private long version;

    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private List<ActivityDTO> activities;

    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private List<CommentDTO> comments;


    /**
     * Answer a default instance
     */
    public LogbookEntryDTO() {

        this.setActivities(new ArrayList<ActivityDTO>());
        this.setComments(new ArrayList<CommentDTO>());

    }

    /**
     * Answer an instance of me for specific parameters
     * @param anId Long
     * @param anActivityDate Calendar
     * @param aVersion long
     */
    public LogbookEntryDTO(Long anId, Calendar anActivityDate, long aVersion) {

        this();
        this.setId(anId);
        this.setActivityDate(anActivityDate);
        this.setVersion(aVersion);

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
