package com.mjdsoftware.logbook.csv;

import com.mjdsoftware.logbook.domain.entities.Activity;
import com.mjdsoftware.logbook.domain.entities.ActivityType;
import com.mjdsoftware.logbook.domain.entities.DistanceUnits;
import com.mjdsoftware.logbook.domain.entities.DurationUnits;
import com.mjdsoftware.logbook.domain.entities.MonitoredAerobicActivity;
import com.mjdsoftware.logbook.dto.ActivityExportRequest;
import com.mjdsoftware.logbook.dto.MonitoredAerobicActivityDTO;
import com.mjdsoftware.logbook.service.ActivityService;
import com.mjdsoftware.logbook.utils.FileUtilities;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

public class ChunkableActivityCSVFileExporterTests {

    /**
     * Perform main test
     */
    @Test
    public void testWriteCsvFileAsynchronously() {

        File tempFile;

        ActivityExportRequest tempRequest =
                new ActivityExportRequest((new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7))).getTime(),
                        (new Date()).getTime(),
                        ActivityType.ROWING,
                        "test.csv");

        ChunkableActivityCSVFileExporter tempObjectToTest;

        tempFile = this.createTemporaryFile();
        tempObjectToTest = new ChunkableActivityCSVFileExporter(tempFile,
                1L,
                tempRequest,
                1,
                this.createMockService(),
                new FileUtilities());


        tempObjectToTest.writeCsvFileAsynchronously(this.createTestData());
        this.performWait();

        Assertions.assertTrue(tempFile.exists() && tempFile.length() > 0);


    }

    /**
     * Create test data
     * @return ActivityWrapper[]
     */
    private ActivityWrapper[] createTestData() {

        ActivityWrapper[] tempWrappers = new ActivityWrapper[1];

        tempWrappers[0] = new ActivityWrapper();
        ReflectionTestUtils.setField(tempWrappers[0], "activity", this.createTestActivity());

        return tempWrappers;

    }

    /**
     * Create test activity
     * @return Activity
     */
    private Activity createTestActivity() {

        MonitoredAerobicActivity tempResult = new MonitoredAerobicActivity();

        MonitoredAerobicActivityDTO tempDTO = this.createActivityDTO();
        tempResult.updateFrom(tempDTO);

        return tempResult;

    }

    /**
     * Create test activity DTO to populate activity
     * @return MonitoredAerobicActivityDTO
     */
    private MonitoredAerobicActivityDTO createActivityDTO() {
        MonitoredAerobicActivityDTO tempDTO;

        tempDTO = new MonitoredAerobicActivityDTO();
        tempDTO.setId(1l);
        tempDTO.setActivityType(ActivityType.ROWING);
        tempDTO.setDuration(1.0);
        tempDTO.setDurationUnits(DurationUnits.HOURS);
        tempDTO.setDistance(116000.0);
        tempDTO.setDistanceUnits(DistanceUnits.METERS);
        tempDTO.setActivityDetails("This is a new activity");
        tempDTO.setVersion(1l);

        return tempDTO;
    }


    /**
     * Answer my mock service that will return no data when asked for activities
     * @return ActivityService
     */
    private ActivityService createMockService() {

        ActivityService tempResult = Mockito.mock(ActivityService.class);

        Mockito.when(tempResult.findAllActivitiesBetweenDatesAsWrappers(Mockito.anyLong(),
                        Mockito.any(ActivityExportRequest.class),
                        Mockito.anyInt()))
                .thenReturn(Arrays.asList(new ActivityWrapper[0]));

        return tempResult;

    }


    private File createTemporaryFile() {

        File tempResult = null;

        try {

            tempResult = File.createTempFile("test", ".csv", new File("/tmp"));

        }
        catch (IOException e) {

            e.printStackTrace();
            Assertions.fail("Unable to create temporary file");

        }

        return tempResult;
    }

    private void performWait() {

        try {
            Thread.sleep(2000);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


}
