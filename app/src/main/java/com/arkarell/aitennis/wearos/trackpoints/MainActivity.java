package com.arkarell.aitennis.wearos.trackpoints;

import android.os.Bundle;
import android.os.Vibrator;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import static java.lang.Math.round;

public class MainActivity extends WearableActivity {

    private Button mWinByMyself;
    private Button mWinByMyOpponent;
    private Button mLostByMyOpponent;
    private Button mLostByMyself;

    private TextView mCountWinByMyself;
    private TextView mCountWinByMyOpponent;
    private TextView mCountLostByMyOpponent;
    private TextView mCountLostByMyself;
    private TextView mMyAggressiveMargin;
    private TextView mMyOpponentAggressiveMargin;
    private TextView mCountPoints;
    private TextView mElapsedTime;
    private TextView mScore;

    private RelativeLayout mPercentWinByMyselfLayout;
    private RelativeLayout mPercentWinLostByMyOpponentLayout;
    private RelativeLayout mPercentLostByMyselfLayout;
    private LinearLayout.LayoutParams mPercentWinByMyselfLayoutParams;
    private LinearLayout.LayoutParams mPercentWinLostByMyOpponentLayoutParams;
    private LinearLayout.LayoutParams mPercentLostByMyselfLayoutParams;
    private TextView mPercentWinByMyselfText;
    private TextView mPercentWinLostByMyOpponentText;
    private TextView mPercentLostByMyselfText;

    private int countWinByMyself;
    private int countWinByMyOpponent;
    private int countLostByMyOpponent;
    private int countLostByMyself;
    private int myAggressiveMargin;
    private int myOpponentAggressiveMargin;
    private int countPoints;

    private long startTimeStamp;
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Orange colors
        // #F7BA8B : Orange pastel
        // #EC8844 : Orange vif

        // Init counts
        countWinByMyself = 0;
        countWinByMyOpponent = 0;
        countLostByMyOpponent = 0;
        countLostByMyself = 0;
        myAggressiveMargin = 0;
        countPoints = 0;

        // Load UI elements
        mWinByMyself = (Button) findViewById(R.id.win_by_myself);
        mWinByMyOpponent = (Button) findViewById(R.id.win_by_my_opponent);
        mLostByMyOpponent = (Button) findViewById(R.id.lost_by_my_opponent);
        mLostByMyself = (Button) findViewById(R.id.lost_by_myself);

        mCountWinByMyself = (TextView) findViewById(R.id.count_win_by_myself);
        mCountWinByMyOpponent = (TextView) findViewById(R.id.count_win_by_my_opponent);
        mCountLostByMyOpponent = (TextView) findViewById(R.id.count_lost_by_my_opponent);
        mCountLostByMyself = (TextView) findViewById(R.id.count_lost_by_myself);
        mMyAggressiveMargin = (TextView) findViewById(R.id.my_aggressive_margin);
        mMyOpponentAggressiveMargin = (TextView) findViewById(R.id.my_opponent_aggressive_margin);
        mCountPoints = (TextView) findViewById(R.id.count_points);
        mElapsedTime = (TextView) findViewById(R.id.elapsed_time);
        mScore = (TextView) findViewById(R.id.score);

        mPercentWinByMyselfLayout = (RelativeLayout) findViewById(R.id.percent_win_by_myself_layout);
        mPercentWinLostByMyOpponentLayout = (RelativeLayout) findViewById(R.id.percent_winlost_by_my_opponent_layout);
        mPercentLostByMyselfLayout = (RelativeLayout) findViewById(R.id.percent_lost_by_myself_layout);
        mPercentWinByMyselfLayoutParams =  (LinearLayout.LayoutParams) mPercentWinByMyselfLayout.getLayoutParams();
        mPercentWinLostByMyOpponentLayoutParams = (LinearLayout.LayoutParams) mPercentWinLostByMyOpponentLayout.getLayoutParams();
        mPercentLostByMyselfLayoutParams = (LinearLayout.LayoutParams) mPercentLostByMyselfLayout.getLayoutParams();

