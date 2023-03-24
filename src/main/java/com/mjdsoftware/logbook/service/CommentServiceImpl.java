package com.mjdsoftware.logbook.service;

import com.mjdsoftware.logbook.domain.entities.Activity;
import com.mjdsoftware.logbook.domain.entities.Comment;
import com.mjdsoftware.logbook.domain.entities.LogbookEntry;
import com.mjdsoftware.logbook.domain.repositories.CommentRepository;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CommentServiceImpl implements CommentService {

    @Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE)
    private CommentRepository commentRepository;

    /**
     * Answer my logger
     *
     * @return org.slf4j.Logger
     */
    private static Logger getLogger() {
        return log;
    }

    /**
     * Answer an instance of me on aRepository
     * @param aRepository CommentRepository
     */
    @Autowired
    public CommentServiceImpl(CommentRepository aRepository) {

        super();
        this.setCommentRepository(aRepository);

    }


    /**
     * Delete comments for LogbookEntry and aComments
     * @param aLogbookEntry LogbookEntry
     * @param aComments List
     */
    @Override
    @Transactional
    public void deleteComments(@NonNull LogbookEntry aLogbookEntry,
                               @NonNull List<Comment> aComments) {

        Set<Long> tempCommentIds;

        tempCommentIds = aComments.stream()
                                  .map(c->c.getId())
                                  .collect(Collectors.toSet());

        getLogger().info("Delete all comments for logBooEntryWithId: {} and commentIds: {}",
                          aLogbookEntry.getId(),
                          tempCommentIds);

        this.getCommentRepository().deleteByLogbookEntryId(aLogbookEntry.getId());
        this.getCommentRepository().deleteAllByIdInBatch(tempCommentIds);

        getLogger().info("Delete all comments for logBooEntryWithId and comments completed");

    }

    /**
     * Delete comments for Activity and aComments
     * @param anActivity Activity
     * @param aComments List
     */
    @Override
    @Transactional
    public void deleteComments(@NonNull Activity anActivity,
                               @NonNull List<Comment> aComments) {

        Set<Long> tempCommentIds;

        tempCommentIds = aComments.stream()
                                  .map(Comment::getId) //Use with Method reference
                                  .collect(Collectors.toSet());

        getLogger().info("Delete all comments for activiyId: {} and commentIds: {}",
                         anActivity.getId(),
                         tempCommentIds);

        this.getCommentRepository().deleteByActivityId(anActivity.getId());
        this.getCommentRepository().deleteAllByIdInBatch(tempCommentIds);

        getLogger().info("Delete all comments for activiyId and comments completed");

    }


}
