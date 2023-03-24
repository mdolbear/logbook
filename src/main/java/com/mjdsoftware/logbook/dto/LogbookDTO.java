package com.mjdsoftware.logbook.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Calendar;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogbookDTO {

    private Long id;

    @NotBlank
    private String name;

    private Calendar startDate;

    private long version;

    /**
     * Answer whether I have a valid id
     * @return boolean
     */
    @JsonIgnore
    public boolean isValidId() {

        return this.getId() != null &&
                this.getId().intValue() > 0;
    }

}
