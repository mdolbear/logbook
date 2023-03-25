package com.mjdsoftware.logbook.dto.oauth;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true,value={"separateUsername","organizationId"})
public class UserAuthDTO {

    private String id;
    private long createdTimestamp;
    private String username;
    private boolean enabled;
    private boolean emailVerified;
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private Map<String, List<String>> attributes;


    /**
     * Answer a default instance. This constructor is required to provide proper initialization for
     * collections. Please DO NOT REMOVE
     */
    public UserAuthDTO() {

        super();
        this.setAttributes(new HashMap<String, List<String>>());
    }


    /**
     * Add a single attribute. This is weird crap, but maybe it handles a multi-valued string.
     * Either way, its what keycloak has.
     * @param name String
     * @param value String
     */
    @JsonIgnore
    public void setSingleAttribute(String name,
                                   String value) {

        if (this.getAttributes() == null) {

            this.setAttributes(new HashMap<>());
        }

        this.getAttributes().put(name,
                                 (value == null ? new ArrayList<String>() : Arrays.asList(value)));

    }

    /**
     * Answer my attributes flattened without the inclusive lists
     * @return Map
     */
    @JsonIgnore
    public Map<String, String> asFlattenedAttributes() {

        Map<String, String> tempNewMap = new HashMap<>();

        for (Map.Entry<String,List<String>> anEntry: this.getAttributes().entrySet()) {

            tempNewMap.put(anEntry.getKey(), String.join(",", anEntry.getValue()));
        }

        return tempNewMap;

    }

}
