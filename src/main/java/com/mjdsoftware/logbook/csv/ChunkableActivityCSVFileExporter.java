package com.mjdsoftware.logbook.csv;

import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.mjdsoftware.logbook.dto.ActivityExportRequest;
import com.mjdsoftware.logbook.service.ActivityService;
import com.mjdsoftware.logbook.utils.FileUtilities;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;

import java.io.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class ChunkableActivityCSVFileExporter  {

    @Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE)
    private File exportFile;

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

    //Constants
    private static final String[] COLUMNS = {"id", "activityType", "duration",
            "durationUnits", "activityDetails", "distance", "distanceUnits",
            "averageWatts", "totalCalories", "averageHeartRate"};

    /**
     * Answer my logger
     *
     * @return org.slf4j.Logger
     */
    private static Logger getLogger() {
        return log;
    }

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
     * Write data asynchronously
     * @param aData ActivityWrapper[]
     */
    public void writeCsvFileAsynchronously(ActivityWrapper[] aData) {

        CompletableFuture.runAsync(() -> {

            //TODO: figure out best way to handle this
            try {
                this.writeDataToFile(aData);
            }
            catch (Exception e) {

                getLogger().info("Failed to export csv file successfully", e);
            }

        });

    }



    /**
     * Write aDate to file given aSchema
     * @param aData ActivityWrapper[]
     * @throws IOException
     */
    private void writeDataToFile(ActivityWrapper[] aData) throws IOException {

        int                 tempPageNumber= 0;
        ActivityWrapper[]   tempArray;
        FileWriter          tempOutputWriter = null;
        CSVPrinter          tempPrinter = null;


        tempArray = aData;

        try {

            tempOutputWriter = new FileWriter(this.getExportFile());
            tempPrinter = this.createCSVPrinterWithColumnNames(tempOutputWriter);

            do {

                //Write data to file
                this.writeArrayToCSVPrinter(tempArray, tempPrinter);

                //Decrement remaining pages
                this.setNumberOfRemainingPages(this.getNumberOfRemainingPages() - 1);

                //Get next page of activities if any remaining
                tempPageNumber++;
                tempArray = this.findNextActivitiesIfRemainingPages(tempPageNumber);

            }
            while (this.getNumberOfRemainingPages() > 0);

        }
        finally {

            this.getFileUtilities().silentlyCloseFileWriter(tempOutputWriter);
            this.getFileUtilities().silentlyClosePrinter(tempPrinter);

        }


    }

    /**
     * Write array to csv printer
     * @param anArray ActivityWrapper[]
     * @param aPrinter CSVPrinter
     * @throws IOException
     */
    private void writeArrayToCSVPrinter(ActivityWrapper[] anArray,
                                        CSVPrinter aPrinter) throws IOException {

        for (ActivityWrapper w : anArray) {

            aPrinter.printRecord(w.asObjectValues());

        }

    }

    /**
     * Answer a new CSVPrinter
     * @param tempOutWriter FileWriter
     * @return CSVPrinter
     * @throws IOException
     */
    private CSVPrinter createCSVPrinterWithColumnNames(FileWriter tempOutWriter) throws IOException {

        return
                new CSVPrinter(tempOutWriter,
                               CSVFormat.DEFAULT
                                .withHeader(COLUMNS));

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
