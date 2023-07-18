package com.mjdsoftware.logbook.domain.entities;

import com.mjdsoftware.logbook.dto.ActivityDTO;
import com.mjdsoftware.logbook.dto.CommentDTO;
import com.mjdsoftware.logbook.dto.StrengthTrainingActivityDTO;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.util.List;
import java.util.stream.Collectors;

@Entity
@DiscriminatorValue("STRENGTH")
public class StrengthTrainingActivity extends Activity {

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

        return new StrengthTrainingActivityDTO(this.getId(),
                                       this.getActivityType(),
                                       this.getDurationUnits(),
                                       this.getDuration(),
                                       this.getVersion(),
                                       this.getActivityDetails().getDetails(),
                                       tempComments);

    }


}
