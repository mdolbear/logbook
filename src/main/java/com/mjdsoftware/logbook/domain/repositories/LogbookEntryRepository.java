package com.mjdsoftware.logbook.domain.repositories;

import com.mjdsoftware.logbook.domain.entities.Logbook;
import com.mjdsoftware.logbook.domain.entities.LogbookEntry;
import com.mjdsoftware.logbook.dto.LogbookEntryDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LogbookEntryRepository extends JpaRepository<LogbookEntry, Long> {

    /**
     * Find the logbook entries for aLogbook. There could be many, so best to page through them.
     * @param aLogbook Logbook
     * @param pageable pageable
     * @return List
     */
    public List<LogbookEntry> findByLogbook(Logbook aLogbook,
                                            Pageable pageable);

    /**
     * Find all lite logbook entries for aLogbook
     * @param id Long
     *
     * @return List
     */
    @Query("select new com.mjdsoftware.logbook.dto.LogbookEntryDTO(ent.id, ent.activityDate, " +
            "ent.version) " +
            " from LogbookEntry ent where ent.logbook.id = :id order by ent.activityDate desc")
    public List<LogbookEntryDTO> findAllByLogbook(@Param("id") Long id);

    /**
     * Find all lite logbook entries for aLogbook
     * @param id Long
     * @return List
     */
    @Query("select new com.mjdsoftware.logbook.dto.LogbookEntryDTO(ent.id, ent.activityDate, " +
            "ent.version) " +
            " from LogbookEntry  ent  where ent.logbook.id = :id order by ent.activityDate desc")
    public List<LogbookEntryDTO> findAllByLogbook(@Param("id") Long id, Pageable pageable);

    /**
     * Answer the count of logbook entries for aLogbook
     * @param aLogbookId Long
     * @return Long
     */
    @Query(value="select count(*) FROM logbook_entries WHERE logbook_id = ?1", nativeQuery=true)
    public Long getCountByLogbookId(Long aLogbookId);

}
