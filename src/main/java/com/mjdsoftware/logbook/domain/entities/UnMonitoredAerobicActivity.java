package com.mjdsoftware.logbook.domain.entities;

import com.mjdsoftware.logbook.dto.ActivityDTO;
import com.mjdsoftware.logbook.dto.CommentDTO;
import com.mjdsoftware.logbook.dto.MonitoredAerobicActivityDTO;
import com.mjdsoftware.logbook.dto.UnMonitoredAerobicActivityDTO;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Entity
@DiscriminatorValue("UNMON_AEROBIC")
public class UnMonitoredAerobicActivity extends Activity {

    @Getter
    @Setter(AccessLevel.PRIVATE)
    @Column(name = "distance")
    private double distance;

    @Getter @Setter(AccessLevel.PRIVATE)
    @Enumerated(EnumType.STRING)
    @Column(name = "distance_units")
    private DistanceUnits distanceUnits;


    /**
     * Answer myself as value object
     * @return ActivityDTO
     */
    @Override
    public ActivityDTO asValueObject() {

        List<CommentDTO> tempComments;

        tempComments =
                this.getComments().stream()
                        .map(co->co.asValueObject())
                        .collect(Collectors.toList());

        return new UnMonitoredAerobicActivityDTO(this.getId(),
                                                this.getActivityType(),
                                                this.getDurationUnits(),
                                                this.getDuration(),
                                                this.getVersion(),
                                                this.getActivityDetails().getDetails(),
                                                tempComments,
                                                this.getDistance(),
                                                this.getDistanceUnits());

    }

    /**
     * Update from anActivityDTO
     * @param anActivityDTO MonitoredAerobicActivityDTO
     */
    @Override
    public void updateFrom(ActivityDTO anActivityDTO) {

        UnMonitoredAerobicActivityDTO tempActivityDTO;

        super.updateFrom(anActivityDTO);

        tempActivityDTO = (UnMonitoredAerobicActivityDTO)anActivityDTO;
        this.setDistance(tempActivityDTO.getDistance());
        this.setDistanceUnits(tempActivityDTO.getDistanceUnits());

    }


}
