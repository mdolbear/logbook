package com.mjdsoftware.logbook.domain.entities;

import com.mjdsoftware.logbook.domain.changemgmt.DomainObjectChanges;
import com.mjdsoftware.logbook.dto.ActivityDTO;
import com.mjdsoftware.logbook.dto.CommentDTO;
import com.mjdsoftware.logbook.dto.LogbookEntryDTO;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "logbook_entries")
@ToString(exclude="logbook")
@EqualsAndHashCode
public class LogbookEntry {

    @Getter
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter @Setter(AccessLevel.PRIVATE)
    @Column(name = "activity_date")
    @Temporal(TemporalType.DATE)
    private Calendar activityDate;

    @Version
    @Column(name="version")
    @Getter @Setter(AccessLevel.PRIVATE)
    private long version;

    @Getter @Setter
    @ManyToOne
    @JoinColumn(name="logbook_id",
                referencedColumnName = "id")
    private Logbook logbook;

    @Getter @Setter(AccessLevel.PRIVATE)
    @OneToMany(fetch=FetchType.EAGER,
               mappedBy="logbookEntry")
    private List<Activity> activities;

    @Getter @Setter(AccessLevel.PRIVATE)
    @OneToMany(fetch=FetchType.EAGER,
               cascade=CascadeType.ALL,
               orphanRemoval = true)
    @JoinTable(name="log_entry_comments",
               joinColumns = @JoinColumn(name="log_entry_id", referencedColumnName="id"),
               inverseJoinColumns = @JoinColumn(name="comment_id", referencedColumnName = "id"))
    private List<Comment> comments;


    /**
     * Answer a default instance
     */
    public LogbookEntry() {

        super();
        this.setActivities(new ArrayList<>());
        this.setComments(new ArrayList<>());

    }

    /**
     * Save create date/time on pre-persist callback
     */
    @PrePersist
    public void prePersist() {

        if (this.getActivityDate() == null) {
            this.setActivityDate(Calendar.getInstance());
        }

    }

    /**
     * Add a comment to me
     * @param aComment Comment
     */
    private void addComment(Comment aComment) {

        this.getComments().add(aComment);
    }

    /**
     * Add anActivity to me
     * @param anActivity Activity
     */
    public void addActivity(Activity anActivity) {

        this.getActivities().add(anActivity);
        anActivity.setLogbookEntry(this);
    }

    /**
     * Answer myself as a value object
     * @return LogbookEntryDTO
     */
    public LogbookEntryDTO asValueObject() {

        List<CommentDTO> tempComments;
        List<ActivityDTO> tempActivities;


        tempComments =
                this.getComments().stream()
                                  .map(co->co.asValueObject())
                                  .collect(Collectors.toList());

        tempActivities =
                this.getActivities().stream()
                                    .map(act->act.asValueObject())
                                    .collect(Collectors.toList());


        return new LogbookEntryDTO(this.getId(),
                                   this.getActivityDate(),
                                   this.getVersion(),
                                   tempActivities,
                                   tempComments);

    }

    /**
     * Update myself from aValueObject
     * @param aValueObject LogbookEntryDTO
     */
    public void updateFrom(LogbookEntryDTO aValueObject) {

        this.setVersion(aValueObject.getVersion()); //For optimistic concurrency check
        this.applyChanges(aValueObject); //Apply comment changes

    }

    /**
     * Apply comment changes to me
     * @param aLogbookEntryDTO ActivityDTO
     */
    private void applyChanges(LogbookEntryDTO aLogbookEntryDTO) {

        List<Comment>                            tempExistingComments;
        List<CommentDTO>                         tempNewDTOs;
        List<Comment>                            tempCommentsToBeRemoved;
        DomainObjectChanges<CommentDTO, Comment> tempChanges;

        //Get collections required for comment changes
        tempChanges =
                new DomainObjectChanges<CommentDTO, Comment>(aLogbookEntryDTO.getComments(),
                                                             this.getComments());
        tempChanges.performChangeAnalysis();

        tempExistingComments = tempChanges.getExistingDomainObjects();
        tempNewDTOs = tempChanges.getNewDTOs();
        tempCommentsToBeRemoved = tempChanges.getDomainObjectsToBeRemoved();

        //Perform removal of the comments I no longer have
        this.getComments().removeAll(tempCommentsToBeRemoved);

        //Modify existing comments that remain
        this.modifyExistingComments(aLogbookEntryDTO, tempExistingComments);

        //Add new comments to me
        this.addNewComments(tempNewDTOs);


    }

    /**
     * Add new comments to me
     * @param tempNewDTOs List
     */
    private void addNewComments(List<CommentDTO> tempNewDTOs) {

        for (CommentDTO aDTO: tempNewDTOs) {

            this.addComment(new Comment(aDTO));

        }

    }

    /**
     * Modify existing comments
     * @param aLogbookEntryDTo LogbookEntryDTO
     * @param anExistingComments
     */
    private void modifyExistingComments(LogbookEntryDTO aLogbookEntryDTo,
                                        List<Comment> anExistingComments) {

        for (Comment aComment: anExistingComments) {

            Optional<CommentDTO> tempDTO =
                    aLogbookEntryDTo.getComments()
                                    .stream()
                                    .filter(dto->dto.hasSameIdAs(aComment.getId()))
                                    .findFirst();

            if (tempDTO.isPresent()) {
                aComment.updateFrom(tempDTO.get());
            }

        }

    }

}
