package org.voimala.votingapp.datasource.voting;

public class VotingOption {
    
    private String text = "";
    private int count = 0;
    
    public VotingOption(final String text) {
        this.text = text;
    }
    
    public final String getText() {
        return text;
    }

    public final int getCount() {
        return count;
    }

    public final void setCount(final int count) {
        this.count = count;
    }
    
}
