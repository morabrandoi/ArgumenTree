package com.example.argumentree;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ComposeQuestionActivity extends AppCompatActivity {
    private Button btnSubmitQuestion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_question);

        btnSubmitQuestion = findViewById(R.id.btnSubmitQuestion);
        btnSubmitQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}