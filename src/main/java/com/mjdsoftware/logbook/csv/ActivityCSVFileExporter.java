package com.mjdsoftware.logbook.csv;

import java.io.File;

public class ActivityCSVFileExporter extends AbstractCSVFileExporter<ActivityWrapper> {

    /**
     * Answer an instance on aFile
     * @param aFile File
     */
    public ActivityCSVFileExporter(File aFile) {

        super();
        this.setExportFile(aFile);

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

}
