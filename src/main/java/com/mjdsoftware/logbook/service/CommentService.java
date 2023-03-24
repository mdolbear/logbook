package com.mjdsoftware.logbook.service;

import com.mjdsoftware.logbook.domain.entities.Activity;
import com.mjdsoftware.logbook.domain.entities.Comment;
import com.mjdsoftware.logbook.domain.entities.LogbookEntry;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CommentService {


    /**
     * Delete comments for LogbookEntry and aComments
     *
     * @param aLogbookEntry LogbookEntry
     * @param aComments     List
     */
    void deleteComments(LogbookEntry aLogbookEntry, List<Comment> aComments);

    /**
     * Delete comments for Activity and aComments
     *
     * @param anActivity Activity
     * @param aComments  List
     */
    void deleteComments(Activity anActivity, List<Comment> aComments);

}
