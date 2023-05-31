package com.mjdsoftware.logbook;

import com.mjdsoftware.logbook.api.LogbookController;
import com.mjdsoftware.logbook.api.UserController;
import com.mjdsoftware.logbook.domain.entities.ActivityType;
import com.mjdsoftware.logbook.domain.entities.DurationUnits;
import com.mjdsoftware.logbook.dto.*;
import com.mjdsoftware.logbook.dto.oauth.OauthToken;
import com.mjdsoftware.logbook.dto.oauth.UserAuthDTO;
import com.mjdsoftware.logbook.service.OauthAccessService;
import com.mjdsoftware.logbook.service.OauthAccessServiceImpl;
import com.mjdsoftware.logbook.utils.KeyCloakUtilities;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;


@SpringBootTest(classes = LogbookApplication.class)
@DirtiesContext
@WithMockUser(username="athelete", password = "athelete")
@ActiveProfiles("test")
public class LogbookApplicationTests {

	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE)
	@Autowired
	private UserController userController;

	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE)
	@Autowired
	private LogbookController logbookController;

	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE)
	@Autowired
	private HttpServletRequest servletRequest;

	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE)
	@Autowired
	private OauthAccessServiceImpl oauthService;

	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE)
	@Mock
	private KeyCloakUtilities keyCloakUtilities;

	//Constants
	private static final String TEST_USER_NAME = "someuser";
	private static final String TEST_USER_PASSWORD = "somepassword";

	/**
	 * Mock keycloak utilities for now so we don't have to deal with it
	 */
	@BeforeEach
	private void setup() {

		UserAuthDTO tempOauthUser = this.createOauthUser();
		UserAuthDTO[] tempUsers = {tempOauthUser};

		when(this.getKeyCloakUtilities().retrieveUser(null,
													  TEST_USER_NAME))
				 .thenReturn(Arrays.asList(tempUsers));

		ReflectionTestUtils.setField(this.getOauthService(),
								"keycloakUtilities",
									 this.getKeyCloakUtilities());


	}

	/**
	 * Create oauth user
	 * @return UserAuthDTO
	 */
	@NotNull
	private UserAuthDTO createOauthUser() {

		UserAuthDTO tempOauthUser;

		tempOauthUser = new UserAuthDTO();
		tempOauthUser.setId("someGibberish");
		tempOauthUser.setUsername(TEST_USER_NAME);
		tempOauthUser.setEnabled(true);

		return tempOauthUser;
	}

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
	@WithMockUser(authorities = "ROLE_app_admin")
	public void findAllLogbooks() {

		ResponseEntity<List<LogbookDTO>> tempLogbooks;


		tempLogbooks = this.getLogbookController().findAllLogbooks(this.getAuthentication(),
																   this.getServletRequest(),
																	this.createJwtToken());

		assertTrue(this.isFindAllLogbooksResultValid(tempLogbooks),
			"Find All Logbooks Failed");

	}



	/**
	 * Create logbook test
	 */
	@Test
	@WithMockUser(authorities = "ROLE_app_admin")
	public void createAndDeleteLogbook() {

		UserDTO tempUser = this.basicCreateUser();
		LogbookDTO tempLogbook = this.basicCreateLogbook(tempUser);
		ResponseEntity<LogbookDTO> tempResult;

		tempResult = this.getLogbookController().findLogbookByName(this.getAuthentication(),
																   this.getServletRequest(),
																	this.createJwtToken(),
																   tempLogbook.getName());

		assertTrue(this.isFindByNameLogbookResultValid(tempResult, tempLogbook.getName()),
				"Logbook find by name failed");

		this.getUserController().deleteUserById(tempUser.getId());

	}

	/**
	 * Basic create user
	 *
	 */
	private UserDTO basicCreateUser() {

		ResponseEntity<UserDTO> tempResult;
		UserDTO					tempUser;

		tempResult =
				this.getUserController().createUser(TEST_USER_NAME, TEST_USER_PASSWORD);
		assertTrue(this.isCreateUserResultValid(tempResult),
				"User creation failed");
		tempUser = tempResult.getBody();

		return tempUser;
	}

	/**
	 * Answer whether the create user result is valid
	 * @param aUser ResponseEntity
	 * @return boolean
	 */
	private boolean isCreateUserResultValid(ResponseEntity<UserDTO> aUser) {

		return aUser.getStatusCode().equals(HttpStatus.OK) &&
					aUser.getBody().getId() != null;
	}


	/**
	 * Basic create logbook. And return it
	 * @return LogbookDTO
	 */
	private LogbookDTO basicCreateLogbook(UserDTO aUser) {

		LogbookDTO 					tempLogbook;
		ResponseEntity<LogbookDTO>	tempResult;

		tempLogbook = new LogbookDTO();
		tempLogbook.setName("NewLogbook" + (new Date()).getTime());

		tempResult = this.getLogbookController().createLogbook(this.getAuthentication(),
															   this.getServletRequest(),
																this.createJwtToken(),
																aUser.getId(),
															    tempLogbook);
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
	@WithMockUser(authorities = "ROLE_app_admin")
	public void createAndDeleteLogbookEntry() {

		LogbookDTO 												tempLogbookDTO;
		LogbookEntryDTO 									    tempEntryDTO;
		ResponseEntity<List<LogbookEntryDTO>>					tempResults;
		UserDTO 												tempUser;

		//Create logbook and logbook entries
		tempUser = this.basicCreateUser();
		tempLogbookDTO = this.basicCreateLogbook(tempUser);
		tempEntryDTO = this.basicCreateLogbookEntry(tempLogbookDTO);

		//Try to find them. We should find at least one
		tempResults = this.getLogbookController()
						  .findLogbookEntries(this.getAuthentication(),
											  this.getServletRequest(),
								  			  this.createJwtToken(),
											  tempLogbookDTO.getId(),
								  			  0,
								                 10);
		assertTrue(this.isFindByIdLogbookEntriesResultValid(tempResults),
				"Logbook entry find paged failed");

		//Delete created entry
		this.getLogbookController().deleteLogbookEntry(this.getAuthentication(),
													  this.getServletRequest(),
													  this.createJwtToken(),
													  tempEntryDTO.getId());

		//Try to find them. We should not find any
		tempResults = this.getLogbookController()
						  .findLogbookEntries(this.getAuthentication(),
											  this.getServletRequest(),
								  			  this.createJwtToken(),
											  tempLogbookDTO.getId(),
											  0,
												 10);
		assertTrue(this.isFindByIdLogbookEntriesResultEmpty(tempResults),
				"Logbook entry find paged not empty");

		//Delete user
		this.getUserController().deleteUserById(tempUser.getId());

	}

	/**
	 * Create, finddall, and delete logbook entry
	 */
	@Test
	@WithMockUser(authorities = "ROLE_app_admin")
	public void createFindAllAndDeleteLogbookEntry() {

		LogbookDTO 												tempLogbookDTO;
		LogbookEntryDTO 									    tempEntryDTO;
		ResponseEntity<List<LogbookEntryDTO>>					tempResults;
		Long													tempDeletedId;
		UserDTO													tempUser;

		//Create logbook and logbook entries
		tempUser = this.basicCreateUser();
		tempLogbookDTO = this.basicCreateLogbook(tempUser);
		tempEntryDTO = this.basicCreateLogbookEntry(tempLogbookDTO);

		//Try to find them. We should find at least one
		tempResults = this.getLogbookController()
						  .findLogbookEntries(this.getAuthentication(),
								  			  this.getServletRequest(),
								  			  this.createJwtToken(),
								  			  tempLogbookDTO.getId(),
									0,
								  		10);
		assertTrue(this.isFindByIdLogbookEntriesResultValid(tempResults),
				"Logbook entry find paged failed");

		//Delete created entry
		tempDeletedId = tempEntryDTO.getId();
		this.getLogbookController().deleteLogbookEntry(this.getAuthentication(),
														this.getServletRequest(),
														this.createJwtToken(),
														tempEntryDTO.getId());

		//Try to find them. We should not find any
		tempResults = this.getLogbookController()
						  .findAllLogbookEntries(this.getAuthentication(),
								                 this.getServletRequest(),
								  				 this.createJwtToken(),
								  				 tempLogbookDTO.getId(),
													 0,
													   10);
		assertTrue(this.isFindByIdLogbookEntriesResultEmpty(tempResults),
				"Logbook entry find paged not empty");

		//Delete user
		this.getUserController().deleteUserById(tempUser.getId());

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
					.createLogbookEntry(this.getAuthentication(),
										this.getServletRequest(),
									    this.createJwtToken(),
										aLogbookDTO.getId(),
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
	@WithMockUser(authorities = "ROLE_app_admin")
	public void createAndDeleteActivity() {

		LogbookDTO 												tempLogbookDTO;
		LogbookEntryDTO 									    tempEntryDTO;
		ResponseEntity<List<LogbookEntryDTO>>					tempLogbookEntryResults;
		ResponseEntity<ActivityDTO>								tempActivityResult;
		ActivityDTO												tempActivity;
		ResponseEntity<List<ActivityDTO>>						tempActivityResults;
		UserDTO													tempUser;

		//Create logbook and logbook entries
		tempUser = this.basicCreateUser();
		tempLogbookDTO = this.basicCreateLogbook(tempUser);
		tempEntryDTO = this.basicCreateLogbookEntry(tempLogbookDTO);

		//Create activity
		tempActivityResult = this.getLogbookController().createLActivity(this.getAuthentication(),
																		this.getServletRequest(),
																		this.createJwtToken(),
																		tempLogbookDTO.getId(),
																		 tempEntryDTO.getId(),
																		 this.createActivityDTO());

		assertTrue(this.isCreateActivityResultValid(tempActivityResult),
				"Create activity failed");
		tempActivity = tempActivityResult.getBody();

		//Find activities for logbook entry
		tempActivityResults =
				this.getLogbookController().findAllActivitiesForEntry(this.getAuthentication(),
																	 this.getServletRequest(),
																     this.createJwtToken(),
																	 tempLogbookDTO.getId(),
																	  tempEntryDTO.getId());
		assertTrue(this.isFindActivitiesByLogbookEntryResultsValid(tempActivity, tempActivityResults),
				  "Find activity for logbook entry failed");



		//Delete created entry
		this.getLogbookController().deleteLogbookEntry(this.getAuthentication(),
													  this.getServletRequest(),
													  this.createJwtToken(),
													  tempEntryDTO.getId());

		//Try to find them. We should not find any
		tempLogbookEntryResults = this.getLogbookController()
									  .findLogbookEntries(this.getAuthentication(),
														  this.getServletRequest(),
											  			  this.createJwtToken(),
														  tempLogbookDTO.getId(),
														 0,
															10);
		assertTrue(this.isFindByIdLogbookEntriesResultEmpty(tempLogbookEntryResults),
				"Logbook entry find paged not empty");

		//Delete user
		this.getUserController().deleteUserById(tempUser.getId());

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
	@WithMockUser(authorities = "ROLE_app_admin")
	public void createModifyAndDeleteLogbookEntry() {

		LogbookDTO 												tempLogbookDTO;
		LogbookEntryDTO 									    tempEntryDTO;
		ResponseEntity<List<LogbookEntryDTO>>					tempResults;
		ResponseEntity<LogbookEntryDTO> 					    tempModifiedEntry;
		UserDTO													tempUser;

		//Create logbook and logbook entries
		tempUser = this.basicCreateUser();
		tempLogbookDTO = this.basicCreateLogbook(tempUser);
		tempEntryDTO = this.basicCreateLogbookEntry(tempLogbookDTO);

		//Try to find them. We should find at least one
		tempResults = this.getLogbookController()
						  .findLogbookEntries(this.getAuthentication(),
											  this.getServletRequest(),
								  			  this.createJwtToken(),
											  tempLogbookDTO.getId(),
												0, 10);
		assertTrue(this.isFindByIdLogbookEntriesResultValid(tempResults),
				"Logbook entry find paged failed");

		//Modify entry comments
		tempEntryDTO.getComments().get(0).setCommentContents("Modified version of whatever we retrieved");
		tempModifiedEntry = this.getLogbookController()
								.modifyLogbookEntry(this.getAuthentication(),
													this.getServletRequest(),
													this.createJwtToken(),
													tempLogbookDTO.getId(), tempEntryDTO);
		assertTrue(this.isModifiedLogbookEntryCommentFound(tempModifiedEntry),
				"Logbook entry modified comment not found");


		//Delete created entry
		this.getLogbookController().deleteLogbookEntry(this.getAuthentication(),
													   this.getServletRequest(),
													   this.createJwtToken(),
													   tempEntryDTO.getId());

		//Try to find them. We should not find any
		tempResults = this.getLogbookController()
					      .findLogbookEntries(this.getAuthentication(),
											  this.getServletRequest(),
								  			  this.createJwtToken(),
											  tempLogbookDTO.getId(),
											0,
											10);
		assertTrue(this.isFindByIdLogbookEntriesResultEmpty(tempResults),
				"Logbook entry find paged not empty");

		//Delete logbook
		this.getLogbookController()
				.deleteLogbook(this.getAuthentication(),
							   this.getServletRequest(),
							   this.createJwtToken(),
							   tempLogbookDTO.getId());

		//Delete user
		this.getUserController().deleteUserById(tempUser.getId());

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

	/**
	 * Answer the authentication for my test
	 * @return Authentication
	 */
	private Authentication getAuthentication() {
		return SecurityContextHolder.getContext().getAuthentication();
	}


	/**
	 * Create jwt token
	 * @return Jwt
	 */
	private Jwt createJwtToken() {

		String[] tempRoles = {"app_admin"};

		Jwt jwt = Jwt.withTokenValue("token")
				.header("alg", "none")
				.claim("scope", "message:read")
				.claim("role", tempRoles)
				.claim("preferred_username", "athelete")
				.build();

		return jwt;
	}

}
