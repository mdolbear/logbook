package com.mjdsoftware.logbook.dto.oauth;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.*;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateUserDTO {

    private String id;
    private long createdTimestamp;
    private String username;
    private boolean enabled;
    private boolean emailVerified;
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private Map<String, List<String>> attributes;
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    protected List<CredentialsDTO> credentials;

    /**
     * Answer a default instance of me
     */
    public CreateUserDTO() {

        super();
        this.setAttributes(new HashMap<>());
        this.setCredentials(new ArrayList<CredentialsDTO>());
        this.setCreatedTimestamp((new Date()).getTime());
        this.setEnabled(true);
        this.setEmailVerified(false);
    }

    /**
     * Answer an instance of me for aUsername
     * @param aUsername String
     */
    public CreateUserDTO(String aUsername) {

        this();
        this.setUsername(aUsername);

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
     * Add a credential to me
     * @param aCredential CredentialsDTO
     */
    @JsonIgnore
    public void addCredential(CredentialsDTO aCredential) {

        this.getCredentials().add(aCredential);

    }



}
