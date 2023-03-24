package com.mjdsoftware.logbook;

import com.mjdsoftware.logbook.api.LogbookController;
import com.mjdsoftware.logbook.domain.entities.ActivityType;
import com.mjdsoftware.logbook.domain.entities.DurationUnits;
import com.mjdsoftware.logbook.dto.ActivityDTO;
import com.mjdsoftware.logbook.dto.CommentDTO;
import com.mjdsoftware.logbook.dto.LogbookDTO;
import com.mjdsoftware.logbook.dto.LogbookEntryDTO;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
@DirtiesContext
public class LogbookApplicationTests {

	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE)
	@Autowired
	private LogbookController logbookController;


	/**
	 * Test Springboot ApplicationContext loading test
	 */
	@Test
	void contextLoads() {
	}

	/**
	 * Find all logbooks test
	 */
	@Test
	public void findAllLogbooks() {

		ResponseEntity<List<LogbookDTO>> tempLogbooks;


		tempLogbooks = this.getLogbookController().findAllLogbooks();
		assertTrue(this.isFindAllLogbooksResultValid(tempLogbooks),
			"Find All Logbooks Failed");

	}

	/**
	 * Create logbook test
	 */
	@Test
	public void createLogbook() {

		LogbookDTO tempLogbook = basicCreateLogbook();
		ResponseEntity<LogbookDTO> tempResult;

		tempResult = this.getLogbookController().findLogbookByName(tempLogbook.getName());
		assertTrue(this.isFindByNameLogbookResultValid(tempResult, tempLogbook.getName()),
				"Logbook find by name failed");
	}

	/**
	 * Basic create logbook. And return it
	 * @return LogbookDTO
	 */
	private LogbookDTO basicCreateLogbook() {

		LogbookDTO 					tempLogbook;
		ResponseEntity<LogbookDTO>	tempResult;

		tempLogbook = new LogbookDTO();
		tempLogbook.setName("NewLogbook" + (new Date()).getTime());

		tempResult = this.getLogbookController().createLogbook(tempLogbook);
		assertTrue(this.isCreateLogbookResultValid(tempResult),
					  "Logbook creation failed");
		tempLogbook = tempResult.getBody();

		return tempLogbook;
	}

	/**
	 * Answer whether the find all logbooks result is valid
	 * @param aLogbooks ResponseEntity
	 * @return boolean
	 */
	private boolean isFindAllLogbooksResultValid(ResponseEntity<List<LogbookDTO>> aLogbooks) {

		return aLogbooks.getStatusCode().equals(HttpStatus.OK) &&
				!aLogbooks.getBody().isEmpty();
	}

	/**
	 * Answer whether the create logbook result is valid
	 * @param aLogbook ResponseEntity
	 * @return boolean
	 */
	private boolean isCreateLogbookResultValid(ResponseEntity<LogbookDTO> aLogbook) {

		return aLogbook.getStatusCode().equals(HttpStatus.OK) &&
				aLogbook.getBody().getId() != null;
	}

	/**
	 * Answer whether the create logbookEntry result is valid
	 * @param aLogbookEntry ResponseEntity
	 * @return boolean
	 */
	private boolean isCreateLogbookEntryResultValid(ResponseEntity<LogbookEntryDTO> aLogbookEntry) {

		return aLogbookEntry.getStatusCode().equals(HttpStatus.OK) &&
					aLogbookEntry.getBody().getId() != null;
	}

	/**
	 * Answer whether the logbook result is valid
	 * @param aLogbook ResponseEntity
	 * @param aName String
	 * @return boolean
	 */
	private boolean isFindByNameLogbookResultValid(ResponseEntity<LogbookDTO> aLogbook,
												   String aName) {

		return aLogbook.getStatusCode().equals(HttpStatus.OK) &&
				aLogbook.getBody().getId() != null &&
					aLogbook.getBody().getName() != null &&
						aLogbook.getBody().getName().equals(aName);

	}

	/**
	 * Create and delete logbook entry
	 */
	@Test
	public void createAndDeleteLogbookEntry() {

		LogbookDTO 												tempLogbookDTO;
		LogbookEntryDTO 									    tempEntryDTO;
		ResponseEntity<List<LogbookEntryDTO>>					tempResults;
		List<LogbookEntryDTO>									tempEntries;


		//Create logbook and logbook entries
		tempLogbookDTO = this.basicCreateLogbook();
		tempEntryDTO = this.basicCreateLogbookEntry(tempLogbookDTO);

		//Try to find them. We should find at least one
		tempResults = this.getLogbookController()
						  .findLogbookEntries(tempLogbookDTO.getId(),
								  			  0, 10);
		assertTrue(this.isFindByIdLogbookEntriesResultValid(tempResults),
				"Logbook entry find paged failed");

		//Delete created entry
		this.getLogbookController().deleteLogbookEntry(tempEntryDTO.getId());

		//Try to find them. We should not find any
		tempResults = this.getLogbookController()
						  .findLogbookEntries(tempLogbookDTO.getId(),
								  0,
								     10);
		assertTrue(this.isFindByIdLogbookEntriesResultEmpty(tempResults),
				"Logbook entry find paged not empty");

	}

	/**
	 * Answer whether the logbook entey results are valid
	 * @param anEntries ResponseEntity
	 * @return boolean
	 */
	private boolean isFindByIdLogbookEntriesResultValid(ResponseEntity<List<LogbookEntryDTO>> anEntries) {

		return anEntries.getStatusCode().equals(HttpStatus.OK) &&
				anEntries.getBody() != null &&
					!anEntries.getBody().isEmpty();

	}

	/**
	 * Answer whether the logbook entey results are valid
	 * @param anEntries ResponseEntity
	 * @return boolean
	 */
	private boolean isFindByIdLogbookEntriesResultEmpty(ResponseEntity<List<LogbookEntryDTO>> anEntries) {

		return anEntries.getStatusCode().equals(HttpStatus.OK) &&
				anEntries.getBody() != null &&
				  anEntries.getBody().isEmpty();

	}

	/**
	 * Create a logbookEntry
	 * @param aLogbookDTO LogbookDTO
	 * @return LogbookEntryDTO
	 */
	private LogbookEntryDTO basicCreateLogbookEntry(LogbookDTO aLogbookDTO) {


		ResponseEntity<LogbookEntryDTO> 		tempResult;
		LogbookEntryDTO 						tempEntryDTO;


		tempEntryDTO = this.createLogbookEntryDTO();
		tempResult =
				this.getLogbookController()
					.createLogbookEntry(aLogbookDTO.getId(),
										tempEntryDTO);

		assertTrue(this.isCreateLogbookEntryResultValid(tempResult),
				"LogbookEntry creation failed");
		tempEntryDTO = tempResult.getBody();

		return tempEntryDTO;


	}

	/**
	 * Create logbook entry dto
	 * @return LogbookEntryDTO
	 */
	private LogbookEntryDTO createLogbookEntryDTO() {

		LogbookEntryDTO tempEntry;

		tempEntry = new LogbookEntryDTO();


		tempEntry.addComment(new CommentDTO(null,
									"New Comment 1",
									0,
									null));
		tempEntry.addComment(new CommentDTO(null,
									"New Comment 2",
									0,
									null));
		tempEntry.addComment(new CommentDTO(null,
										"New Comment 3",
										0,
										null));
		tempEntry.addComment(new CommentDTO(null,
											"New Comment 4",
											0,
											null));


		return tempEntry;

	}


	/**
	 * Create and delete logbook entry
	 */
	@Test
	public void createAndDeleteActivity() {

		LogbookDTO 												tempLogbookDTO;
		LogbookEntryDTO 									    tempEntryDTO;
		ResponseEntity<List<LogbookEntryDTO>>					tempLogbookEntryResults;
		ResponseEntity<ActivityDTO>								tempActivityResult;
		ActivityDTO												tempActivity;
		ResponseEntity<List<ActivityDTO>>						tempActivityResults;


		//Create logbook and logbook entries
		tempLogbookDTO = this.basicCreateLogbook();
		tempEntryDTO = this.basicCreateLogbookEntry(tempLogbookDTO);

		//Create activity
		tempActivityResult = this.getLogbookController().createLActivity(tempLogbookDTO.getId(),
																		 tempEntryDTO.getId(),
																		 this.createActivityDTO());

		assertTrue(this.isCreateActivityResultValid(tempActivityResult),
				"Create activity failed");
		tempActivity = tempActivityResult.getBody();

		//Find activities for logbook entry
		tempActivityResults =
				this.getLogbookController().findAllActivitiesForEntry(tempLogbookDTO.getId(),
																	  tempEntryDTO.getId());
		assertTrue(this.isFindActivitiesByLogbookEntryResultsValid(tempActivity, tempActivityResults),
				  "Find activity for logbook entry failed");



		//Delete created entry
		this.getLogbookController().deleteLogbookEntry(tempEntryDTO.getId());

		//Try to find them. We should not find any
		tempLogbookEntryResults = this.getLogbookController()
									  .findLogbookEntries(tempLogbookDTO.getId(),
											 0,
												10);
		assertTrue(this.isFindByIdLogbookEntriesResultEmpty(tempLogbookEntryResults),
				"Logbook entry find paged not empty");

	}

	/**
	 * Answer whether the find activities by logbook entry result valid
	 * @param anActivity ActivityDTO
	 * @param anActivityResults ResponseEntity
	 * @return boolean
	 */
	private boolean isFindActivitiesByLogbookEntryResultsValid(ActivityDTO anActivity,
															   ResponseEntity<List<ActivityDTO>> anActivityResults) {
		return anActivityResults != null &&
				anActivityResults.getStatusCode().equals(HttpStatus.OK) &&
					!anActivityResults.getBody().isEmpty() &&
						anActivityResults.getBody().contains(anActivity);

	}

	/**
	 * Answer whether the create activity result is valid
	 * @param anActivityDTO ResponseEntity
	 * @return boolean
	 */
	private boolean isCreateActivityResultValid(ResponseEntity<ActivityDTO> anActivityDTO) {

		return anActivityDTO.getStatusCode().equals(HttpStatus.OK) &&
					anActivityDTO.getBody().getId() != null;
	}


	/**
	 * Create activity dto
	 * @return ActivityDTO
	 */
	private ActivityDTO createActivityDTO() {

		ActivityDTO tempActivity;

		tempActivity = new ActivityDTO();

		tempActivity.setActivityType(ActivityType.ROWING);
		tempActivity.setActivityDetails("Steady state - Zone 2 - 232 pace");
		tempActivity.setDuration(30.0d);
		tempActivity.setDurationUnits(DurationUnits.MINUTES);

		tempActivity.addComment(new CommentDTO(null,
				"New Activity Comment 1",
						0,
						null));
		tempActivity.addComment(new CommentDTO(null,
				"New  Activity Comment 2",
						0,
						null));
		tempActivity.addComment(new CommentDTO(null,
				"New  Activity Comment 3",
					0,
					null));
		tempActivity.addComment(new  CommentDTO(null,
				"New  Activity Comment 4",
					0,
					null));


		return tempActivity;

	}


	/**
	 * Create, modify, and delete logbook entry
	 */
	@Test
	public void createModifyAndDeleteLogbookEntry() {

		LogbookDTO 												tempLogbookDTO;
		LogbookEntryDTO 									    tempEntryDTO;
		ResponseEntity<List<LogbookEntryDTO>>					tempResults;
		ResponseEntity<LogbookEntryDTO> 					    tempModifiedEntry;


		//Create logbook and logbook entries
		tempLogbookDTO = this.basicCreateLogbook();
		tempEntryDTO = this.basicCreateLogbookEntry(tempLogbookDTO);

		//Try to find them. We should find at least one
		tempResults = this.getLogbookController()
						  .findLogbookEntries(tempLogbookDTO.getId(),
									0, 10);
		assertTrue(this.isFindByIdLogbookEntriesResultValid(tempResults),
				"Logbook entry find paged failed");

		//Modify entry comments
		tempEntryDTO.getComments().get(0).setCommentContents("Modified version of whatever we retrieved");
		tempModifiedEntry = this.getLogbookController()
								.modifyLogbookEntry(tempLogbookDTO.getId(), tempEntryDTO);
		assertTrue(this.isModifiedLogbookEntryCommentFound(tempModifiedEntry),
				"Logbook entry modified comment not found");


		//Delete created entry
		this.getLogbookController().deleteLogbookEntry(tempEntryDTO.getId());

		//Try to find them. We should not find any
		tempResults = this.getLogbookController()
					      .findLogbookEntries(tempLogbookDTO.getId(),
								0,
								10);
		assertTrue(this.isFindByIdLogbookEntriesResultEmpty(tempResults),
				"Logbook entry find paged not empty");

	}

	/**
	 * Determine if comment was modified
	 * @param aModifiedEntry
	 * @return boolean
	 */
	private boolean isModifiedLogbookEntryCommentFound(ResponseEntity<LogbookEntryDTO> aModifiedEntry) {

		return !aModifiedEntry.getBody()
											.getComments()
											.stream()
											.filter(c->c.getCommentContents().equals("Modified version of whatever we retrieved"))
											.collect(Collectors.toList())
											.isEmpty();
	}


}
