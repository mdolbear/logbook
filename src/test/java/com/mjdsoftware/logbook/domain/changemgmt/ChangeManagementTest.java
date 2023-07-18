package com.mjdsoftware.logbook.domain.changemgmt;

import com.mjdsoftware.logbook.domain.entities.Comment;
import com.mjdsoftware.logbook.domain.entities.StrengthTrainingActivity;
import com.mjdsoftware.logbook.dto.CommentDTO;
import com.mjdsoftware.logbook.dto.StrengthTrainingActivityDTO;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class ChangeManagementTest {


    /**
     * Perform change management test. This test has a mix of added new objects,
     * removal of a domain object, and keeps existing objects in dto
     */
    @Test
    public void performChangeManagementTestRemovingOneKeepingExistingAndAddingNewObjects() {

        StrengthTrainingActivityDTO tempDTO = this.creatDTOObjects1();
        StrengthTrainingActivity tempDomainObject = this.creatDomainObjects1();

        tempDomainObject.applyChanges(tempDTO);
        assertTrue(this.hasExpectedComments1(tempDomainObject),
         "Expected objects not in collection");
    }

    /**
     * Answer true if expected object are in the resulting collection of anActivity
     * @param anActivity Activity
     * @return
     */
    public boolean hasExpectedComments1(StrengthTrainingActivity anActivity) {

        boolean tempCond1;
        boolean tempCond2;
        boolean tempCond3;
        boolean tempCond4;

        tempCond1 =
                !anActivity.getComments()
                          .stream()
                          .filter(comment->comment.getCommentContents().equals("New Comment 1") &&
                                                comment.getId() == null)
                          .collect(Collectors.toList()).isEmpty();

        tempCond2 =
                !anActivity.getComments()
                          .stream()
                          .filter(comment->comment.hasSameIdAs(Long.valueOf(2)) &&
                                comment.getCommentContents().equals("Existing Comment 2"))
                          .collect(Collectors.toList()).isEmpty();

        tempCond3 =
                !anActivity.getComments()
                          .stream()
                          .filter(comment->comment.hasSameIdAs(Long.valueOf(3)) &&
                                comment.getCommentContents().equals("Existing Comment 3"))
                          .collect(Collectors.toList()).isEmpty();

        tempCond4 =
                !anActivity.getComments()
                          .stream()
                          .filter(comment->comment.getCommentContents().equals("New Comment 4") &&
                                        comment.getId() == null)
                          .collect(Collectors.toList()).isEmpty();


        return tempCond1 && tempCond2 && tempCond3 && tempCond4;

    }



    /**
     * Create dto objects
     * @return ActivityDTO
     */
    public StrengthTrainingActivityDTO creatDTOObjects1() {

        StrengthTrainingActivityDTO tempDTO;

        tempDTO = new StrengthTrainingActivityDTO();
        tempDTO.addComment(new CommentDTO(null,
                         "New Comment 1",
                                  0,
                                null));
        tempDTO.addComment(new CommentDTO(Long.valueOf(2),
                          "Existing Comment 2",
                                   0,
                                 null));
        tempDTO.addComment(new CommentDTO(Long.valueOf(3),
                         "Existing Comment 3",
                                  0,
                                null));
        tempDTO.addComment(new CommentDTO(null,
                         "New Comment 4",
                                  0,
                                null));

        return tempDTO;

    }


    /**
     * Create domain objects
     * @return StrengthTrainingActivity
     */
    public StrengthTrainingActivity creatDomainObjects1() {

        StrengthTrainingActivity tempDomainObject;

        tempDomainObject = new StrengthTrainingActivity();

        tempDomainObject.addComment(new Comment(Long.valueOf(2),
                "Existing Comment 2",
                0,
                null));
        tempDomainObject.addComment(new Comment(Long.valueOf(3),
                "Existing Comment 3",
                0,
                null));
        tempDomainObject.addComment(new Comment(Long.valueOf(5),
                      "Removed Comment 5",
                               0,
                             null));

        return tempDomainObject;

    }



    /**
     * Perform change management test. This test add all new objects from the dto
     * into the domain object
     */
    @Test
    public void performChangeManagementTestAllNewObjectsAdded() {

        StrengthTrainingActivityDTO tempDTO = this.creatDTOObjects2();
        StrengthTrainingActivity tempDomainObject = this.creatDomainObjects2();

        tempDomainObject.applyChanges(tempDTO);
        assertTrue(this.hasExpectedComments2(tempDomainObject),
                "Expected objects not in collection");
    }

    /**
     * Answer true if expected object are in the resulting collection of anActivity
     * @param anActivity Activity
     * @return
     */
    public boolean hasExpectedComments2(StrengthTrainingActivity anActivity) {

        boolean tempCond1;
        boolean tempCond2;
        boolean tempCond3;
        boolean tempCond4;

        tempCond1 =
                !anActivity.getComments()
                        .stream()
                        .filter(comment->comment.getCommentContents().equals("New Comment 1") &&
                                comment.getId() == null)
                        .collect(Collectors.toList()).isEmpty();

        tempCond2 =
                !anActivity.getComments()
                        .stream()
                        .filter(comment->comment.getCommentContents().equals("New Comment 2") &&
                                        comment.getId() == null)
                        .collect(Collectors.toList()).isEmpty();

        tempCond3 =
                !anActivity.getComments()
                        .stream()
                        .filter(comment->comment.getCommentContents().equals("New Comment 3") &&
                                    comment.getId() == null)
                        .collect(Collectors.toList()).isEmpty();

        tempCond4 =
                !anActivity.getComments()
                        .stream()
                        .filter(comment->comment.getCommentContents().equals("New Comment 4") &&
                                    comment.getId() == null)
                        .collect(Collectors.toList()).isEmpty();


        return tempCond1 && tempCond2 && tempCond3 && tempCond4;

    }



    /**
     * Create dto objects
     * @return ActivityDTO
     */
    public StrengthTrainingActivityDTO creatDTOObjects2() {

        StrengthTrainingActivityDTO tempDTO;

        tempDTO = new StrengthTrainingActivityDTO();
        tempDTO.addComment(new CommentDTO(null,
                "New Comment 1",
                0,
                null));
        tempDTO.addComment(new CommentDTO(null,
                "New Comment 2",
                0,
                null));
        tempDTO.addComment(new CommentDTO(null,
                "New Comment 3",
                0,
                null));
        tempDTO.addComment(new CommentDTO(null,
                "New Comment 4",
                0,
                null));

        return tempDTO;

    }


    /**
     * Create domain objects
     * @return StrengthTrainingActivity
     */
    public StrengthTrainingActivity creatDomainObjects2() {

        StrengthTrainingActivity tempDomainObject;

        tempDomainObject = new StrengthTrainingActivity();

        return tempDomainObject;

    }


    /**
     * Perform change management test. This object removes all objects in the domain object since
     * the dto is empty.
     */
    @Test
    public void performChangeManagementTestAllObjectsRemovedFromDomainObject() {

        StrengthTrainingActivityDTO tempDTO = this.creatDTOObjects3();
        StrengthTrainingActivity tempDomainObject = this.creatDomainObjects3();

        tempDomainObject.applyChanges(tempDTO);
        assertTrue(this.hasExpectedComments3(tempDomainObject),
                "No objects should be in collection");
    }

    /**
     * Answer true if expected object are in the resulting collection of anActivity
     * @param anActivity Activity
     * @return
     */
    public boolean hasExpectedComments3(StrengthTrainingActivity anActivity) {

        boolean tempCond1;


        tempCond1 =
                anActivity.getComments().isEmpty();


        return tempCond1 ;

    }



    /**
     * Create dto objects
     * @return StrengthTrainingActivityDTO
     */
    public StrengthTrainingActivityDTO creatDTOObjects3() {

        StrengthTrainingActivityDTO tempDTO;

        tempDTO = new StrengthTrainingActivityDTO();

        return tempDTO;

    }


    /**
     * Create domain objects
     * @return StrengthTrainingActivity
     */
    public StrengthTrainingActivity creatDomainObjects3() {

        StrengthTrainingActivity tempDomainObject;

        tempDomainObject = new StrengthTrainingActivity();

        tempDomainObject.addComment(new Comment(Long.valueOf(2),
                "Existing Comment 2",
                0,
                null));
        tempDomainObject.addComment(new Comment(Long.valueOf(3),
                "Existing Comment 3",
                0,
                null));
        tempDomainObject.addComment(new Comment(Long.valueOf(5),
                "Existing Comment 5",
                0,
                null));

        return tempDomainObject;

    }

}
