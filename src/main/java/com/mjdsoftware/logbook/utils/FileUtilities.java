package com.mjdsoftware.logbook.utils;


import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.FileSystems;

@Component
public class FileUtilities {

    //Constants
    public static final String FILE_PATH_SEPARATOR = FileSystems.getDefault().getSeparator();

    /**
     * Answer a default instance
     */
    public FileUtilities() {
        super();
    }

    /**
     * Move a file from aSourcePath to aDestPath
     * @param aSourcePath String
     * @param aDestPath String
     */
    public void copyFileToDirectory(String aSourcePath,
                                    String aDestPath)
            throws IOException {

        File tempSrc;
        File    tempDest;

        //Create source file and validate existence
        tempSrc = new File(aSourcePath);
        this.validateFileExists(tempSrc);

        //Create file for directory
        tempDest = new File(aDestPath);


        FileUtils.copyToDirectory(tempSrc, tempDest);

    }

    /**
     * Create a new empty file from aFile
     * @param aFile File
     * @return File
     */
    public void createNewEmptyFile(File aFile) throws IOException {


        if (aFile.exists()) {

            aFile.delete();
            aFile.createNewFile();
        }
        else {
            aFile.createNewFile();

        }

    }

    /**
     * Validate file exists
     * @apram aFile File
     */
    public void validateFileExists(File aFile) {

        if (aFile == null ||
                !aFile.exists()) {

            throw new IllegalStateException("File does not exist");
        }
    }



    /**
     * Force directory creation if it does not exist
     * @param aFile File
     */
    public void forceDirectoryCreationIfNotExists(File aFile) {

        if (aFile != null &&
                !aFile.exists()) {

            aFile.mkdirs();
            aFile.setReadable(true, false);
            aFile.setWritable(true, false);
            aFile.setExecutable(true, false);

        }

    }

    /**
     * Answer an output stream on aFilepaht
     * @param aFilepath String
     * @return OutputStream
     * @throws FileNotFoundException
     */
    public OutputStream createBufferedOutputStreamOnFilePath(String aFilepath) throws
            FileNotFoundException {

        return new BufferedOutputStream(new FileOutputStream(aFilepath));

    }


    /**
     * Validate file exists
     * @apram aFile File
     */
    public void validateDirectoryExists(File aFile) {

        if (!this.doesDirectoryExist(aFile)) {

            throw new IllegalStateException("Directory does not exist");
        }
    }

    /**
     * Does directory exist
     * @param aFile File
     * @return boolean
     */
    public boolean doesDirectoryExist(File aFile) {

        return aFile != null &&
                aFile.exists() &&
                aFile.isDirectory();
    }

    /**
     * Answer the original filename without path information from aMultipartFile
     * @param aMultipartFile MultipartFile
     * @return String
     */
    public String getOriginalFilenameFromMultipartFile(MultipartFile aMultipartFile) {

        String  tempResult;


        tempResult = aMultipartFile.getName();
        if (aMultipartFile.getOriginalFilename() != null) {

            tempResult = aMultipartFile.getOriginalFilename();
        }

        return tempResult;

    }

    /**
     * Delete a file at anAbsoluteFilePath. Answer true if file was deleted.
     * @param anAbsoluteFilePath String
     * @return boolean
     */
    public boolean deleteFileAtPath(String anAbsoluteFilePath) {

        File    tempFile;
        boolean tempResult = false;

        tempFile = new File(anAbsoluteFilePath);

        if (tempFile.exists()) {

            tempResult = tempFile.delete();
        }

        return tempResult;

    }

    /**
     * Silently close aStream if its not null
     * @param aStream Closeable
     */
    public void silentlyClose(Closeable aStream) {

        try {
            if (aStream != null) {

                aStream.close();
            }
        }
        catch (IOException e) {
            //Do nothing
        }

    }

    /**
     * Silently close aFileWriter
     * @param aWriter FileWriter
     */
    public void silentlyCloseFileWriter(FileWriter aWriter) {

        if (aWriter != null) {

            try {
                aWriter.close();
            }
            catch (IOException e) {

                //Do nothing
            }

        }
    }

    /**
     * Silently close aPrinter
     * @param aPrinter CSVPrinter
     */
    public void silentlyClosePrinter(CSVPrinter aPrinter) {

        if (aPrinter != null) {

            try {
                aPrinter.close();
            }
            catch (IOException e) {

                //Do nothing
            }

        }
    }

}