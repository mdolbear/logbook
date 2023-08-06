package com.mjdsoftware.logbook.csv;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@Slf4j
public abstract class AbstractCSVFileExporter<D> {

    @Getter(AccessLevel.PROTECTED) @Setter(AccessLevel.PROTECTED)
    private File exportFile;

    @Getter(AccessLevel.PROTECTED) @Setter(AccessLevel.PRIVATE)
    private CsvMapper csvMapper;

    /**
     * Answer my logger
     *
     * @return org.slf4j.Logger
     */
    private static Logger getLogger() {
        return log;
    }

    /**
     * Write data asynchronously
     * @param aData D[]
     */
    public void writeCsvFileAsynchronously(D[] aData) {

        CompletableFuture.runAsync(() -> {

            //TODO: figure out best way to handle this
            try {
                this.writeCsvFile(aData);
            }
            catch (Exception e) {

                getLogger().info("Failed to export csv file successfully", e);
            }

        });

    }


    /**
     * Write csv file
     * @param aData D[]
     */
    private void writeCsvFile(D[] aData) throws Exception {

        CsvSchema tempSchema;

        tempSchema = this.createCsvMapperAndSchema();

        //This controls the format of how the report columns look
        this.getCsvMapper().addMixIn(this.getDClass(),
                                     this.getCsvLineFormatClass());

        //Write data to file
        this.writeDataToFile(aData, tempSchema);


    }

    /**
     * Write aDate to file given aSchema
     * @param aData D[]
     * @param aSchema CsvSchema
     * @throws IOException
     */
    protected void writeDataToFile(D[] aData, CsvSchema aSchema) throws IOException {

        this.getCsvMapper().writerFor(this.getDArrayClass())
                           .with(aSchema)
                           .writeValue(this.getExportFile(), aData);
    }

    /**
     * Create Csv mapper and schema
     * @return CsvSchema
     */
    private CsvSchema createCsvMapperAndSchema() {


        this.setCsvMapper(new CsvMapper());
        return
                this.getCsvMapper().schemaFor(this.getCsvLineFormatClass())
                                   .withHeader();

    }

    /**
     * Answer my D array class. This is the array class that will be sent to be
     * written in csv form. It's the "data" class in array form
     * @return Class
     */
    protected abstract Class<?> getDArrayClass();

    /**
     * Answer my D  class. This is the class that will be sent to be written in csv form.
     * Its the "data" class
     * @return Class
     */
    protected abstract Class<?> getDClass();

    /**
     * Answer my CSV Line class. This class controls the mapping from json to CSV.
     * @return Class
     */
    protected abstract Class<?> getCsvLineFormatClass();


}
