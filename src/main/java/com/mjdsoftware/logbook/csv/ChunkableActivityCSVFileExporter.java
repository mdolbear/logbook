package com.mjdsoftware.logbook.csv;

import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.mjdsoftware.logbook.dto.ActivityExportRequest;
import com.mjdsoftware.logbook.service.ActivityService;
import com.mjdsoftware.logbook.utils.FileUtilities;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@Slf4j
public class ChunkableActivityCSVFileExporter extends AbstractCSVFileExporter<ActivityWrapper> {

    @Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE)
    private Long logbookId;

    @Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE)
    private ActivityExportRequest exportRequest;

    @Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE)
    private int numberOfRemainingPages;

    @Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE)
    private ActivityService activityService;

    @Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE)
    private FileUtilities fileUtilities;

    public ChunkableActivityCSVFileExporter(File anExportFile,
                                            Long logbookId,
                                            ActivityExportRequest exportRequest,
                                            int numberOfRemainingPages,
                                            ActivityService activityService,
                                            FileUtilities aFileUtilities) {

        this.setExportFile(anExportFile);
        this.setLogbookId(logbookId);
        this.setExportRequest(exportRequest);
        this.setNumberOfRemainingPages(numberOfRemainingPages);
        this.setActivityService(activityService);
        this.setFileUtilities(aFileUtilities);

    }

    /**
     * Answer my D array class. This is the array class that will be sent to be
     * written in csv form. It's the "data" class in array form
     *
     * @return Class
     */
    @Override
    protected Class<?> getDArrayClass() {
        return ActivityWrapper[].class;
    }

    /**
     * Answer my D  class. This is the class that will be sent to be written in csv form.
     * Its the "data" class
     *
     * @return Class
     */
    @Override
    protected Class<?> getDClass() {
        return ActivityWrapper.class;
    }

    /**
     * Answer my CSV Line class. This class controls the mapping from json to CSV.
     *
     * @return Class
     */
    @Override
    protected Class<?> getCsvLineFormatClass() {
        return ActivityCSVLineDefinition.class;
    }


    /**
     * Write aDate to file given aSchema
     * @param aData ActivityWrapper[]
     * @param aSchema CsvSchema
     * @throws IOException
     */
    @Override
    protected void writeDataToFile(ActivityWrapper[] aData, CsvSchema aSchema) throws IOException {

        int                 tempPageNumber= 0;
        OutputStream        tempStream = null;
        ActivityWrapper[]   tempArray;

        tempArray = aData;

        try {

            tempStream =
                    this.getFileUtilities()
                        .createBufferedOutputStreamOnFilePath(this.getExportFile().getAbsolutePath());

            do {

                //Write data to file
                this.getCsvMapper().writerFor(this.getDArrayClass())
                                   .with(aSchema)
                                   .writeValue(tempStream, tempArray);

                //Decrement remaining pages
                this.setNumberOfRemainingPages(this.getNumberOfRemainingPages() - 1);

                //Get next page of activities if any remaining
                tempPageNumber++;
                tempArray = this.findNextActivitiesIfRemainingPages(tempPageNumber);

            }
            while (this.getNumberOfRemainingPages() > 0);

        }
        finally {

            this.getFileUtilities().silentlyClose(tempStream);

        }


    }

    /**
     * Find next page of activities if there are any remaining
     * @param aPageNumber int
     * @return ActivityWrapper[]
     */
    private ActivityWrapper[] findNextActivitiesIfRemainingPages(int aPageNumber) {

        ActivityWrapper[]       tempArray = null;
        List<ActivityWrapper>   tempWrappers;

        if (this.getNumberOfRemainingPages() > 0) {

            tempWrappers =
                    this.getActivityService()
                            .findAllActivitiesBetweenDatesAsWrappers(this.getLogbookId(),
                                    this.getExportRequest(),
                                    aPageNumber);
            tempArray =
                    tempWrappers.toArray(new ActivityWrapper[tempWrappers.size()]);

        }

        return tempArray;

    }


}
