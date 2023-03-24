package com.mjdsoftware.logbook.domain.changemgmt;

import com.mjdsoftware.logbook.domain.entities.DomainObjectCollectionEntry;
import com.mjdsoftware.logbook.dto.DTOCollectionEntry;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DomainObjectChanges<T extends DTOCollectionEntry, P extends DomainObjectCollectionEntry> {

    private List<T> dtos;
    private List<P> domainObjects;

    private List<P> existingDomainObjects;
    private List<T> newDTOs;
    private List<P> domainObjectsToBeRemoved;


    /**
     * Answer an instance of me on
     * @param aDTOs List
     * @param aDomainObjects List
     */
    public DomainObjectChanges(List<T> aDTOs,
                               List<P> aDomainObjects) {

        this.setDtos(aDTOs);
        this.setDomainObjects(aDomainObjects);
        this.setExistingDomainObjects(new ArrayList<P>());
        this.setNewDTOs(new ArrayList<T>());
        this.setDomainObjectsToBeRemoved(new ArrayList<P>());

    }

    /**
     * Produce changes
     */
    public void performChangeAnalysis() {

        List<P>                      tempExistingComments;
        List<T>                      tempNewDTOs;
        List<P>                      tempCommentsToBeRemoved;
        DomainObjectChangeManagement tempChangeManagement = new DomainObjectChangeManagement();

        //Get collections required for domain object changes
        tempExistingComments =
                tempChangeManagement.getDomainObjectsPresentInDTOs(this.getDtos(),
                                                             this.getDomainObjects());
        tempNewDTOs = tempChangeManagement.getDTOsNotPresentInDomainObjects(this.getDtos(),
                                                                              this.getDomainObjects());
        tempCommentsToBeRemoved =
                tempChangeManagement.getDomainObjectsToBeRemoved(this.getDomainObjects(),
                                                            tempExistingComments);

        this.setExistingDomainObjects(tempExistingComments);
        this.setNewDTOs(tempNewDTOs);
        this.setDomainObjectsToBeRemoved(tempCommentsToBeRemoved);

    }

}
