/*
@author Angelina Picone
@version 1.0
Written: March 2022

purpose: cubes for the lonely game mechanics
 */

package com.zybooks.cubesforthelonely;

public class CftLGame {
    private int[] scores;
    private final int USER_ACCESSIBLE_SCORES = 13;
    private final int UPPER = 6;
    private int upperScore;
    private int lowerScore;
    private final int UPPER_BONUS = 35;
    private final int MAX_DICE = 5;
    private final int DIE_SIDES = 6;

    /**
     * default constructor - creates an array for the scores
     */
    public CftLGame() {
        // create array of scores
        scores = new int[USER_ACCESSIBLE_SCORES];
        for(int i = 0; i < USER_ACCESSIBLE_SCORES; i++)
        {
            scores[i] = 0;
        }
    }

    /**
     * calculates and then returns the score of the upper values
     * @return total of the upper scores
     */
    public int getUpperScore() { calculateUpperScore(); return upperScore; }

    /**
     * calculates the upper values' score and checks if the user has achieved the bonus
     * @return 35 if they have achieved it
     */
    public int getUpperBonus() {
        calculateUpperScore();
        if(upperScore-UPPER_BONUS > 62) { return UPPER_BONUS;}
        return 0;
    }

    /**
     * calculates and then returns the score of the lower values
     * @return total of the lower scores
     */
    public int getLowerScore() {calculateLowerScore(); return lowerScore; }

    /**
     * allows a score to be set at a certain index
     * @param index of the button that was pressed
     * @param score that has needs to be saved at the index
     */
    public void setScore(int index, int score) { scores[index] = score; }

    /**
     * calculates the score of the upper portion
     */
    private void calculateUpperScore() {
        upperScore = 0;
        for(int i = 0; i < UPPER; i++) { upperScore += scores[i]; }
        if(upperScore > 62) { upperScore += UPPER_BONUS; }
    }

    /**
     * calculates the score for the lower portion
     */
    private void calculateLowerScore() {
        lowerScore = 0;
        for(int i = UPPER; i < USER_ACCESSIBLE_SCORES; i++) { lowerScore += scores[i]; }
    }

    /**
     * finds the possible score for the button pressed
     * @param scoreButtonIndex - index of the button that was pressed (excludes totals)
     * @param pips - array of what the pips showing are
     * @return - score
     */
    public int findRoundScore(int scoreButtonIndex, int pips[]) {
        int round = 0;
        switch(scoreButtonIndex)
        {
            case 0:
                // ones score
                round  = countNumber(1, pips);
                break;
            case 1:
                // twos score
                round  = countNumber(2, pips);
                break;
            case 2:
                // threes score
                round  = countNumber(3, pips);
                break;
            case 3:
                // fours
                round  = countNumber(4, pips);
                break;
            case 4:
                // fives
                round  = countNumber(5, pips);
                break;
            case 5:
                // sixes
                round  = countNumber(6, pips);
                break;
            case 6:
                // 3 of kind
                int check = checkMultiples(pips);
                if(check == 3 || check == 1) {
                    for(int i = 0; i < MAX_DICE; i++) {
                        round += pips[i];
                    }
                }
                break;
            case 7:
                // 4 of kind -- same as chance -- fall through
                if(checkMultiples(pips) == 4) {
                    for(int i = 0; i < MAX_DICE; i++) {
                        round += pips[i];
                    }
                }
                break;
            case 8:
                // five of kind
                if(checkMultiples(pips) == 5) { round = 50; }
                break;
            case 9:
                // full house
                if(checkMultiples(pips) == 1) { round = 25; }
                break;
            case 10:
                // small straight
                int check2 = checkStraights(pips);
                if(check2 == 2 || check2 == 1) { round = 30; }
                break;
            case 11:
                // large straight
                if(checkStraights(pips) == 2) { round = 40; }
                break;
            case 12:
                // chance
                for(int i = 0; i < MAX_DICE; i++)
                {
                    round += pips[i];
                }
                break;
        }
        return round;
    }

    /**
     * checkMultiples - checks if numbers show up multiple times in the pips
     * @return - 0 (nothing), 1 (full house), 3 (3 of kind), 4 (4 of kind), 5 (5 of kind)
     */
    private int checkMultiples(int pips[]) {
        int[] shows = new int[DIE_SIDES];
        for(int i = 0; i < DIE_SIDES; i++) { shows[i] = 0;}
        for(int i = 0; i < MAX_DICE; i++) {
            for(int j = 1; j <= DIE_SIDES; j++) {
                if(pips[i] == j) {
                    shows[j-1] = shows[j-1] + 1;
                }
            }
        }

        for(int i = 0; i < DIE_SIDES; i++)
        {
            if(shows[i] == 3)
            {
                for(int j = 0; j < DIE_SIDES; j++) {
                    if (shows[j] == 2) { return 1; }
                }
                return 3;
            }
            if(shows[i] == 4) { return 4; }
            if(shows[i] == 5) { return 5; }
        }
        return 0;
    }

    /**
     * checkStraights - find if there is a straight (large/small) in the pips provided
     * @param pips - array of what the pips showing are
     * @return - 1 (small straight), 2 (large straight)
     */
    private int checkStraights(int[] pips) {
        int straightCount  = 0;
        for(int i = 1; i < MAX_DICE; i++)
        {
            if(pips[i]- pips[i-1] == 1)
            {
                straightCount++;
            }
        }
        if(straightCount == 4) { return 2; }

        // check for small straight (hard coded - couldn't figure out the code)
        boolean ones = pips[0] == 1 || pips[1] == 1;
        boolean twos = pips[1] == 2 || pips[2] == 2;
        boolean threes = pips[2] == 3 || pips[3] == 3;
        boolean fours = pips[3] == 4 || pips[4] == 4;
        if(ones && twos && threes && fours) { return 1; }
        twos = pips[0] == 2 || pips[1] == 2;
        threes = pips[1] == 3 || pips[2] == 3;
        fours = pips[2] == 4 || pips[3] == 4;
        boolean fives = pips[3] == 5 || pips[4] == 5;
        if(twos && threes && fours && fives) { return 1; }
        twos = pips[0] == 3 || pips[1] == 3;
        threes = pips[1] == 4 || pips[2] == 4;
        fours = pips[2] == 5 || pips[3] == 5;
        boolean sixes = pips[3] == 6 || pips[4] == 6;
        if(threes && fours && fives && sixes) { return 1; }
        return 0;
    }

    /**
     * countNumber - returns timesAppears * find, 0 if find none
     * @param find - number looking for
     * @param pips - array of what the pips showing are
     * @return timesAppears * find
     */
    private int countNumber(int find, int [] pips) {
        int sum = 0;
        for(int i = 0; i < MAX_DICE; i++)
        {
            if(pips[i] == find)
            {
                sum += find;
            }
        }
        return sum;
    }

}