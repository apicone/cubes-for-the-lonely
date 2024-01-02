/*
@author Angelina Picone
@version 1.0
Written: March 2022

purpose: presents the instructions, closes when continue button is pressed
 */

package com.zybooks.cubesforthelonely;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Instructions extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);

        TextView mRulesTextView = findViewById(R.id.rulesText);

        String str = "Rules: \n" +
                "1.) There are a total of 13 rounds. The game \n     will end after the 13th round \n" +
                "2.) You must select a score to end each \n     round \n" +
                "3.) A round is comprised of 3 rolls/turns \n" +
                "4.) You may only roll when there are \n     rolls/turns remaining \n" +
                "5.) You may only select each score once \n" +
                "6.) There are hidden achievements and \n     abilities (not a rule, more of a challenge)\n" +
                "7.) Click a colored dice to hold it and click a \n     black dice to unhold it\n" +
                "8.) Click a score to save your score there\n" +
                "9.) A new game may only start once one is \n     completed\n" +
                "10.) When changing color, only choose one. \n     Last one selected will be the color \n     chosen";
        mRulesTextView.setText(str);
    }

    /**
     * controls what happens when continue button is clicked (moves onto MainActivity.java)
     * @param view
     */
    public void continueClick(View view) {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }
}