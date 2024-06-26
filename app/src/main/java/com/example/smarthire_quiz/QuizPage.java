package com.example.smarthire_quiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

public class QuizPage extends AppCompatActivity {

    private List<Question> questions;
    private int currentQuestionIndex = 0;
    private TextView questionText;
    private RadioGroup optionsGroup;
    private Button nextButton;
    private int score = 0;
    private TextView timerText;
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis;
    private SharedPreferences sharedPreferences;
    private TextView questionCounter;


    private static final String SHARED_PREF_TIME_LEFT = "timeLeftInMillis";
    private static final String SHARED_PREF_CURRENT_INDEX = "currentQuestionIndex";
    private static final String SHARED_PREF_SCORE = "score";
    private static final long COUNTDOWN_INTERVAL = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_page);

        questionText = findViewById(R.id.question_text);
        optionsGroup = findViewById(R.id.options_group);
        nextButton = findViewById(R.id.next_button);
        timerText = findViewById(R.id.timer_text);
        questionCounter = findViewById(R.id.question_counter);

        // Load or initialize SharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);


        if (savedInstanceState == null) {
            // Load quiz state from SharedPreferences if available
            if (sharedPreferences.contains(SHARED_PREF_TIME_LEFT)) {
                timeLeftInMillis = sharedPreferences.getLong(SHARED_PREF_TIME_LEFT, 600000); // Default to 10 minutes
                currentQuestionIndex = sharedPreferences.getInt(SHARED_PREF_CURRENT_INDEX, 0);
                score = sharedPreferences.getInt(SHARED_PREF_SCORE, 0);
            } else {
                // Initialize for a new quiz
                timeLeftInMillis = 600000; // 10 minutes in milliseconds
            }
            loadQuestions();
            showQuestion(currentQuestionIndex);
            startTimer();
        } else {
            // Restore state from savedInstanceState
            timeLeftInMillis = savedInstanceState.getLong(SHARED_PREF_TIME_LEFT);
            currentQuestionIndex = savedInstanceState.getInt(SHARED_PREF_CURRENT_INDEX);
            score = savedInstanceState.getInt(SHARED_PREF_SCORE);
            updateCountdownText();
            if (timeLeftInMillis <= 0) {
                endQuiz();
            } else {
                startTimer();
            }
        }

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer();
                currentQuestionIndex++;
                if (currentQuestionIndex < questions.size()) {
                    showQuestion(currentQuestionIndex);
                } else {
                    endQuiz();
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the current time left to the outState bundle
        outState.putLong(SHARED_PREF_TIME_LEFT, timeLeftInMillis);
        outState.putInt(SHARED_PREF_CURRENT_INDEX, currentQuestionIndex);
        outState.putInt(SHARED_PREF_SCORE, score);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore the time left from the savedInstanceState
        timeLeftInMillis = savedInstanceState.getLong(SHARED_PREF_TIME_LEFT);
        updateCountdownText();
        if (timeLeftInMillis <= 0) {
            endQuiz();
        } else {
            startTimer();
        }
    }

    private void loadQuestions() {
        InputStream is = getResources().openRawResource(R.raw.questions);
        Reader reader = new InputStreamReader(is);
        questions = new Gson().fromJson(reader, new TypeToken<List<Question>>() {}.getType());
    }

    private void showQuestion(int index) {
        Question question = questions.get(index);
        questionText.setText(question.getQuestion());

        optionsGroup.removeAllViews();
        for (String option : question.getOptions()) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(option);
            optionsGroup.addView(radioButton);
        }
        String questionCounterText = "Question " + (index + 1) + "/" + questions.size();
        questionCounter.setText(questionCounterText);
    }

    private void checkAnswer() {
        int selectedId = optionsGroup.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Toast.makeText(this, "Please select an answer", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton selectedRadioButton = findViewById(selectedId);
        String selectedAnswer = selectedRadioButton.getText().toString();
        String correctAnswer = questions.get(currentQuestionIndex).getAnswer();

        if (selectedAnswer.equals(correctAnswer)) {
            score++;
           // Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
        } //else {
           // Toast.makeText(this, "Incorrect! The correct answer is: " + correctAnswer, Toast.LENGTH_LONG).show();
        //}
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, COUNTDOWN_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountdownText();
            }

            @Override
            public void onFinish() {
                timeLeftInMillis = 0;
                endQuiz();
            }
        }.start();
    }

    private void updateCountdownText() {
        long minutes = timeLeftInMillis / 60000;
        long seconds = (timeLeftInMillis % 60000) / 1000;
        String timeLeft = String.format("%02d:%02d", minutes, seconds);
        timerText.setText(timeLeft);
    }

    private void endQuiz() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        Intent intent = new Intent(QuizPage.this, ResultActivity.class);
        intent.putExtra("SCORE", score);
        intent.putExtra("TOTAL_QUESTIONS", questions.size());
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Save the current quiz state to SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(SHARED_PREF_TIME_LEFT, timeLeftInMillis);
        editor.putInt(SHARED_PREF_CURRENT_INDEX, currentQuestionIndex);
        editor.putInt(SHARED_PREF_SCORE, score);
        editor.apply();

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
