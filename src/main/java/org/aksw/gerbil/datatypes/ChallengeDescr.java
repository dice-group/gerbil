package org.aksw.gerbil.datatypes;

import java.sql.Timestamp;

public class ChallengeDescr {

    private Timestamp startDate;
    private Timestamp endDate;
    private String name;

    public ChallengeDescr(Timestamp startDate, Timestamp endDate, String name) {

        this.setStartDate(startDate);
        this.setEndDate(endDate);
        this.setName(name);
    }

    public Timestamp getStartDate() {
        return startDate;
    }

    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

    public Timestamp getEndDate() {
        return endDate;
    }

    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        return (name+startDate+endDate).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof ChallengeDescr) {
            return this.hashCode()==obj.hashCode();
        }
        return false;
    }

}
