package com.mjdsoftware.logbook.domain.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "activity_details")
@ToString()
@NoArgsConstructor
public class ActivityDetails {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @Column(name = "details")
    @Getter @Setter
    private String details;

    @Version
    @Column(name="version")
    @Getter @Setter(AccessLevel.PRIVATE)
    private long version;


    /**
     * Update from aDetails
     * @param aDetails String
     */
    public void updateFrom(String aDetails) {

        this.setDetails(aDetails);
    }

}
