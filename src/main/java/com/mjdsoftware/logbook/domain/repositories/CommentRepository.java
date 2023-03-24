package com.mjdsoftware.logbook.domain.repositories;

import com.mjdsoftware.logbook.domain.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Modifying
    @Query(value="DELETE FROM log_entry_comments WHERE logbook_id = ?1)", nativeQuery=true)
    public void deleteByLogbookEntryId(Long logbookEntryId);

    @Modifying
    @Query(value="DELETE FROM activity_comments WHERE  activity_id = ?1)", nativeQuery=true)
    public void deleteByActivityId(Long activityId);
    
}
