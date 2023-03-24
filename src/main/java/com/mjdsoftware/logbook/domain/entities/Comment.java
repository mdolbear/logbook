package com.mjdsoftware.logbook.domain.entities;

import com.mjdsoftware.logbook.dto.CommentDTO;
import jakarta.persistence.*;
import lombok.*;

import java.util.Calendar;

@Entity
@Table(name = "comments")
@NoArgsConstructor
@AllArgsConstructor
@ToString()
@EqualsAndHashCode
public class Comment implements DomainObjectCollectionEntry {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @Column(name = "details")
    @Getter @Setter
    private String commentContents;

    @Version
    @Column(name="version")
    @Getter @Setter(AccessLevel.PRIVATE)
    private long version;

    @Column(name = "created_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @Getter @Setter(AccessLevel.PRIVATE)
    private Calendar createdAt;

    /**
     * Answer an instance of me for aCommentDTO
     * @param aCommentDTO CommentDTo
     */
    public Comment(CommentDTO aCommentDTO) {

        this();
        this.setCommentContents(aCommentDTO.getCommentContents());

    }

    /**
     * Save create date/time on pre-persist callback
     */
    @PrePersist
    public void prePersist() {

        if (this.getCreatedAt() == null) {
            this.setCreatedAt(Calendar.getInstance());
        }

    }


    /**
     * Answer myself as a value object
     * @return CommentDTO
     */
    public CommentDTO asValueObject() {

        return new CommentDTO(this.getId(),
                            this.getCommentContents(),
                            this.getVersion(),
                            this.getCreatedAt());
    }

    /**
     * Update myself from aValueObject
     * @param aValueObject CommentDTO
     */
    public void updateFrom(CommentDTO aValueObject) {

        this.setCommentContents(aValueObject.getCommentContents());
        this.setVersion(aValueObject.getVersion()); //For optimistic concurrency check

    }


}
