package org.voimala.votingapp.datasource.voting;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

public class Voting {
    
    private String uuid = "";
    private Calendar startTime = null;
    private Calendar endTime = null;
    private ArrayList<VotingOption> options = new ArrayList<VotingOption>();
    private String title = "";
    private String description = "";
    private String creator = "";
    private final Logger logger = Logger.getLogger(getClass().getName());
    
    public Voting(final String id) {
        this.uuid = id;
    }
    
    public Voting(final String id, final String title, final List<VotingOption> options) {
        this.uuid = id;
        this.title = title;
        this.options = (ArrayList<VotingOption>) options;
    }

    public final String getTitle() {
        return title;
    }
    
    public final List<VotingOption> getOptions() {
        return options;
    }

    public final String getId() {
        return uuid;
    }

    public final void setId(final String id) {
        this.uuid = id;
    }

    public final String getDescription() {
        return description;
    }

    public final void setDescription(final String description) {
        this.description = description;
    }
    
    /**@throws RuntimeException if start time is after end time. */
    public final void setStartTime(final Calendar startTime) {
        this.startTime = startTime;
        checkStartAndEndTime();
    }

    public final Calendar getStartTime() {
        return startTime;
    }

    /** @return DD.MM.YYYY HH:MM:SS String. */
    public final String getStartTimeAsString() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        return simpleDateFormat.format(startTime.getTime());
    }

    public final Calendar getEndTime() {
        return endTime;
    }
    
    /** @return DD.MM.YYYY HH:MM:SS String. */
    public final String getEndTimeAsString() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        return simpleDateFormat.format(endTime.getTime());
    }
    
    /**@throws RuntimeException if start time is after end time. */
    public final void setEndTime(final Calendar endTime) {
        this.endTime = endTime;
        checkStartAndEndTime();
    }

    private void checkStartAndEndTime() {
        if (!isStartTimeBeforeEndTime()) {
            throw new RuntimeException("Start time" + " " + getStartTimeAsString() + " " + 
                    "must be before end time" + " " + endTime + ".");
        }
    }

    public final void setCreator(final String creator) {
        this.creator = creator;
    }

    public final String getCreator() {
        return creator;
    }
    
    public final boolean isOpen() {
        if (startTime == null) {
            return false;
        }
        
        Date dateNow = new Date();
        
        if (endTime == null && dateNow.after(startTime.getTime())) {
            return true;
        }
             
        return (dateNow.after(startTime.getTime()) && dateNow.before(endTime.getTime()));
    }
    
    public final boolean isClosed() {
        Date dateNow = new Date();
        
        if (endTime == null) {
            return false;
        }
        
        return (dateNow.after(endTime.getTime()));
    }
    
    public final boolean isUpcoming() {
        if (startTime == null) {
            return false;
        }
        
        Date dateNow = new Date();
        return (dateNow.before(startTime.getTime()));
    }
    
    /** @return Returns true if either start time or end time is null (or both). */
    private boolean isStartTimeBeforeEndTime() {
        if (startTime == null && endTime == null) {
            return true;
        }
        
        if (startTime == null || endTime == null) {
            return true;
        }
        
        return (startTime.getTime().before(endTime.getTime()));
    }

    public final int getNumberOfVotes() {
        int amount = 0;
        
        for (VotingOption option : options) {
            amount += option.getCount();
        }
        
        return amount;
    }

}
