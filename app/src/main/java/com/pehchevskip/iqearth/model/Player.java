package com.pehchevskip.iqearth.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pehchevskip on 18-May-18.
 */

public class Player {

    private String nickname;
    private int score;
    private Map<String, List<String>> answers;

    public Player() {
        this.nickname = "Default nickname";
        this.score = 0;
        this.answers = new HashMap<>();
    }

    public Player(String nickname) {
        this.nickname = nickname;
        this.score = 0;
        this.answers = new HashMap<>();
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
        this.score = score;
    }

    public Map<String, List<String>> getAnswers() {
        return answers;
    }

    public void setAnswers(Map<String, List<String>> answers) {
        this.answers = answers;
    }

    public List<String> getAnswers(String key) {
        return this.answers.get(key);
    }
}
