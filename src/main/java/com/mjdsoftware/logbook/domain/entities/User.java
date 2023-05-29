package com.mjdsoftware.logbook.domain.entities;

import com.mjdsoftware.logbook.dto.LogbookDTO;
import com.mjdsoftware.logbook.dto.UserDTO;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
@ToString()
@EqualsAndHashCode
public class User {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @Column(name = "user_name")
    @Getter @Setter(AccessLevel.PRIVATE)
    private String username;

    @Getter @Setter(AccessLevel.PRIVATE)
    @OneToMany(fetch=FetchType.EAGER,
                mappedBy="user")
    private List<Logbook> logbooks;

    @Version
    @Column(name="version")
    @Getter @Setter(AccessLevel.PRIVATE)
    private long version;

    /**
     * Answer a default instance
     */
    public User() {

        this.setLogbooks(new ArrayList<Logbook>());
    }

    /**
     * Answer an instance of me for aUsername
     * @param aUsername String
     */
    public User(String aUsername) {

        this();
        this.setUsername(aUsername);
    }


    /**
     * Add a logbook to me
     * @param aLogbook Logbook
     */
    public void addLogbook(Logbook aLogbook) {

        this.getLogbooks().add(aLogbook);
        aLogbook.setUser(this);
    }

    /**
     * Answer myself as a value object
     * @return UserDTO
     */
    public UserDTO asValueObject() {

        UserDTO tempUser;
        List<LogbookDTO> tempLogbookDTOs;

        tempLogbookDTOs = this.getLogbooks()
                              .stream()
                              .map(perObj -> perObj.asValueObject())
                              .collect(Collectors.toList());

        tempUser =
                new UserDTO(this.getId(),
                            this.getUsername(),
                            new ArrayList<LogbookDTO>(),
                            this.getVersion());
        tempUser.setLogbooks(tempLogbookDTOs);

        return tempUser;

    }

    /**
     * Update myself from aValueObject
     * @param aValueObject UserDTO
     */
    public void updateFrom(UserDTO aValueObject) {

        this.setUsername(aValueObject.getUsername());
        this.setVersion(aValueObject.getVersion());

    }

}
