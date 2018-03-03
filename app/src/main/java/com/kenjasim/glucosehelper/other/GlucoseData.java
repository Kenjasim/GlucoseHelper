package com.kenjasim.glucosehelper.other;


/**
 * This class stores the data which is input by the user for their
 */
public class GlucoseData {

    //Initialising the variables
    String bloodLevels;
    String carbohydrates;
    String insulin;
    String dateTime;
    String dataID;
    String timestamp;
    String notes;

    /**
     * Constructor of the Glucose data class, stores the data in the variable
     */
    public GlucoseData(String bloodLevels, String carbohydrates, String insulin, String dateTime, String dataID, String timestamp, String notes) {
        this.bloodLevels = bloodLevels;
        this.carbohydrates = carbohydrates;
        this.insulin = insulin;
        this.dateTime = dateTime;
        this.dataID = dataID;
        this.timestamp = timestamp;
        this.notes = notes;
    }

    /**
     * @return The blood glucose levels
     */
    public String getBloodLevels() {
        return bloodLevels;
    }

    /**
     * @return The carbohydrate intake
     */
    public String getCarbohydrates() {
        return carbohydrates;
    }

    /**
     * @return The insulin given
     */
    public String getInsulin() {
        return insulin;
    }

    /**
     * @return The date and time
     */
    public String getDateTime() {
        return dateTime;
    }

    /**
     * @return The data id
     */
    public String getDataID() {
        return dataID;
    }

    /**
     * @return The timestamp
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * @return The notes attached to the input
     */
    public String getNotes() {
        return notes;
    }

}
