package com.pehchevskip.iqearth;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.pehchevskip.iqearth.bluetooth.BluetoothControler;
import com.pehchevskip.iqearth.controlers.GameControler;
import com.pehchevskip.iqearth.controlers.GameControler.GameStatus;

import static com.pehchevskip.iqearth.controlers.GameControler.GameStatus.*;

public class finishedGameActivity extends AppCompatActivity {

    //Views
    TextView gameStatusView;

    //Game Controller
    GameControler gameControler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finished_game);
        gameControler=GameControler.getInstance();
        gameStatusView=(TextView)findViewById(R.id.game_status);
        GameControler.GameStatus gameStatus=gameControler.getGameStatus();
        switch (gameStatus)
        {
            case WIN:
                gameStatusView.setText("Win");
                break;
            case LOSS:
                gameStatusView.setText("Loss");


        }
    }
}
