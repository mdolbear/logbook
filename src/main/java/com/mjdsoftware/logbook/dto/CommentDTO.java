package com.mjdsoftware.logbook.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Calendar;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO implements DTOCollectionEntry {

    private Long id;
    private String commentContents;
    private long version;
    private Calendar createdAt;



}
