/*
@author Angelina Picone
@version 1.0
Written: March 2022

purpose: cubes for the lonely game mechanics
 */

package com.zybooks.cubesforthelonely;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Arrays;

//import java.util.Random;

public class MainActivity extends AppCompatActivity {

    // final fields
    private final int MAX_DICE = 5;
    private final long mTimerLength = 2000;
    private final int USER_ACCESSIBLE_SCORES = 13;
    // UI fields
    private ImageView[] mDiceImageViews;
    private TextView mCountTextView;
    private Dice[] mDice;
    private Button[] scoreButtons;
    private TextView mLowerScoreText;
    private TextView mUpperScoreText;
    private TextView mUpperBonusText;
    // other fields
    private boolean[] canRoll;
    private boolean[] canChoose;
    private int[] pips;
    private int mVisibleDice;
    private int rollCount;
    private int roundNum;
    private CountDownTimer mTimer;
    private CftLGame game;
    private String colorStr;
    private boolean[] achievements;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // roll count things
        rollCount = 0;
        mCountTextView = findViewById(R.id.rollCountView);

        // Create an array of ImageViews
        mDiceImageViews = new ImageView[MAX_DICE];
        mDiceImageViews[0] = findViewById(R.id.die1);
        mDiceImageViews[1] = findViewById(R.id.die2);
        mDiceImageViews[2] = findViewById(R.id.die3);
        mDiceImageViews[3] = findViewById(R.id.die4);
        mDiceImageViews[4] = findViewById(R.id.die5);

        // prep dice
        canRoll = new boolean[MAX_DICE];
        mDice = new Dice[MAX_DICE];
        mVisibleDice = MAX_DICE;
        for(int i = 0; i < MAX_DICE; i++)
        {
            canRoll[i] = true;
            mDice[i] = new Dice(i+1);
            int color = Color.parseColor("#FF0000");
            mDiceImageViews[i].setColorFilter(color);
        }

        // buttons
        scoreButtons = new Button[USER_ACCESSIBLE_SCORES];
        scoreButtons[0] = findViewById(R.id.onesText);
        scoreButtons[1] = findViewById(R.id.twosText);
        scoreButtons[2] = findViewById(R.id.threesText);
        scoreButtons[3] = findViewById(R.id.foursText);
        scoreButtons[4] = findViewById(R.id.fivesText);
        scoreButtons[5] = findViewById(R.id.sixesText);
        scoreButtons[6] = findViewById(R.id.threeKindScore);
        scoreButtons[7] = findViewById(R.id.fourKindScore);
        scoreButtons[8] = findViewById(R.id.fiveKindScore);
        scoreButtons[9] = findViewById(R.id.fullHouseScore);
        scoreButtons[10] = findViewById(R.id.smallStraightScore);
        scoreButtons[11] = findViewById(R.id.largeStraightScore);
        scoreButtons[12] = findViewById(R.id.chanceScore);

        // prep buttons
        canChoose = new boolean[USER_ACCESSIBLE_SCORES];
        for(int i = 0; i < USER_ACCESSIBLE_SCORES; i++) { canChoose[i] = true; }

        // instantiate instances
        pips = new int[MAX_DICE];
        game = new CftLGame();
        roundNum = 0;
        colorStr = findColor(1);

