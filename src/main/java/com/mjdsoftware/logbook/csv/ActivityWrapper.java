package com.mjdsoftware.logbook.csv;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mjdsoftware.logbook.domain.entities.Activity;
import com.mjdsoftware.logbook.domain.entities.MonitoredAerobicActivity;
import com.mjdsoftware.logbook.domain.entities.UnMonitoredAerobicActivity;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActivityWrapper {

    @Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE)
    private Activity activity;

    /**
     * Answer myself as object values
     * @return Object[]
     */
    public Object[] asObjectValues() {

        Object[] tempResult = new Object[10];

        tempResult[0] = this.getId();
        tempResult[1] = this.getActivityType();
        tempResult[2] = this.getDuration();
        tempResult[3] = this.getDurationUnits();
        tempResult[4] = this.getActivityDetails();
        tempResult[5] = this.getDistance();
        tempResult[6] = this.getDistanceUnits();
        tempResult[7] = this.getAverageWatts();
        tempResult[8] = this.getTotalCalories();
        tempResult[9] = this.getAverageHeartRate();


        return tempResult;

    }


    /**
     * Answer my id
     * @return Long
     */
    public Long getId() {
        return this.getActivity().getId();
    }

    /**
     * Answer my activity type
     * @return String
     */
    public String getActivityType() {
        return this.getActivity().getActivityType().name();
    }


    /**
     * Answer my duration
     * return double
     */
    public double getDuration() {
        return this.getActivity().getDuration();
    }

    /**
     * Answer my duration units
     * @return String
     */
    public String getDurationUnits() {
        return this.getActivity().getDurationUnits().name();
    }


    /**
     * Answer my activity details
     * @return String
     */
    public String getActivityDetails() {

        return this.getActivity().getActivityDetails().getDetails();
    }

    /**
     * Answer my distance
     * @return double
     */
    public double getDistance() {

        double                          tempResult = 0.0;
        MonitoredAerobicActivity        tempMon;
        UnMonitoredAerobicActivity      tempUnMon;

        if (this.getActivity() instanceof UnMonitoredAerobicActivity) {
            tempUnMon = (UnMonitoredAerobicActivity) this.getActivity();
            tempResult = tempUnMon.getDistance();
        }
        else if (this.getActivity() instanceof MonitoredAerobicActivity) {
            tempMon = (MonitoredAerobicActivity)this.getActivity();
            tempResult = tempMon.getDistance();
        }

        return tempResult;

    }

    /**
     * Answer my distance
     * @return double
     */
    public String getDistanceUnits() {

        String                       tempResult = null;
        MonitoredAerobicActivity     tempMon;
        UnMonitoredAerobicActivity   tempUnMon;

        if (this.getActivity() instanceof UnMonitoredAerobicActivity) {
            tempUnMon = (UnMonitoredAerobicActivity) this.getActivity();
            tempResult = tempUnMon.getDistanceUnits().name();
        }
        else if (this.getActivity() instanceof MonitoredAerobicActivity) {
            tempMon = (MonitoredAerobicActivity)this.getActivity();
            tempResult = tempMon.getDistanceUnits().name();
        }

        return tempResult;

    }


    /**
     * Answer my average watts
     * @return double
     */
    public double getAverageWatts() {

        double                       tempResult = 0.0;
        MonitoredAerobicActivity     tempMon;

        if (this.getActivity() instanceof MonitoredAerobicActivity) {
            tempMon = (MonitoredAerobicActivity)this.getActivity();
            tempResult = tempMon.getAverageWatts();
        }

        return tempResult;

    }


    /**
     * Answer my total calories
     * @return double
     */
    public double getTotalCalories() {

        double                       tempResult = 0.0;
        MonitoredAerobicActivity     tempMon;

        if (this.getActivity() instanceof MonitoredAerobicActivity) {
            tempMon = (MonitoredAerobicActivity)this.getActivity();
            tempResult = tempMon.getTotalCalories();
        }

        return tempResult;

    }


    /**
     * Answer my average heart rate
     * @return double
     */
    public double getAverageHeartRate() {

        double                       tempResult = 0.0;
        MonitoredAerobicActivity     tempMon;

        if (this.getActivity() instanceof MonitoredAerobicActivity) {
            tempMon = (MonitoredAerobicActivity)this.getActivity();
            tempResult = tempMon.getAverageHeartRate();
        }

        return tempResult;

    }

}
