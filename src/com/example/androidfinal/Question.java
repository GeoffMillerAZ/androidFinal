package com.example.androidfinal;
/*
 * Author: Geoff Miller
 * ID: Z1644162
 * Android Final with Professor Georgia Brown
 * This class holds the data for an individual question and
 * handles the method for checking a correct answer.
 * 
 * Extra grad requirement: 2nd data source hosted on turing.
 * One in asyncTask one in thread.
 */
import android.util.Log;

public class Question {
    private static final String TAG = "Android Final";
    public String question;
    public String answera;
    public String answerb;
    public String answerc;
    public String answerd;
    private int correct = 2;

    // Constructor
    public Question(String question, String answera, String answerb,
            String answerc, String answerd, String correct) {
        this.question = question;
        this.answera = answera;
        this.answerb = answerb;
        this.answerc = answerc;
        this.answerd = answerd;
        //tries to parse a string to int
        if (correct != null) {
            try{
                this.correct = Integer.parseInt(correct);
            } catch (NumberFormatException ex) {
                Log.e(TAG, "Question: Parse correct in constructor", ex);
            }
        }
    }

    // default constructor
    public Question() {
        this.question = "";
        this.answera = "";
        this.answerb = "";
        this.answerc = "";
        this.answerd = "";
        this.correct = -1;
    }
    
    /*
     * Checks to see if the answer that is given
     * as an argument matches the known correct
     * answer of the current question. if it is
     * correct it will return true, else it
     * returns false.
     */
    public boolean isCorrect(int chosen){
        if(this.correct == chosen)
            return true;
        else
            return false;
    }
}
