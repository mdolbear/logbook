package com.mjdsoftware.logbook.domain.entities;

import com.mjdsoftware.logbook.domain.changemgmt.DomainObjectChanges;
import com.mjdsoftware.logbook.dto.ActivityDTO;
import com.mjdsoftware.logbook.dto.CommentDTO;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="activity_discriminator",
                     discriminatorType = DiscriminatorType.STRING)
@Table(name = "activities")
@ToString(exclude="logbookEntry")
@EqualsAndHashCode
public abstract class Activity {

    @Getter
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter @Setter(AccessLevel.PRIVATE)
    @Enumerated(EnumType.STRING)
    @Column(name = "activity_type")
    private ActivityType activityType;

    @Getter @Setter(AccessLevel.PRIVATE)
    @Enumerated(EnumType.STRING)
    @Column(name = "duration_units")
    private DurationUnits durationUnits;


    @Getter @Setter(AccessLevel.PRIVATE)
    @Column(name = "duration")
    private double duration;

    @Version
    @Column(name="version")
    @Getter @Setter(AccessLevel.PRIVATE)
    private long version;

    @Getter @Setter(AccessLevel.PROTECTED)
    @ManyToOne
    @JoinColumn(name="log_entry_id",
                referencedColumnName = "id")
    private LogbookEntry logbookEntry;

    @Getter @Setter()
    @OneToOne(cascade=CascadeType.ALL,
            orphanRemoval = true)
    @JoinColumn(name="activity_details_id",
                referencedColumnName = "id")
    private ActivityDetails activityDetails;

    @Getter @Setter(AccessLevel.PRIVATE)
    @OneToMany(fetch=FetchType.EAGER,
               cascade=CascadeType.ALL,
               orphanRemoval = true)
    @JoinTable(name="activity_comments",
               joinColumns = @JoinColumn(name="activity_id", referencedColumnName="id"),
               inverseJoinColumns = @JoinColumn(name="comment_id", referencedColumnName = "id"))
    private List<Comment> comments;


    /**
     * Answer a default instance
     */
    public Activity() {

        super();
        this.setComments(new ArrayList<>());

    }

    /**
     * Add a comment to me
     * @param aComment Comment
     */
    public void addComment(Comment aComment) {

        this.getComments().add(aComment);
    }

    /**
     * Answer myself as value object
     * @return ActivityDTO
     */
    public abstract ActivityDTO asValueObject();

    /**
     * Update from anActivityDTO
     * @param anActivityDTO ActivityDTO
     */
    public void updateFrom(ActivityDTO anActivityDTO) {

        this.setVersion(anActivityDTO.getVersion()); //For optimistic concurrency check
        this.updateActivityDetails(anActivityDTO);
        this.setActivityType(anActivityDTO.getActivityType());
        this.setValidDuration(anActivityDTO.getDuration(),
                              anActivityDTO.getDurationUnits());

        //Apply comment changes
        this.applyChanges(anActivityDTO);

    }


    /**
     * Update my activity details
     * @param anActivityDTO ActivityDTO
     */
    private void updateActivityDetails(ActivityDTO anActivityDTO) {

        ActivityDetails tempActivityDetails;

        if (anActivityDTO.getActivityDetails() != null) {

            tempActivityDetails = new ActivityDetails();
            tempActivityDetails.updateFrom(anActivityDTO.getActivityDetails());
            this.setActivityDetails(tempActivityDetails);

        }
        else {
            this.setActivityDetails(null);
        }

    }

    /**
     * Apply comment changes to me
     * @param anActivityDTO ActivityDTO
     */
    public void applyChanges(ActivityDTO anActivityDTO) {

        List<Comment>                            tempExistingComments;
        List<CommentDTO>                         tempNewDTOs;
        List<Comment>                            tempCommentsToBeRemoved;
        DomainObjectChanges<CommentDTO, Comment> tempChanges;

        //Get collections required for comment changes
        tempChanges =
                new DomainObjectChanges<CommentDTO, Comment>(anActivityDTO.getComments(),
                                                             this.getComments());
        tempChanges.performChangeAnalysis();

        tempExistingComments = tempChanges.getExistingDomainObjects();
        tempNewDTOs = tempChanges.getNewDTOs();
        tempCommentsToBeRemoved = tempChanges.getDomainObjectsToBeRemoved();

        //Perform removal of the comments I no longer have
        this.getComments().removeAll(tempCommentsToBeRemoved);

        //Modify existing comments that remain
        this.modifyExistingComments(anActivityDTO, tempExistingComments);

        //Add new comments to me
        this.addNewComments(tempNewDTOs);


    }

    /**
     * Add new comments to me
     * @param aNewDTOs List
     */
    private void addNewComments(List<CommentDTO> aNewDTOs) {

        aNewDTOs.stream().forEach(dto->this.addComment(new Comment(dto)));

    }

    /**
     * Modify existing comments
     * @param anActivityDTO ActivityDTO
     * @param anExistingComments
     */
    private void modifyExistingComments(ActivityDTO anActivityDTO,
                                        List<Comment> anExistingComments) {

        for (Comment aComment: anExistingComments) {

            Optional<CommentDTO> tempDTO =
                    anActivityDTO.getComments().stream().filter(dto->dto.hasSameIdAs(aComment.getId())).findFirst();

            if (tempDTO.isPresent()) {
                aComment.updateFrom(tempDTO.get());
            }

        }

    }

    /**
     * Set valid duration
     * @param aDuration double
     * @param aDurationUnits DurationUnits
     */
    public void setValidDuration(double aDuration,
                                 DurationUnits aDurationUnits) {

        if (aDuration < 0.0) {

            throw new IllegalArgumentException("Invalid Duration value");
        }


        this.setDuration(aDuration);
        this.setDurationUnits(aDurationUnits);

    }

}
