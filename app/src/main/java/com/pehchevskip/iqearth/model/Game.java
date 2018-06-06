package com.pehchevskip.iqearth.model;

import java.util.ArrayList;
import java.util.List;

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

    public Game(char letter) {
        this.letter = letter;
        players = new ArrayList<>();
    }

    public Game(int time, char letter) {
        this.time = time;
        this.letter = letter;
    }

    public Game(int time, List<Player> players, char letter) {
        this.time = time;
        this.players = players;
        this.letter = letter;
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
