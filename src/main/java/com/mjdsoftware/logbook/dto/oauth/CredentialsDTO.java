package com.mjdsoftware.logbook.dto.oauth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CredentialsDTO {

    private String id;
    private String type;
    private String userLabel;
    private Long createdDate;
    private String secretData;
    private String credentialData;
    private Integer priority;
    private String value;

    // only used when updating a credential.  Might set required action
    private Boolean temporary;


    //Constants - CREDENTIALS TYPE VALUES
    public static final String SECRET = "secret";
    public static final String PASSWORD = "password";
    public static final String TOTP = "totp";
    public static final String HOTP = "hotp";
    public static final String KERBEROS = "kerberos";

    /**
     * Answer an instance of me for aPassword with the proper defaults
     * set
     */
    public CredentialsDTO(String aPassword) {

        super();
        this.setType(PASSWORD);
        this.setTemporary(false);
        this.setValue(aPassword);

        this.setCreatedDate((new Date()).getTime());
    }

}
