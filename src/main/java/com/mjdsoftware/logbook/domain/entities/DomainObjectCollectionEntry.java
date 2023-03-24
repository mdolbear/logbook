package com.mjdsoftware.logbook.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;
import java.util.stream.Collectors;

public interface DomainObjectCollectionEntry {

    /**
     * Answer my id
     * @return Long
     */
    public Long getId();

    /**
     * Answer the whether there is a dto has the same ids as in anIds
     *
     * @param anIds List
     */
    @JsonIgnore
    public default boolean hasSameIdsAs(List<Long> anIds) {

        return !anIds.stream()
                     .filter(id-> this.hasSameIdAs(id))
                     .collect(Collectors.toList())
                     .isEmpty();

    }

    /**
     * Answer whether I have the same id as anId
     * @param anId Long
     * @return boolean
     */
    @JsonIgnore
    public default boolean hasSameIdAs(Long anId) {

        return anId != null &&
                anId.intValue() != 0 &&
                anId.equals(this.getId());

    }

}
