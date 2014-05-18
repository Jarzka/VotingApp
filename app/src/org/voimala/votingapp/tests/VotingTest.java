package org.voimala.votingapp.tests;

import junit.framework.TestCase;

import org.junit.Test;
import org.voimala.util.Rfc3339;
import org.voimala.votingapp.datasource.voting.Voting;

public class VotingTest extends TestCase {
    
    @Test
    public void testIsOpen() {
        Voting voting = new Voting("123");
        voting.setStartTime(Rfc3339.parse("2000-02-05T14:15:00.000Z"));
        voting.setEndTime(Rfc3339.parse("3000-01-01T12:00:00.000Z"));
        assertTrue(voting.isOpen());
    }
    
    @Test
    public void testIsOpen2() {
        Voting voting = new Voting("123");
        voting.setStartTime(Rfc3339.parse("2000-02-05T14:15:00.000Z"));
        voting.setEndTime(Rfc3339.parse("5040-02-05T14:15:00.000Z"));
        assertTrue(voting.isOpen());
    }
    
    @Test
    public void testIsNotOpen() {
        Voting voting = new Voting("123");
        voting.setEndTime(Rfc3339.parse("1970-01-01T12:00:00.000Z"));
        assertFalse(voting.isOpen());
    }
    
    @Test
    public void testIsClosed() {
        Voting voting = new Voting("123");
        voting.setEndTime(Rfc3339.parse("1970-01-01T12:00:00.000Z"));
        assertTrue(voting.isClosed());
    }
    
    @Test
    public void testIsNotClosed() {
        Voting voting = new Voting("123");
        voting.setEndTime(Rfc3339.parse("3000-01-01T12:00:00.000Z"));
        assertFalse(voting.isClosed());
    }
    
    @Test
    public void testIsUpcoming() {
        Voting voting = new Voting("123");
        voting.setStartTime(Rfc3339.parse("9999-01-01T12:00:00.000Z"));
        assertTrue(voting.isUpcoming());
    }
    
    @Test
    public void testIsNotUpcoming() {
        Voting voting = new Voting("123");
        voting.setStartTime(Rfc3339.parse("1970-01-01T12:00:00.000Z"));
        voting.setEndTime(Rfc3339.parse("1971-01-01T12:00:00.000Z"));
        assertFalse(voting.isUpcoming());
    }
    
    @Test (expected = NumberFormatException.class)
    public void testBadStartTime() {
        Voting voting = new Voting("123");
        voting.setStartTime(Rfc3339.parse("asd"));
    }
    
    @Test (expected = RuntimeException.class)
    public void testEndTimeBeforeStartTimeException() {
        Voting voting = new Voting("123");
        voting.setStartTime(Rfc3339.parse("1971-01-01T12:00:00.000Z"));
        voting.setEndTime(Rfc3339.parse("1970-01-01T12:00:00.000Z"));
    }
    
}
