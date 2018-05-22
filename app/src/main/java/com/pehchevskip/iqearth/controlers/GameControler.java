package com.pehchevskip.iqearth.controlers;

import com.pehchevskip.iqearth.model.Game;
import com.pehchevskip.iqearth.model.Player;

import java.util.ArrayList;

public class GameControler {

    private static Game game;
    private static ArrayList<Player> players;
    private static GameControler controler;
    private static GameStatus gameStatus;
    private static Player currentPlayer;

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

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
        if (p==currentPlayer){
            currentPlayer.setScore(1);
        }
        for(Player pl :players){
            if(pl==p){
                pl.setScore(score);
            }
        }

    }
    public void setCurrentPlayer(Player p){
        currentPlayer=p;
    }

    public GameStatus getResults()
    {
        Player bestPlayer=findBestPlayer();
        if(currentPlayer.getScore()>bestPlayer.getScore()){
            currentPlayer.setGameStatus(GameStatus.WIN);
        }
        else if(currentPlayer.getScore()<bestPlayer.getScore()){
            currentPlayer.setGameStatus(GameStatus.LOSS);
        }
        else{
            currentPlayer.setGameStatus(GameStatus.DRAW);
        }
        gameStatus=currentPlayer.getGameStatus();
        return gameStatus;

    }
    private Player findBestPlayer(){
        Player maxPlayer=players.get(0);

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
    }

    public static ArrayList<Player> getPlayers() {
        return players;
    }
}
