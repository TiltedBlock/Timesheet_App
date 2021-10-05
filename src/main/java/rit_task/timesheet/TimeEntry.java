package rit_task.timesheet;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class TimeEntry {

    private String id; // Automatically assigned at creation

    @NotBlank
    private String date; // Format: dd.MM.YY

    @NotBlank
    @Pattern(regexp = "^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$")
    private String timeFrom; // Format: HH:mm

    @NotBlank
    @Pattern(regexp = "^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$")
    private String timeTo; // Format: HH:mm

    private String category; // Can be: Development, Support, Meeting, Other - Bad Entry defaults to Other

    private String description; // Max. 255 characters, automatically shortened


    public TimeEntry() {
    }

    public TimeEntry(TimeEntry timeEntryInput) {

        if (timeEntryInput.getId() == null) {
            this.id = "id_" + System.currentTimeMillis();
        } else {
            this.id = timeEntryInput.id;
        }

        this.date = timeEntryInput.getDate();
        this.timeFrom = timeEntryInput.getTimeFrom();
        this.timeTo = timeEntryInput.getTimeTo();
        this.setCategory(timeEntryInput.getCategory());
        this.setDescription(timeEntryInput.getDescription());

        if (Integer.parseInt(this.timeFrom.replace(":","")) > Integer.parseInt(this.timeTo.replace(":",""))) {
            String timeFromTemp = this.timeFrom;
            this.timeFrom = this.timeTo;
            this.timeTo = timeFromTemp;
        }

    }

    public TimeEntry(String date, String timeFrom, String timeTo, String category, String description) {
        this.date = date;
        this.timeFrom = timeFrom;
        this.timeTo = timeTo;
        this.category = category;
        this.description = description;
    }

    public void presetId(String id ) {
        // Overwrite automatic ID (for testing)
        this.id = id;
    }

    private void setCategory(String category) {

        // Makes sure the category is a valid option, defaults wrong input to "Other"

        category = category.toLowerCase();

        switch (category) {

            case "development":
                this.category = "Development";
                return;

            case "support":
                this.category = "Support";
                return;

            case "meeting":
                this.category = "Meeting";
                return;

            default:
                this.category = "Other";

        }

    }

    public void setDescription(String description) {

        // Make sure character limit is not exceeded

        if (description.length() > 255) {
            description = description.substring(0,255);
        }

        this.description = description;
    }

}
