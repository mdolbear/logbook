package com.mjdsoftware.logbook.dto.oauth;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
public class KeycloakUserDTO {

    private String keycloakUserId;
    private long createdTimestamp;
    private String username;
    private boolean enabled;
    private boolean emailVerified;
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private Map<String, List<String>> attributes;



}
