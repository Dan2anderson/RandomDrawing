package com.anderson.daniel.randomdrawing;

import android.widget.Button;

/**
 * Created by Daniel on 11/18/2015.
 */
public class ContestantItem {
    private String name;
    private int points;


    public String getName(){
        return name;
    }
    public void setName(String nameLocal){
        name=nameLocal;
    }
    public int getPoints(){
        return points;
    }
    public void setPoints(int pointsLocal){
        points = pointsLocal;
    }
}
