package com.pehchevskip.iqearth.controlers;

import android.util.Log;

import com.pehchevskip.iqearth.model.Game;
import com.pehchevskip.iqearth.model.Player;

import java.util.ArrayList;
import java.util.List;

public class GameControler {

    private static Game game;
    private static ArrayList<Player> players;
    private static GameControler controler;
    private static GameStatus gameStatus;
    private static Player currentPlayer;

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public enum GameStatus{
        WIN,LOSS,DRAW
    }

    //constructor
    public GameControler(){
        players=new ArrayList<Player>();
    }

    public static GameControler getInstance(){
        if(controler==null){
            controler=new GameControler();
        }
        return controler;

    }
    public void increaseScore(Player p,int score){
        for(Player pl :players){
            if(pl==p){
                pl.setScore(score);
            }
        }
    }

    public void increaseScore(String ipAddress, int score) {
        for(Player player : players) {
            if(player.getIpAddress() != null && player.getIpAddress().equals(ipAddress))
                player.setScore(score);
        }
    }


    public GameStatus getResults()
    {
        Player bestPlayer=findBestPlayer();
        currentPlayer=players.get(0);
        if(currentPlayer.getScore()>bestPlayer.getScore()){
            currentPlayer.setGameStatus(GameStatus.WIN);
        }
        else if(currentPlayer.getScore()<bestPlayer.getScore()){
            currentPlayer.setGameStatus(GameStatus.LOSS);
        }
        else if(currentPlayer.getScore()==bestPlayer.getScore()&&currentPlayer.getNickname().equals(bestPlayer.getNickname())){
            currentPlayer.setGameStatus(GameStatus.WIN);
        }
        else if(currentPlayer.getScore()==bestPlayer.getScore()&&!(currentPlayer.getNickname().equals(bestPlayer.getNickname()))){
            currentPlayer.setGameStatus(GameStatus.DRAW);
        }
        gameStatus=currentPlayer.getGameStatus();
        return gameStatus;
    }

    public String giveResults(String ip) {
        Player bestPlayer = findBestPlayer();
        Player me = findPlayerByIp(ip);
        if(bestPlayer == me) {
            return "Win";
        } else if(bestPlayer.getScore() == me.getScore()) {
            return "Draw";
        } else return "Loss";
    }

    private Player findBestPlayer(){
        Player maxPlayer=null;
        if(players.size()!=0){
        maxPlayer=players.get(0);}

        for(Player p:players){
            if(p.getScore()>maxPlayer.getScore()){
                maxPlayer=p;

            }
        }
        return maxPlayer;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game)
    {
        this.game=game;
    }

    public void addPlayer(Player player){
        this.players.add(player);
        Log.i("add", "added player with nick:" + player.getNickname() + ", and ip:" + player.getIpAddress());
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public List<Player> getOpponents() {
        return players.subList(1, players.size());
    }

    public Player getCurrentPlayer() {
        return players.get(0);
    }

    public Player findPlayerByIp(String ip) {
        for(Player player : players){
            if(player.getIpAddress() != null && player.getIpAddress().equals(ip)) return player;
        }
        return null;
    }

    public void clearPlayerList() {
        players.clear();
    }
}
