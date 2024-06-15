package com.example.smarthire_quiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
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

public class QuizPage extends AppCompatActivity {

    private List<Question> questions;
    private int currentQuestionIndex = 0;
    private TextView questionText;
    private RadioGroup optionsGroup;
    private Button nextButton;

    private int score = 0;

    private TextView timerText;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_page);

        questionText = findViewById(R.id.question_text);
        optionsGroup = findViewById(R.id.options_group);
        nextButton = findViewById(R.id.next_button);

        timerText = findViewById(R.id.timer_text);

        loadQuestions();
        showQuestion(currentQuestionIndex);

        startTimer();

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer();
                currentQuestionIndex++;
                if (currentQuestionIndex < questions.size()) {
                    showQuestion(currentQuestionIndex);
                } else {
                    endQuiz();
                //    Intent intent = new Intent(QuizPage.this, ResultActivity.class);
                //    intent.putExtra("SCORE", score);
                //    intent.putExtra("TOTAL_QUESTIONS", questions.size());
                //    startActivity(intent);
                //    finish();
                }
            }
        });
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
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Incorrect! The correct answer is: " + correctAnswer, Toast.LENGTH_LONG).show();
        }
    }

    private void startTimer() {

            countDownTimer = new CountDownTimer(60000, 1000) { // 1 minute in milliseconds

                public void onTick(long millisUntilFinished){
                    long minutes = millisUntilFinished / 60000;
                    long seconds = (millisUntilFinished % 60000) / 1000;
                    String timeLeft = String.format("%02d:%02d", minutes, seconds);
                    timerText.setText(timeLeft);
                }
            public void onFinish() {
                endQuiz();
            }
        }.start();
    }

    private void endQuiz() {
        Intent intent = new Intent(QuizPage.this, ResultActivity.class);
        intent.putExtra("SCORE", score);
        intent.putExtra("TOTAL_QUESTIONS", questions.size());
        startActivity(intent);
        finish();
    }
}