        mPercentWinByMyselfText = (TextView) findViewById(R.id.percent_win_by_myself_text);
        mPercentWinLostByMyOpponentText = (TextView) findViewById(R.id.percent_winlost_by_my_opponent_text);
        mPercentLostByMyselfText = (TextView) findViewById(R.id.percent_lost_by_myself_text);

        // Init start timestamp
        startTimeStamp = System.currentTimeMillis();

        // Init vibrator
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        // Add listeners
        mWinByMyself.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                countWinByMyself += 1;
                mCountWinByMyself.setText(String.valueOf(countWinByMyself));
                updateMyAggressiveMargin(1);
                addOnePoint();
            }
        });
        mWinByMyOpponent.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                countWinByMyOpponent += 1;
                mCountWinByMyOpponent.setText(String.valueOf(countWinByMyOpponent));
                updateMyOpponentAggressiveMargin(1);
                addOnePoint();
            }
        });
        mLostByMyOpponent.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                countLostByMyOpponent += 1;
                mCountLostByMyOpponent.setText(String.valueOf(countLostByMyOpponent));
                updateMyOpponentAggressiveMargin(-1);
                addOnePoint();
            }
        });
        mLostByMyself.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                countLostByMyself += 1;
                mCountLostByMyself.setText(String.valueOf(countLostByMyself));
                updateMyAggressiveMargin(-1);
                addOnePoint();
            }
        });

        /* Configurations possibles :
            - Comportement générale de la montre - cadran  horaire :
                - Ecran actif en permanence ==> Utilisation d'un mode "ambient" - affichage de l'écran en cours en noir et blanc sombre
                   - Mode ambient au bout de 4-5" quand on regarde la montre
                   - Mode ambient immédiat quand on ne regarde plus la montre
                - Ecran actif en permanence désactivé ==> Pas de mode ambient - Ecran noir
                  - Ecran noir 5" après avoir réveillé la montre
                  - Ecran noir immédiatement quand on tourne le poignet
                - Dans les 2 modes : réveil de la montre en tournant le poignet et/ou en cliquant sur l'écran

            - Comportement de l'application
              - Ecran actif en permanence désactivé + no setAmbientEnabled()
                ==> Sortie de l'application au bout de 25" - La montre retourne sur le cadran horaire
                    mais l'aplication tourne toujours en tâche de fond
                    On pout relancer l'application et on retrouve notre pointage en cours
                    setAutoResumeEnabled(true) ne change rien à ce comportement
                ==> Pas utilisable pendant qu'on joue au tennis
              - Ecran actif en permanence activé + no setAmbientEnabled()
                ==> Idem écran en permanence désactivé
                    setAutoResumeEnabled(true) ne change rien à ce comportement
                ==> Pas utilisable pendant qu'on joue au tennis
              - Ecran actif en permanence désactivé + setAmbientEnabled()
                 - Pas de mode Ambient
                 - Passage sur écran noir dès qu'on tourne un peu le poignet pour ne plus voir l'écran
                 - Passage sur écran noir au bout de 25" si on reste à regarder la montre
                 - Réveil auto au bout de 1" si on fait un bon mouvement du poignet
                   sinon pas de réveil auto. Dans ce cas, on peut réveiller la montre en cliquant dessus
                   ou en faisant un balayage vers le haut : réveil en 1"
                 - setAutoResumeEnabled(true); ne change rien
              - Ecran actif en permanence activé + setAmbientEnabled()
                - Passage en mode ambient au bout de 25" si on reste à regarder la montre,
                  immédiatement si on tourne le poignet
                - Réveil auto si on tourne à nouveau le poignet mais ne fontionne pas toujours
                  sinon on peut réveiller la montre via un balayage vers le haut ou en cliquant
                  sur l'écran
                - En mode ambient, si on clique, on ne peut pas cliquer dans l'application et donc
                  entrer des points par erreur, sauf que parfois le réveil auto se fait en deux étapes
                  avec un premier réveil de l'appli mais en noir et blanc, puis un réveil complet
                  où on retrouve la couleur. Dans la 1ère phase de réveil, si on clique, on entre un
                  point - Grhhh, pas pratique
                - setAutoResumeEnabled(true); ne change rien
         */

        // Enables Always-on
        setAmbientEnabled ();
        setAutoResumeEnabled(true);
    }

    private void addOnePoint() {
        countPoints += 1;

        vibrator.vibrate(50);

        // Set nb points, score and elapsed time since the beginning of track
        setCountPoints();;
        setElapsedTime();
        setScore();

        // set percent bar
        float fwinByMyselfPercent = (countWinByMyself * 100) / countPoints;
        float fwinlostByMyOpponentPercent = ((countWinByMyOpponent + countLostByMyOpponent) *100 ) / countPoints;
        float flostByMyselfPercent = (countLostByMyself *100 ) / countPoints;

        int winByMyselfPercent = round(fwinByMyselfPercent);
        int winlostByMyOpponentPercent = round(fwinlostByMyOpponentPercent);
        int lostByMyselfPercent = round(flostByMyselfPercent);

        if (winByMyselfPercent != 0) {
            mPercentWinByMyselfLayoutParams.weight = winByMyselfPercent;
            mPercentWinByMyselfLayout.setLayoutParams(mPercentWinByMyselfLayoutParams);
            mPercentWinByMyselfText.setText(String.valueOf(winByMyselfPercent) + "%");
            if (winByMyselfPercent != 0)
                mPercentWinByMyselfLayout.setVisibility(View.VISIBLE);
        }

        if (winlostByMyOpponentPercent != 0) {
            mPercentWinLostByMyOpponentLayoutParams.weight = winlostByMyOpponentPercent;
            mPercentWinLostByMyOpponentLayout.setLayoutParams(mPercentWinLostByMyOpponentLayoutParams);
            mPercentWinLostByMyOpponentText.setText(String.valueOf(winlostByMyOpponentPercent) + "%");
            if (winlostByMyOpponentPercent != 0)
                mPercentWinLostByMyOpponentLayout.setVisibility(View.VISIBLE);
        }

        if (lostByMyselfPercent != 0) {
            mPercentLostByMyselfLayoutParams.weight = lostByMyselfPercent;
            mPercentLostByMyselfLayout.setLayoutParams(mPercentLostByMyselfLayoutParams);
            mPercentLostByMyselfText.setText(String.valueOf(lostByMyselfPercent) + "%");
            if (lostByMyselfPercent != 0)
                mPercentLostByMyselfLayout.setVisibility(View.VISIBLE);
        }
    }

    private void setCountPoints() {
        String sCountPoints = String.valueOf(countPoints);
        if (countPoints == 1)
            sCountPoints += " pt";
        else
            sCountPoints += " pts";
        mCountPoints.setText(sCountPoints);
    }

    private void setElapsedTime() {
        int elapsedTime = round((System.currentTimeMillis() - startTimeStamp) / 60000);
        String sElapsedTime = String.valueOf(elapsedTime) + " min.";
        mElapsedTime.setText(sElapsedTime);
    }

    private void setScore() {
        String score = String.valueOf(countWinByMyself + countLostByMyOpponent) + "/" +
                       String.valueOf(countWinByMyOpponent + countLostByMyself);
        mScore.setText(score);
    }

    private void updateMyAggressiveMargin(int delta) {
        myAggressiveMargin += delta;
        mMyAggressiveMargin.setText(String.valueOf(myAggressiveMargin));
    }

    private void updateMyOpponentAggressiveMargin(int delta) {
        myOpponentAggressiveMargin += delta;
        mMyOpponentAggressiveMargin.setText(String.valueOf(myOpponentAggressiveMargin));
    }


    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        setElapsedTime();
    }


}
