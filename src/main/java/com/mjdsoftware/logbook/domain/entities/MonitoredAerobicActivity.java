package com.mjdsoftware.logbook.domain.entities;

import com.mjdsoftware.logbook.dto.ActivityDTO;
import com.mjdsoftware.logbook.dto.CommentDTO;
import com.mjdsoftware.logbook.dto.MonitoredAerobicActivityDTO;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Entity
@DiscriminatorValue("MON_AEROBIC")
public class MonitoredAerobicActivity extends Activity {

    @Getter
    @Setter(AccessLevel.PRIVATE)
    @Column(name = "distance")
    private double distance;

    @Getter @Setter(AccessLevel.PRIVATE)
    @Enumerated(EnumType.STRING)
    @Column(name = "distance_units")
    private DistanceUnits distanceUnits;

    @Getter
    @Setter(AccessLevel.PRIVATE)
    @Column(name = "avg_watts")
    private double averageWatts;

    @Getter
    @Setter(AccessLevel.PRIVATE)
    @Column(name = "total_calories")
    private double totalCalories;

    @Getter
    @Setter(AccessLevel.PRIVATE)
    @Column(name = "avg_heart_rate")
    private double averageHeartRate;

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

        return new MonitoredAerobicActivityDTO(this.getId(),
                                                this.getActivityType(),
                                                this.getDurationUnits(),
                                                this.getDuration(),
                                                this.getVersion(),
                                                this.getActivityDetails().getDetails(),
                                                tempComments,
                                                this.getDistance(),
                                                this.getDistanceUnits(),
                                                this.getAverageWatts(),
                                                this.getTotalCalories(),
                                                this.getAverageHeartRate());

    }

    /**
     * Update from anActivityDTO
     * @param anActivityDTO MonitoredAerobicActivityDTO
     */
    @Override
    public void updateFrom(ActivityDTO anActivityDTO) {

        MonitoredAerobicActivityDTO tempActivityDTO;

        super.updateFrom(anActivityDTO);

        tempActivityDTO = (MonitoredAerobicActivityDTO)anActivityDTO;
        this.setDistance(tempActivityDTO.getDistance());
        this.setDistanceUnits(tempActivityDTO.getDistanceUnits());
        this.setAverageHeartRate(tempActivityDTO.getAverageHeartRate());
        this.setAverageWatts(tempActivityDTO.getAverageWatts());
        this.setTotalCalories(tempActivityDTO.getTotalCalories());

    }


}
