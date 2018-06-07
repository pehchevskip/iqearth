package com.pehchevskip.iqearth.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by pehchevskip on 18-May-18.
 */

public class Game {

    private int time;
    private List<Player> players;
    private char letter;

    public Game() {
        players = new ArrayList<>();
    }

    public Game(int time, char letter) {
        this.time = time;
        this.letter = letter;
        players = new ArrayList<>();
    }

    public Game(int time) {
        this.time = time;
        this.players = new ArrayList<>();
        Random rnd = new Random();
        char c = (char) (rnd.nextInt(26) + 'a');
        this.letter=c;
    }

    public int getTime() {
        return time;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public char getLetter() {
        return letter;
    }

    public void setLetter(char letter) {
        this.letter = letter;
    }

    public void setTime(int time) {

        this.time = time;
    }


}
