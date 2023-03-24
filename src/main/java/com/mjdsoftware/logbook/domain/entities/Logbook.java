package com.mjdsoftware.logbook.domain.entities;

import com.mjdsoftware.logbook.dto.LogbookDTO;
import jakarta.persistence.*;
import lombok.*;

import java.util.Calendar;

@Entity
@Table(name = "logbooks")
@NoArgsConstructor
@ToString()
@EqualsAndHashCode
public class Logbook {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @Column(name = "name")
    @Getter @Setter
    private String name;

    @Getter @Setter(AccessLevel.PRIVATE)
    @Column(name = "start_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar startDate;


    @Version
    @Column(name="version")
    @Getter @Setter(AccessLevel.PRIVATE)
    private long version;

    /**
     * Save create date/time on pre-persist callback
     */
    @PrePersist
    public void prePersist() {

        if (this.getStartDate() == null) {
            this.setStartDate(Calendar.getInstance());
        }

    }

    /**
     * Answer myself as a value object
     * @return LogbookDTO
     */
    public LogbookDTO asValueObject() {

        return new LogbookDTO(this.getId(),
                             this.getName(),
                             this.getStartDate(),
                             this.getVersion());
    }

    /**
     * Update myself from aValueObject
     * @param aValueObject LogbookDTO
     */
    public void updateFrom(LogbookDTO aValueObject) {

        this.setName(aValueObject.getName()); //Name must be unique
        this.setVersion(aValueObject.getVersion()); //For optimistic concurrency check

    }


    //This may be the best site for statistical methods, even though
    //we don't have direct access to the underlying LogEntry objects

}
