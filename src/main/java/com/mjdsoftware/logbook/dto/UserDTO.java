package com.mjdsoftware.logbook.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mjdsoftware.logbook.domain.entities.Logbook;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@AllArgsConstructor
public class UserDTO {

    private Long id;
    private String username;

    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private List<LogbookDTO> logbooks;

    private long version;

    /**
     * Answer a default instance
     */
    public UserDTO() {

        this.setLogbooks(new ArrayList<LogbookDTO>());

    }

    /**
     * Answer a user for username
     * @param username String
     */
    public UserDTO(String username) {

        this();
        this.setUsername(username);

    }

}
