package com.example.androidfinal;

/*
 * Author: Geoff Miller
 * ID: Z1644162
 * Android Final with Professor Georgia Brown
 * This program downloads xml data from two web servers
 * and then parses the data into useable data objects.
 * This application then begins a quiz that will track
 * a user's success.
 * 
 * Extra grad requirement: 2nd data source hosted on turing.
 * One in asyncTask one in thread.
 */

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.content.res.XmlResourceParser;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
    // tag for logging
    private static final String TAG = "Android Final";

    // define constants
    public static final String DATA1 = "cppquizdata.xml";
    public static final String DATA2 = "javaquizdata.xml";
    public static final String SERVER1_URL = "http://faculty.cs.niu.edu/~gbrown/tmp/";
    public static final String SERVER2_URL = "http://students.cs.niu.edu/~z1644162/android/";
    public static final String DATA_URL1 = SERVER1_URL + DATA1;
    public static final String DATA_URL2 = SERVER2_URL + DATA2;
    // define gui elements
    private Button btnCpp, btnJava, btnA, btnB, btnC, btnD;
    private TextView txtTop;

    // define data
    private ArrayList<Question> cppQuestions;
    private ArrayList<Question> javaQuestions;
    private ArrayList<Question> thisList;
    private Question thisQuestion;
    private int index = 0;
    private int size = 0;
    int tCorrect = 0;
    int tTotal = 0;
    int maxQ = 10;

    /*
     * This overrides the onCreate method of Activity. This
     * will prep the GUI by assigning it to programmatic
     * variable names and assigning event handlers
     * for buttons. This will also initialize the
     * visibility of gui elements for starting a quiz.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i(TAG, "OnCreate()...");

        cppQuestions = new ArrayList<Question>();
        javaQuestions = new ArrayList<Question>();
        thisList = new ArrayList<Question>();

        // download the first set of data
        AsyncDownloader downloader1 = new AsyncDownloader();
        downloader1.setqList(cppQuestions);
        downloader1.setUrl(DATA_URL1);
        downloader1.execute();
        // download the 2nd set of data
        AsyncDownloader2 downloader2 = new AsyncDownloader2();
        downloader2.setqList(javaQuestions);
        downloader2.setUrl(DATA_URL2);
        downloader2.start();

        thisQuestion = new Question();
        getGui();
        prepGuiSelectQuiz();

    }

    /*
     * Makes only the quiz selection buttons visible
     */
    private void prepGuiQuizzing() {
        btnCpp.setVisibility(View.GONE);
        btnJava.setVisibility(View.GONE);
        btnA.setVisibility(View.VISIBLE);
        btnB.setVisibility(View.VISIBLE);
        btnC.setVisibility(View.VISIBLE);
        btnD.setVisibility(View.VISIBLE);
    }

    /*
     * Makes only the quiz answer buttons visible
     */
    private void prepGuiSelectQuiz() {
        btnCpp.setVisibility(View.VISIBLE);
        btnJava.setVisibility(View.VISIBLE);
        btnA.setVisibility(View.GONE);
        btnB.setVisibility(View.GONE);
        btnC.setVisibility(View.GONE);
        btnD.setVisibility(View.GONE);
    }

    /*
     * Begins a new quiz. This preps the GUI,
     * initializes quiz data and pulls a random
     * index for the starting question. It then
     * calls to advance the question.
     */
    private void startQuiz() {
        prepGuiQuizzing();
        tCorrect = 0;
        tTotal = 0;
        maxQ = 10;
        index = (int) (Math.random() * (thisList.size() - 1));
        size = thisList.size();
        advanceQ();
    }

    /*
     * Advances to the next question.
     * If the current question is less than 10, Sets
     * thisQuestion to the randomly selected
     * question so that other parts of the
     * program will be accessing the correct
     * question. It then updates the display
     * to show the question and options.
     * Keeps track of how many questions have
     * been asked.
     * 
     * If there have been 10 questions, it
     * preps the gui back for selecting a quiz
     * and displays the user's score on the screen.
     */
    private void advanceQ() {
        if (--maxQ >= 0) {
            thisQuestion = thisList.get(index++ % size);
            txtTop.setText(thisQuestion.question + "\n\n" + "A) "
                    + thisQuestion.answera + "\n" + "B) "
                    + thisQuestion.answerb + "\n" + "C) "
                    + thisQuestion.answerc + "\n" + "D) "
                    + thisQuestion.answerd);
        } else {
            prepGuiSelectQuiz();
            txtTop.setText("Quiz complete\nScore: " + tCorrect + "/" + tTotal
                    + " = " + (((double) tCorrect / (double) tTotal) * 100)
                    + "%" + "\n\nChoose your quiz type:");
        }
    }

    /*
     * This gets the GUI elements by ID and sets
     * them to java variables. This also creates
     * the event handlers for all buttons.
     */
    private void getGui() {
        txtTop = (TextView) findViewById(R.id.txtVwTop);
        txtTop.setText("Select your quiz type:");

        btnCpp = (Button) findViewById(R.id.btnCpp);
        btnCpp.setOnClickListener(new View.OnClickListener() {
            /*
             * Sets the propper question list and then
             * makes a call to start quiz
             */
            public void onClick(View v) {
                // Perform action on click
                thisList = cppQuestions;
                startQuiz();
            }
        });
        btnJava = (Button) findViewById(R.id.btnJava);
        btnJava.setOnClickListener(new View.OnClickListener() {
            /*
             * Sets the propper question list and then
             * makes a call to start quiz
             */
            public void onClick(View v) {
                // Perform action on click
                thisList = javaQuestions;
                startQuiz();
            }
        });
        btnA = (Button) findViewById(R.id.btnA);
        btnA.setOnClickListener(new View.OnClickListener() {
            /*
             * Sets the propper question list and then
             * makes a call to start quiz
             */
            public void onClick(View v) {
                // Perform action on click
                if (thisQuestion.isCorrect(1)) {
                    tCorrect++;
                }
                tTotal++;
                advanceQ();
            }
        });
        btnB = (Button) findViewById(R.id.btnB);
        btnB.setOnClickListener(new View.OnClickListener() {
            /*
             * Sets the propper question list and then
             * makes a call to start quiz
             */
            public void onClick(View v) {
                // Perform action on click
                if (thisQuestion.isCorrect(2)) {
                    tCorrect++;
                }
                tTotal++;
                advanceQ();
            }
        });
        btnC = (Button) findViewById(R.id.btnC);
        btnC.setOnClickListener(new View.OnClickListener() {
            /*
             * Sets the propper question list and then
             * makes a call to start quiz
             */
            public void onClick(View v) {
                // Perform action on click
                if (thisQuestion.isCorrect(3)) {
                    tCorrect++;
                }
                tTotal++;
                advanceQ();
            }
        });
        btnD = (Button) findViewById(R.id.btnD);
        btnD.setOnClickListener(new View.OnClickListener() {
            /*
             * Sets the propper question list and then
             * makes a call to start quiz
             */
            public void onClick(View v) {
                // Perform action on click
                if (thisQuestion.isCorrect(4)) {
                    tCorrect++;
                }
                tTotal++;
                advanceQ();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /*
     * This class is used to download data in the background.
     * It first downloads the data from a web server. The url
     * is specified by the url setter method below. Then
     * it parses the data into the list that is set by the setter
     * method.
     */
    private class AsyncDownloader extends AsyncTask<Object, String, Integer> {
        private ArrayList<Question> qList;
        private String url = "";

        /*
         * the core body of what the task will be doing in the
         * background. It first downloads the data then passes
         * the result to parse the data. It returns the number
         * of records found.
         */
        @Override
        protected Integer doInBackground(Object... params) {
            Log.i(TAG, "Async doInBackground: " + url);
            XmlPullParser receivedData = tryDownloadingXmlData();
            int recordsFound = tryParsingXmlData(receivedData);
            return recordsFound;
        }

        /*
         * This tries to download the xml data from
         * the webserver. on failure, it will return
         * null. If it is successful it will return the
         * xml data file.
         */
        private XmlPullParser tryDownloadingXmlData() {
            Log.i(TAG, "Async tryDownloadingXmlData");
            try {
                URL xmlUrl = new URL(url);
                XmlPullParser receivedData = XmlPullParserFactory.newInstance()
                        .newPullParser();
                receivedData.setInput(xmlUrl.openStream(), null);
                return receivedData;
            } catch (XmlPullParserException ex) {
                Log.e(TAG, "XmlPullParserException", ex);
            } catch (IOException ex) {
                Log.e(TAG, "XmlPullParserException", ex);
            }
            Log.i(TAG, "Async trydownloadingXmlData: returning null");
            return null;
        }

        /*
         * Takes xml data as an argument. It will return 0 on failure.
         * This checks to make sure that the data is not null. If
         * it is not null then it passes to another method for parsing.
         */
        private int tryParsingXmlData(XmlPullParser receivedData) {
            Log.i(TAG, "Async tryParsingXmlData...");
            if (receivedData != null) {
                Log.i(TAG, "Async tryParsingXmlData: not null");
                try {
                    return processReceivedData(receivedData);
                } catch (XmlPullParserException ex) {
                    Log.e(TAG, "XmlPullParserException", ex);
                } catch (IOException ex) {
                    Log.e(TAG, "XmlPullParserException", ex);
                }
            }
            Log.i(TAG, "Async tryParsingXmlData: is null");
            return 0;
        }

        /*
         * This loops through the xml data and examines each
         * tag. If the tag is a data element of a question,
         * it advances the tag to the text. then it stores the
         * text. then it advances to the end tag of that element
         * and continues to the next element's tag. It will
         * continue to loop until all elements have been
         * stored. At the end of each item tag it makes a
         * call to publishProgress which is part of the
         * asyncTask and will call the override of method:
         * onProgressUpdate, which actually adds the
         * question to the list.
         * This is very dependent on knowing exactly
         * what the xml data looks like.
         */
        private int processReceivedData(XmlPullParser xmlData)
                throws XmlPullParserException, IOException {
            Log.i(TAG, "Async processReceivedData...");
            // holds the number of records found.
            int recordsFound = 0;
            // set to what xml tag is the current in the below loop
            String tagName = "";

            // values to find in the xml
            String question = "", answera = "", answerb = "", answerc = "", answerd = "", correct = "";

            // prime the loop
            int eventType = -1;
            // loop until end of xml file
            try {
                while (eventType != XmlResourceParser.END_DOCUMENT) {
                    Log.i(TAG, "Async processReceivedData: " + recordsFound
                            + xmlData.getName());
                    tagName = xmlData.getName();

                    if (tagName != null) {
                        switch (eventType) {
                        case XmlResourceParser.START_TAG:
                            Log.i(TAG, "Async processReceivedData: start tag: "
                                    + tagName);
                            if (tagName.equals("item")) {
                                Log.i(TAG,
                                        "Async processReceivedData: start tag: "
                                                + tagName + "is item");
                            } else if (tagName.equals("question")) {
                                eventType = xmlData.next();
                                question = xmlData.getText();
                                eventType = xmlData.next();
                            } else if (tagName.equals("answera")) {
                                eventType = xmlData.next();
                                answera = xmlData.getText();
                                eventType = xmlData.next();
                            } else if (tagName.equals("answerb")) {
                                eventType = xmlData.next();
                                answerb = xmlData.getText();
                                eventType = xmlData.next();
                            } else if (tagName.equals("answerc")) {
                                eventType = xmlData.next();
                                answerc = xmlData.getText();
                                eventType = xmlData.next();
                            } else if (tagName.equals("answerd")) {
                                eventType = xmlData.next();
                                answerd = xmlData.getText();
                                eventType = xmlData.next();
                            } else if (tagName.equals("correct")) {
                                eventType = xmlData.next();
                                correct = xmlData.getText();
                                eventType = xmlData.next();
                            }
                            break;
                        case XmlResourceParser.TEXT:

                            break;
                        case XmlResourceParser.END_TAG:
                            Log.i(TAG, "Async processReceivedData: end tag: "
                                    + tagName);
                            if (tagName.equals("item")) {
                                Log.i(TAG,
                                        "Async processReceivedData: end tag: "
                                                + tagName + "is item");
                                recordsFound++;
                                publishProgress(question, answera, answerb,
                                        answerc, answerd, correct);
                            }
                            break;
                        default:
                            break;
                        }
                    }
                    eventType = xmlData.next();
                }
            } catch (Exception ex) {
                Log.e(TAG, "Async processReceivedData: while loop error: ", ex);
            }

            return recordsFound;
        }

        /*
         * Updates the list with the newest question pulled
         * from the xml parser.
         */
        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            qList.add(new Question(values[0], values[1], values[2], values[3],
                    values[4], values[5]));
            Log.i(TAG, "PUBLISHING " + Integer.toString(qList.size()));
            Log.i(TAG, " = " + qList.get(qList.size() - 1).question);
            Log.i(TAG, " = " + qList.get(qList.size() - 1).answera);
            Log.i(TAG, " = " + qList.get(qList.size() - 1).answerb);
            Log.i(TAG, " = " + qList.get(qList.size() - 1).answerc);
            Log.i(TAG, " = " + qList.get(qList.size() - 1).answerd);
        }

        public void setqList(ArrayList<Question> qList) {
            this.qList = qList;
        }

        public void setUrl(String inUrl) {
            url = inUrl;
        }
    }

    private class AsyncDownloader2 extends Thread {
        private ArrayList<Question> qList;
        private String url = "";

        /*
         * the core body of what the task will be doing in the
         * background. It first downloads the data then passes
         * the result to parse the data. It returns the number
         * of records found.
         */
        public void run() {
            Log.i(TAG, "Async doInBackground: " + url);
            XmlPullParser receivedData = tryDownloadingXmlData();
            tryParsingXmlData(receivedData);
        }

        /*
         * This tries to download the xml data from
         * the webserver. on failure, it will return
         * null. If it is successful it will return the
         * xml data file.
         */
        private XmlPullParser tryDownloadingXmlData() {
            Log.i(TAG, "Async tryDownloadingXmlData");
            try {
                URL xmlUrl = new URL(url);
                XmlPullParser receivedData = XmlPullParserFactory.newInstance()
                        .newPullParser();
                receivedData.setInput(xmlUrl.openStream(), null);
                return receivedData;
            } catch (XmlPullParserException ex) {
                Log.e(TAG, "XmlPullParserException", ex);
            } catch (IOException ex) {
                Log.e(TAG, "XmlPullParserException", ex);
            }
            Log.i(TAG, "Async trydownloadingXmlData: returning null");
            return null;
        }

        /*
         * Takes xml data as an argument. It will return 0 on failure.
         * This checks to make sure that the data is not null. If
         * it is not null then it passes to another method for parsing.
         */
        private int tryParsingXmlData(XmlPullParser receivedData) {
            Log.i(TAG, "Async tryParsingXmlData...");
            if (receivedData != null) {
                Log.i(TAG, "Async tryParsingXmlData: not null");
                try {
                    return processReceivedData(receivedData);
                } catch (XmlPullParserException ex) {
                    Log.e(TAG, "XmlPullParserException", ex);
                } catch (IOException ex) {
                    Log.e(TAG, "XmlPullParserException", ex);
                }
            }
            Log.i(TAG, "Async tryParsingXmlData: is null");
            return 0;
        }

        /*
         * This loops through the xml data and examines each
         * tag. If the tag is a data element of a question,
         * it advances the tag to the text. then it stores the
         * text. then it advances to the end tag of that element
         * and continues to the next element's tag. It will
         * continue to loop until all elements have been
         * stored. At the end of each item tag it makes a
         * call to publishProgress which is part of the
         * asyncTask and will call the method:
         * onProgressUpdate, which actually adds the
         * question to the list.
         * This is very dependent on knowing exactly
         * what the xml data looks like.
         */
        private int processReceivedData(XmlPullParser xmlData)
                throws XmlPullParserException, IOException {
            Log.i(TAG, "Async processReceivedData...");
            // holds the number of records found.
            int recordsFound = 0;
            // set to what xml tag is the current in the below loop
            String tagName = "";

            // values to find in the xml
            String question = "", answera = "", answerb = "", answerc = "", answerd = "", correct = "";

            // prime the loop
            int eventType = -1;
            // loop until end of xml file
            try {
                while (eventType != XmlResourceParser.END_DOCUMENT) {
                    Log.i(TAG, "Async processReceivedData: " + recordsFound
                            + xmlData.getName());
                    tagName = xmlData.getName();

                    if (tagName != null) {
                        switch (eventType) {
                        case XmlResourceParser.START_TAG:
                            Log.i(TAG, "Async processReceivedData: start tag: "
                                    + tagName);
                            if (tagName.equals("item")) {
                                Log.i(TAG,
                                        "Async processReceivedData: start tag: "
                                                + tagName + "is item");
                            } else if (tagName.equals("question")) {
                                eventType = xmlData.next();
                                question = xmlData.getText();
                                eventType = xmlData.next();
                            } else if (tagName.equals("answera")) {
                                eventType = xmlData.next();
                                answera = xmlData.getText();
                                eventType = xmlData.next();
                            } else if (tagName.equals("answerb")) {
                                eventType = xmlData.next();
                                answerb = xmlData.getText();
                                eventType = xmlData.next();
                            } else if (tagName.equals("answerc")) {
                                eventType = xmlData.next();
                                answerc = xmlData.getText();
                                eventType = xmlData.next();
                            } else if (tagName.equals("answerd")) {
                                eventType = xmlData.next();
                                answerd = xmlData.getText();
                                eventType = xmlData.next();
                            } else if (tagName.equals("correct")) {
                                eventType = xmlData.next();
                                correct = xmlData.getText();
                                eventType = xmlData.next();
                            }
                            break;
                        case XmlResourceParser.TEXT:

                            break;
                        case XmlResourceParser.END_TAG:
                            Log.i(TAG, "Async processReceivedData: end tag: "
                                    + tagName);
                            if (tagName.equals("item")) {
                                Log.i(TAG,
                                        "Async processReceivedData: end tag: "
                                                + tagName + "is item");
                                recordsFound++;
                                publishProgress(question, answera, answerb,
                                        answerc, answerd, correct);
                            }
                            break;
                        default:
                            break;
                        }
                    }
                    eventType = xmlData.next();
                }
            } catch (Exception ex) {
                Log.e(TAG, "Async processReceivedData: while loop error: ", ex);
            }

            return recordsFound;
        }

        /*
         * Updates the list with the newest question pulled
         * from the xml parser.
         */
        protected void publishProgress(String question, String answera,
                String answerb, String answerc, String answerd, String correct) {
            qList.add(new Question(question, answera, answerb, answerc,
                    answerd, correct));
            Log.i(TAG, "PUBLISHING " + Integer.toString(qList.size()));
            Log.i(TAG, " = " + qList.get(qList.size() - 1).question);
            Log.i(TAG, " = " + qList.get(qList.size() - 1).answera);
            Log.i(TAG, " = " + qList.get(qList.size() - 1).answerb);
            Log.i(TAG, " = " + qList.get(qList.size() - 1).answerc);
            Log.i(TAG, " = " + qList.get(qList.size() - 1).answerd);
        }

        public void setqList(ArrayList<Question> qList) {
            this.qList = qList;
        }

        public void setUrl(String inUrl) {
            url = inUrl;
        }
    }

}
