package fr.aitennis.smartwatch.trackpoints;

public class Session {

    public int countWinByMyself;
    public int countWinByMyOpponent;
    public int countLostByMyOpponent;
    public int countLostByMyself;
    public int myAggressiveMargin;
    public int myOpponentAggressiveMargin;
    public int countPoints;

    public long startTimeStamp;
    public long endTimeStamp;

    public Session() {

        countWinByMyself = 0;
        countWinByMyOpponent = 0;
        countLostByMyOpponent = 0;
        countLostByMyself = 0;
        myAggressiveMargin = 0;
        myOpponentAggressiveMargin = 0;
        countPoints = 0;

        startTimeStamp = System.currentTimeMillis();
    }
}
