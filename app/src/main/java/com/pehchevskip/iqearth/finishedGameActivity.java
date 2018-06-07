package com.pehchevskip.iqearth;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.pehchevskip.iqearth.controlers.GameControler;
import com.pehchevskip.iqearth.controlers.GameControler.GameStatus;
import com.pehchevskip.iqearth.model.Player;

public class finishedGameActivity extends AppCompatActivity {

    //Views
    TextView gameStatusView;
    TextView your_score;
    TextView opp_score;

    //Game Controller
    GameControler gameControler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finished_game);
        gameControler=GameControler.getInstance();
        gameStatusView=(TextView)findViewById(R.id.game_status);
        Player player=gameControler.getPlayers().get(0);
        Log.d("Your Score", String.valueOf(player.getScore()));
        Player opponent=gameControler.getPlayers().get(1);
        Log.d("Opp Score",String.valueOf(opponent.getScore()));
        GameStatus gameStatus=gameControler.getGameStatus();
        Log.d("Opponent_nick",opponent.getNickname());
        Log.d("My_nick",player.getNickname());
        switch (gameStatus)
        {
            case WIN:
                gameStatusView.setText("Win");
                break;
            case LOSS:
                gameStatusView.setText("Loss");
                break;
            case DRAW:
                gameStatusView.setText("Draw");
                break;


        }
    }
}