        mLowerScoreText = findViewById(R.id.lowerScore);
        mUpperScoreText = findViewById(R.id.upperScore);
        mUpperBonusText = findViewById(R.id.upperBonusText);
        achievements = new boolean[2];
        for(int i = 0; i < 2; i++) {achievements[i] = false; }
    }

    /**
     * controls the onClick actions of a score button
     * @param view
     */
    public void onScoreClick(View view) {
        findPips();

        // find which button has been pressed
        int scoreButtonIndex = -1;
        for(int i = 0; i < USER_ACCESSIBLE_SCORES; i++)
        {
            if(scoreButtons[i].getId() == view.getId())
            {
                scoreButtonIndex = i;
                i = USER_ACCESSIBLE_SCORES;
            }
        }

        // check if the button has been pressed before
        if(canChoose[scoreButtonIndex]) {
            // get score for that relates to the button
            int round = game.findRoundScore(scoreButtonIndex, pips);
            // change button text
            String s = round + "";
            scoreButtons[scoreButtonIndex].setText(s);
            // change button color
            int color = Color.parseColor("#000000");
            scoreButtons[scoreButtonIndex].setBackgroundColor(color);
            color = Color.parseColor("#FFFFFF");
            scoreButtons[scoreButtonIndex].setTextColor(color);
            // change button availability
            canChoose[scoreButtonIndex] = false;
            // change game's knowledge of score
            game.setScore(scoreButtonIndex, round);
            endRound();
        }
        else {
            if(roundNum >= USER_ACCESSIBLE_SCORES ) { endGame(); }
            else {
                // tell user that they cannot choose that button
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("You already have a score there. Pick a DIFFERENT score");
                builder.show();
            }
        }

    }

    /**
     * finds the number of the pips showing on the dice
     */
    private void findPips() {
        for(int i =0; i < MAX_DICE; i++)
        {
            pips[i] = mDice[i].getNumber();
        }
        // sorts the numbers in increasing order
        Arrays.sort(pips);
    }

    /**
     * controls the onClick actions of the roll button
     * @param view
     */
    public void onRollClick(View view) {
        if(rollCount < 3 && roundNum < USER_ACCESSIBLE_SCORES) {
            rollDice();
            for(int i = 0; i < USER_ACCESSIBLE_SCORES; i++)
            {
                scoreButtons[i].setEnabled(true);
            }
        }
        else if(roundNum >= USER_ACCESSIBLE_SCORES && rollCount < 3)
        {
            endGame();
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("You have reached the roll maximum. Pick a score")
            .setPositiveButton("continue...", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            })
            .show();
        }
    }

    /**
     * controls what happens when a die is clicked
     * @param view
     */
    public void onDieClick(View view) {
        // find the die and change if it can be rolled
        for(int i =0; i < MAX_DICE; i++)
        {
            if(mDiceImageViews[i].getId() == view.getId())
            {
                canRoll[i] = !canRoll[i];
            }
        }

        // change the colors of the dice
        for (int i = 0; i < mVisibleDice; i++) {
            if(!canRoll[i]){
                // change to black
                int color = Color.parseColor("#000000");
                mDiceImageViews[i].setColorFilter(color);
            }
            else {
                if(colorStr != null) {
                    int color = Color.parseColor(colorStr);
                    mDiceImageViews[i].setColorFilter(color);
                }
                else { makeRainbow(); }
            }
        }
        showDice();
    }

    /**
     * resets things for a new game
     */
    private void onNewGameClick() {
        for(int i = 0; i < USER_ACCESSIBLE_SCORES; i++) {
            canChoose[i] = true;
            int color = Color.parseColor("#F1E0FF");
            scoreButtons[i].setBackgroundColor(color);
            color = Color.parseColor("#000000");
            scoreButtons[i].setTextColor(color);
            scoreButtons[i].setText("0");
        }
        mUpperScoreText.setText("0");
        mUpperBonusText.setText("0");
        mLowerScoreText.setText("0");
        game = new CftLGame();
        roundNum = 0;
        colorStr = findColor(1);
        rollCount = 0;
    }

    /**
     * roll the dice (self-explanatory)
     */
    private void rollDice() {
        // increase count and make known to the user
        rollCount++;
        String str = "Number of Rolls: "+rollCount;
        mCountTextView.setText(str);


        if (mTimer != null) {
            mTimer.cancel();
        }

        // roll
        mTimer = new CountDownTimer(mTimerLength, 100) {
            public void onTick(long millisUntilFinished) {
                for (int i = 0; i < mVisibleDice; i++) {
                    if(canRoll[i]) {
                        mDice[i].roll();
                    }
                }
                showDice();
            }

            public void onFinish() {
               // mMenu.findItem(R.id.action_stop).setVisible(false);
            }
        }.start();
    }

    /**
     * display the dice
     */
    private void showDice() {
        // Display only the number of dice visible
        for (int i = 0; i < mVisibleDice; i++) {
            Drawable diceDrawable = ContextCompat.getDrawable(this, mDice[i].getImageId());
            mDiceImageViews[i].setImageDrawable(diceDrawable);
            mDiceImageViews[i].setContentDescription(Integer.toString(mDice[i].getNumber()));
        }

    }

    /**
     * end a round for a user
     */
    private void endRound() {
        // check to make sure the game has not ended
        if(roundNum < USER_ACCESSIBLE_SCORES) {
            // reset the dice
            for (int i = 0; i < MAX_DICE; i++) {
                canRoll[i] = true;
                mDice[i] = new Dice(i + 1);
                if(colorStr != null) {
                    int color = Color.parseColor(colorStr);
                    mDiceImageViews[i].setColorFilter(color);
                }
                else { makeRainbow(); }
            }
            rollCount = 0;
            // increase the round number
            roundNum++;

            // tell the user the round has ended
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("Round "+ roundNum + " over, click roll to continue");
            builder.setPositiveButton("continue...", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            })
                    .show();

            for(int i = 0; i < USER_ACCESSIBLE_SCORES; i++)
            {
                scoreButtons[i].setEnabled(false);
            }


            // update the totals
            String s = game.getLowerScore() + "";
            mLowerScoreText.setText(s);
            s = game.getUpperScore() + "";
            mUpperScoreText.setText(s);
            s = game.getUpperBonus() + "";
            mUpperBonusText.setText(s);
        }
        else
        {
            endGame();
        }
    }

    /**
     * ends the game
     */
    private void endGame()   {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        int total = game.getLowerScore() + game.getUpperScore();

        if(total < 6) {
            achievements[0] = true;
            builder.setMessage("Congrats! You have scored the worst possible score!");
            builder.setPositiveButton("continue...", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    changeDieColor();
                }
            })
            .show();
        }
        else if(total > 275) {
            achievements[1] = true;
            builder.setMessage("Congrats! You have scored the best possible score!");
            builder.setPositiveButton("continue...", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    changeDieColor();
                }
            })
            .show();
        }

        builder.setMessage("Total Score: " + total +
                "    Game is over");
        builder.setPositiveButton("new game...", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                onNewGameClick();
            }
        })
        .show();
    }

    /**
     * finds the hexcode for the selected color
     * @param choice of color
     * @return
     */
    private String findColor(int choice) {
        switch(choice)
        {
            case 1:
                // red
                return "#FF0000";
            case 2:
                // orange
                return "#DF7A00";
            case 3:
                // green
                return "#00AA00";
            case 4:
                // blue
                return "#0000FF";
            case 5:
                // purple
                return "#6a0dad";
        }
        return "#FF0000";
    }

    /**
     * allows the user to change the color of the dice
     * @param view
     */
    public void onChangeColorClick(View view) {

        String[] color = {"red", "orange", "green", "blue", "purple", "rainbow"};
        boolean[] checked = new boolean[6];
        AlertDialog builder = new AlertDialog.Builder(MainActivity.this)
            .setTitle("Choose ONE color")
            .setCancelable(true)
            .setMultiChoiceItems(color, checked, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                    colorStr = findColor(i+1);
                    if(i+1 > 5) {colorStr = null;}
                }
            })
            .setPositiveButton("continue...", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialogInterface, int i) {
                       changeDieColor();
                   }
            })
            .show();

    }

    /**
     * changes color of dice that can be rolled
     */
    private void changeDieColor() {
        if(colorStr != null) {
            for (int i = 0; i < mVisibleDice; i++) {
                if (canRoll[i]) {
                    int c = Color.parseColor(colorStr);
                    mDiceImageViews[i].setColorFilter(c);
                }
            }
        }
        else {
            makeRainbow();
        }
    }

    private void makeRainbow(){
        for (int i = 0; i < mVisibleDice; i++) {
            if (canRoll[i]) {
                int c = Color.parseColor(findColor(i+1));
                mDiceImageViews[i].setColorFilter(c);
            }
        }
    }

}