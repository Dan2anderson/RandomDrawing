package com.anderson.daniel.randomdrawing;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements ContestantsFragment.OnFragmentInteractionListener, View.OnClickListener {
    //ContestantsFragment contestantsFragment;
    Boolean hasDrawingStarted;
    String[] contestantHopper;
    int[] contestantPoints;
    int winnerIndex;
    int numSelected;
    ListView listView1;
    EditText searchField;
    ContestantListAdapter contestantAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        hasDrawingStarted=false;
        winnerIndex=0;
        numSelected=0;
        setContentView(R.layout.activity_main);
        //ArrayList<ContestantItem> rowDetails = getListData();
        ArrayList<ContestantItem> rowDetails = new ArrayList<ContestantItem>();
        listView1 = (ListView) findViewById(R.id.contactsListView);
        listView1.setAdapter(new ContestantListAdapter(this,rowDetails));
        contestantAdapter = (ContestantListAdapter) listView1.getAdapter();


        searchField = (EditText)findViewById(R.id.searchField);
        searchField.setOnClickListener(this);
    }
    /**
     * This method generates an array list of names to auto populate the list adapter.
     * this method is used for testing only and should not be used in production
     * @return=ArrayList
     */
    private ArrayList getListData(){
        ArrayList<ContestantItem> contestantList = new ArrayList<ContestantItem>();
        ContestantItem cItem;
        for(int i=0;i<10;i++){
            cItem = new ContestantItem();
            cItem.setName("Danny Anderson");
            cItem.setPoints(5);
            contestantList.add(cItem);
        }
        return contestantList;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        //this must be implemented but there is currently no fragment interaction.
    }

    /**
     * if the name is found this returns the index position in the list.  If not found -99
     * @param=View
     *
     */
    public void findAlphabetical(View view){
        if(!hasDrawingStarted) {
            hideKeyboard();
            String inputTxt = searchField.getText().toString();
            int findAtIndex = contestantAdapter.searchAlphabetical(inputTxt) - 1;
            if (findAtIndex < 0) {
                findAtIndex = 0;
            }
            listView1.smoothScrollToPosition(findAtIndex);
        }else{
            Toast.makeText(MainActivity.this,"No can do.  Sorry the drawing has already started.",Toast.LENGTH_LONG).show();
        }
    }
    /**
     * This method adds the new name in alphabetical order.
     * @param=View
     *
     */
    public void addInAlpabetOrder(View view){
        if(!hasDrawingStarted) {
            //add the name in alphabetical order
            hideKeyboard();
            //ListView tmpListView = (ListView) contestantsFragment.getView().findViewById(R.id.contactsListView);
            String inputTxt = searchField.getText().toString();
            if(inputTxt.length()<=0){Toast.makeText(MainActivity.this,"Please enter a name",Toast.LENGTH_LONG).show();return;}
            int addAtIndex = contestantAdapter.searchAlphabetical(inputTxt);
            contestantAdapter.addItem(inputTxt, addAtIndex);
            listView1.smoothScrollToPosition(addAtIndex);
        }else{
            Toast.makeText(MainActivity.this,"No can do. Sorry, the drawing has already started.",Toast.LENGTH_LONG).show();
        }
    }
    /**
     * This method is called when the clear button is pressed.
     * @param=View
     */
    public void clearContestantList(View view){
        doublCheckClear();
    }
    /**
     * Selects one winner from those contestants who have not already won.
     * @param=View
     */
    public void pickRandomWiner(View view){
        String winnerName;
        String place ="";
        //handling for edge cases and constraints.
        if(hasDrawingStarted){
            if(numSelected >=contestantHopper.length){
                Toast.makeText(MainActivity.this,"Every one is already a winner, which means every one is also a loser :(",Toast.LENGTH_LONG).show();
                return;
            }else if(numSelected>10){
                Toast.makeText(MainActivity.this,"there is an arbitrary limit of 10 winners.  You really don't need more than that any ways. ",Toast.LENGTH_LONG).show();
                return;
            }
        }else if(contestantAdapter.getCount()<=1){
            Toast.makeText(MainActivity.this, "Must have 2 or more contestants", Toast.LENGTH_LONG).show();
            return;
        }
        //if the drawing has not started we need to do some first time set up.
        if(!hasDrawingStarted){
            contestantHopper = new String[contestantAdapter.getCount()];
            contestantPoints = new int[contestantAdapter.getCount()];
            contestantAdapter.generateHopper(contestantHopper,contestantPoints);
            //contestantHopper = contestantAdapter.getHopper();
            // contestantPoints = contestantAdapter.getPoints();
            contestantAdapter.clearAllItems();
            hasDrawingStarted=true;
        }
        //pick a winner.
        winnerName = chooseWinner();
        //display winner
        numSelected++;

        switch(numSelected){
            case 1: place="1st";
                break;
            case 2: place = "2nd";
                break;
            case 3: place = "3rd";
                break;
            case 4: place = "4th";
                break;
            case 5: place = "5th";
                break;
            case 6: place = "6th";
                break;
            case 7: place = "7th";
                break;
            case 8: place = "8th";
                break;
            case 9: place = "9th";
                break;
            case 10: place = "10th";
                break;

        }
        contestantAdapter.startDrawing(true);//passes the App state to the list adapter.
        contestantAdapter.addItem(winnerName +" " + place,winnerIndex);
    }

    /**
     * Implementation for the on click listener calls HideKeyboard.
     * @param=View
     * @return=boolean
     */
    @Override
    public boolean onTouchEvent(MotionEvent event){
        hideKeyboard();
        return true;
    }

    /**
     * this method will hide the soft keyboard when called.
     */
    public void hideKeyboard(){
        if(getCurrentFocus()!=null && getCurrentFocus() instanceof EditText){
            InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
        }

    }
    /**
     * Selects a name from the the list called the "contestantHopper".
     * @return=String
     */
    private String chooseWinner(){
        int sumPoints= 0;
        int placeHolder = 0;
        //sum all winner points
        for(int i = 0; i < contestantPoints.length-1; i++){
           sumPoints = sumPoints + contestantPoints[i];
            //reset winner points to be in increments instead of weighted.
            placeHolder = placeHolder + contestantPoints[i];
            contestantPoints[i]=placeHolder;
        }
        //Then Math selects a random number from 1 to 100 and who ever is closest to that number wins?
        int randInt = (int)(Math.random() *sumPoints);
        for(int i = 0; i<contestantPoints.length-1; i++){
            if(contestantPoints[i]>=randInt){
                return contestantHopper[i];
            }
        }
        return "there was a problem. ";//Logically the code should actually never be able get here.
    }

    /**
     * This method will display an alertDialog to the user if the user selects yes it will clear all the data that has been entered
     * and reset the project variables.
     */
    private void doublCheckClear(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("This will delete all names and restart the drawing. Are you sure you want to proceed?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                contestantAdapter.clearAllItems();
                contestantAdapter.startDrawing(false);
                contestantHopper= null;
                contestantPoints=null;
                hasDrawingStarted=false;
                winnerIndex=0;
                numSelected=0;
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog so do nothing
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }



    @Override
    public void onClick(View view){
        if(view == searchField) {
            searchField.getText().clear();
        }
    }
}
