package com.mjdsoftware.logbook.domain.changemgmt;

import com.mjdsoftware.logbook.domain.entities.DomainObjectCollectionEntry;
import com.mjdsoftware.logbook.dto.DTOCollectionEntry;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
public class DomainObjectChangeManagement<T extends DTOCollectionEntry, P extends DomainObjectCollectionEntry> {


    /**
     * Answer the domain objects that should be removed
     * @param aTotalDomainObjects List
     * @param aDomainObjectsToBeModified List
     * @return List
     */
    public  List<P> getDomainObjectsToBeRemoved(List<P> aTotalDomainObjects,
                                                List<P> aDomainObjectsToBeModified) {

        List<P> tempDomainObjectsToBeRemoved;

        tempDomainObjectsToBeRemoved = new ArrayList<P>(aTotalDomainObjects);
        tempDomainObjectsToBeRemoved.removeAll(aDomainObjectsToBeModified);

        return tempDomainObjectsToBeRemoved;

    }


    /**
     * Answer the domain objects present in aDTOs
     * @param aDTOs List
     * @param aDomainObjects List
     */
    public List<P> getDomainObjectsPresentInDTOs(List<T> aDTOs,
                                                 List<P> aDomainObjects) {

        List<Long> tempIds = aDTOs.stream()
                                  .map(dto->dto.getId())
                                  .collect(Collectors.toList());

        return
                aDomainObjects
                          .stream()
                          .filter(domainObject -> domainObject.hasSameIdsAs(tempIds))
                          .collect(Collectors.toList());

    }

    /**
     * Answer the DTOs for which there are not corresponding objects in aDomainObjects
     * @apram aDTOs List
     * @param aDomainObjects List
     * @return List
     */
    public List<T> getDTOsNotPresentInDomainObjects(List<T> aDTOs,
                                                    List<P> aDomainObjects) {

        List<Long> tempDomainObjectIds
                = aDomainObjects
                      .stream()
                      .map(domainObject->domainObject.getId())
                      .collect(Collectors.toList());

        return
                aDTOs
                    .stream()
                    .filter(dto -> !dto.hasSameIdsAs(tempDomainObjectIds))
                    .collect(Collectors.toList());

    }




}
