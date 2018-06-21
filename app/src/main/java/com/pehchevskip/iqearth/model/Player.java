package com.pehchevskip.iqearth.model;

import com.pehchevskip.iqearth.controlers.GameControler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by pehchevskip on 18-May-18.
 */

public class Player {

    private String nickname;
    private int score;
    private Map<String, List<String>> answers;
    private GameControler.GameStatus gameStatus;
    private String ipAddress;

    public Player() {
        this("Default nickname");
    }

    public Player(String nickname) {
        this.nickname = nickname;
        this.score = 0;
        this.answers = new HashMap<>();
        this.answers.put("countries", new ArrayList<String>());
        this.answers.put("animals", new ArrayList<String>());
        this.answers.put("mountains", new ArrayList<String>());
        this.answers.put("cities", new ArrayList<String>());
    }
    public void setGameStatus(GameControler.GameStatus gameStatus){
        this.gameStatus=gameStatus;
    }
    public GameControler.GameStatus getGameStatus(){
        return gameStatus;
    }
    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score += score;
    }

    public void setAnswers(Map<String, List<String>> answers) {
        this.answers = answers;
    }

    public Map<String, List<String>> getAnswers() {
        return answers;
    }

    public List<String> getAnswers(String key) {
        return answers.get(key);
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}
