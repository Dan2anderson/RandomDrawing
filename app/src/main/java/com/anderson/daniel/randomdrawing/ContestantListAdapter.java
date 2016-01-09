package com.anderson.daniel.randomdrawing;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Daniel on 11/18/2015.
 */
public class ContestantListAdapter extends BaseAdapter {
    private ArrayList<ContestantItem> contestantList;
    private LayoutInflater  layoutInflater;
    private boolean drawingStarted;

    /**
     * Constructor for the list Adapter.
     * @param=Context,ArrayList<ContestantItem>
     */
    public ContestantListAdapter(Context context, ArrayList<ContestantItem> contestantList){
        this.contestantList = contestantList;
        layoutInflater = LayoutInflater.from(context);
        drawingStarted=false;
    }
    /**
     * self explanitory
     * @return=int
     * etc
     */
    @Override
    public int getCount(){
        return contestantList.size();
    }

    /**
     * this is used by the view recycler to put the correct item back on screen.
     * @param=Int,View,ViewGroup
     * @return=View
     */
    public View getView(int position, View convertView, ViewGroup parentView){
        ViewHolder viewHolder;
        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.contestant_row_layout,null);//null might need to be activity_main
            viewHolder = new ViewHolder();
            viewHolder.nameView = (TextView) convertView.findViewById(R.id.nameView);
            viewHolder.buttonMinus = (ImageButton) convertView.findViewById(R.id.Minus);
            viewHolder.buttonPlus = (ImageButton) convertView.findViewById(R.id.Plus);
            viewHolder.pointsView = (TextView) convertView.findViewById(R.id.pointsView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }
        viewHolder.nameView.setText((contestantList.get(position).getName()));
        viewHolder.pointsView.setText(Integer.toString(contestantList.get(position).getPoints()));
        viewHolder.buttonMinus.setOnClickListener(minusListener);
        viewHolder.buttonPlus.setOnClickListener(addListener);
        if(drawingStarted){
            viewHolder.buttonPlus.setVisibility(View.GONE);
            viewHolder.buttonMinus.setVisibility(View.GONE);
        }else{
            viewHolder.buttonPlus.setVisibility(View.VISIBLE);
            viewHolder.buttonMinus.setVisibility(View.VISIBLE);
        }

        return convertView;
    }
    //on click listener for the minus button
    private View.OnClickListener minusListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            View parentRow = (View) v.getParent();
            ListView listView = (ListView) parentRow.getParent();
            final int position = listView.getPositionForView(parentRow);
            ContestantItem tmpContestant = (ContestantItem) contestantList.get(position);
            int tmpPoints = tmpContestant.getPoints()-1;
            if(tmpPoints<0){tmpPoints=0;}
            tmpContestant.setPoints(tmpPoints);
            notifyDataSetChanged();
        }
    };
    //on click listener for the plus button
    private View.OnClickListener addListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            View parentRow = (View) v.getParent();
            ListView listView = (ListView) parentRow.getParent();
            final int position = listView.getPositionForView(parentRow);
            ContestantItem tmpContestant = (ContestantItem) contestantList.get(position);
            int tmpPoints = tmpContestant.getPoints()+1;
            tmpContestant.setPoints(tmpPoints);
            notifyDataSetChanged();
        }
    };
    //This view holder is used in getView to store the view.
    static class ViewHolder {
        TextView nameView;
        TextView pointsView;
        ImageButton buttonMinus;
        ImageButton buttonPlus;
    }
    @Override
    public Object getItem(int index){
        return contestantList.get(index);
    }
    @Override
    public long getItemId(int index){
        return index;
    }

    public void startDrawing(boolean hasDrawingStarted){
        drawingStarted=hasDrawingStarted;
    }

    //this method should add the contestent to the listView.  must know what position to add it in.
    public void addItem(String name, int index){
        ContestantItem tempContestant = new ContestantItem();
        tempContestant.setName(name);
        tempContestant.setPoints(1);
        contestantList.add(index,tempContestant);
        notifyDataSetChanged();
    }

    public void clearAllItems(){
        contestantList.clear();
        notifyDataSetChanged();
    }

    /**
     * creates and fills the two arrays (hopper and points) these are used to select a winner once the drawing has started.
     * @param=String[],int[]
     */
    public void generateHopper(String[] hopper, int[] points){
        int index=0;
        for(ContestantItem contestant: contestantList){
            hopper[index]=contestant.getName();
            points[index]=contestant.getPoints();
            index++;
        }
    }

    public int searchAlphabetical(String name){
        //Binary search algorithm.
       return recursiveBinarySearch(name.toLowerCase(),0, contestantList.size() / 2,contestantList.size()/2);

    }
    //returns the position of the matching name or the closes
    /**
     * returns the position of the matching name or the last (hence closest) position.
     * This method uses a Binary search because I didn't want to get rusty on concepts I don't usually get the chance to use.
     * It would be better (more efficient) to just loop through the list.
     * @param=String,int,int,int
     * @return=int
     *
     */
    private int recursiveBinarySearch(String searchStr,int lastPosition,int midPosition,int segmentSize){
        boolean running=true;
        boolean searchUp=false;
        boolean matchFound=false;
        int index=0;
        if(midPosition < 0){
            return 0;}
        if(midPosition == contestantList.size()){
            return contestantList.size();}
        String nameOnList = contestantList.get(midPosition).getName().toLowerCase();
        while(running){
            //first thing we need to check that we are not going to run into index out of bounds exceptions.
            if((index >= nameOnList.length()-1) || (index >= searchStr.length()-1)){
                running=false;
                if(searchStr.length()<nameOnList.length()){searchUp=true;}
            }
            if(index<searchStr.length()){int leter1 = (int)searchStr.charAt(index);}
            if(index<nameOnList.length()){int letter2= (int)nameOnList.charAt(index);}
            if((int)searchStr.charAt(index) == (int)nameOnList.charAt(index)){
                if((index == nameOnList.length()-1) && (index == searchStr.length()-1)){
                    matchFound = true;
                    running=false;
                }

            }else if((int)searchStr.charAt(index) < (int)nameOnList.charAt(index)){
                searchUp=true;//if search name comes first alphabetically.
                running= false;
            }else if((int)searchStr.charAt(index) > (int)nameOnList.charAt(index)){
                searchUp=false;//if search name coms last alphabetically then searhc down
                running= false;
            }
            index = index+1;//if equal compare the next character.
        }//END while
        if(matchFound){
            return midPosition;
        }else{
            int tmpNewSegment = segmentSize/2;
            if(tmpNewSegment<1){tmpNewSegment=1;}
            if(searchUp){
                if(lastPosition-midPosition == -1){
                    return lastPosition+1; //yes we wan't it bellow the current list item.
                }else {
                    return recursiveBinarySearch(searchStr,midPosition, midPosition-tmpNewSegment,tmpNewSegment);
                }
            }else{
                if(lastPosition-midPosition==1 && segmentSize <=1){

                    return midPosition +1; //yes we want it bellow the current list item.
                }else {
                    return recursiveBinarySearch(searchStr,midPosition, midPosition + tmpNewSegment, tmpNewSegment);
                }
            }
        }
    }//End recursiveBinarySearch
}
