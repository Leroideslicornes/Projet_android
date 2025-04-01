package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;

import com.example.myapplication.databinding.ActivityQuestionBinding;

import java.util.ArrayList;
import java.util.List;

public class Question_Activity extends AppCompatActivity {

    private ActivityQuestionBinding binding;
    private List<Question_fragment> questionActivities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuestionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        questionActivities = new ArrayList<>();
        addQuestions();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        for(Question_fragment q : questionActivities) {
            ft.add(binding.questions.getId(),q);
        }
        ft.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*binding.validation.setOnClickListener(view -> {
            Intent intent = new Intent(Question_Activity.this,Result.class);
            intent.putExtra("FOUND",Integer.toString(getCorrectAnswer()));
            intent.putExtra("NB_QUESTION",Integer.toString(questionActivities.size()));
            startActivity(intent);
        });*/
    }

    private int getCorrectAnswer() {
        int found = 0;
        for(Question_fragment q : questionActivities) {
            if(q.isCorrect()) {
                found++;
            }
        }
        return found;
    }

    private void addQuestion(String question, String answer1, String answer2, String answer3, String correctAnswer) {
        questionActivities.add(Question_fragment.newInstance(question,answer1,answer2,answer3,correctAnswer));
    }

    private void addQuestions() {
        addQuestion("En quelle année est sorti Super Mario Kart ?","1982","1992","2002","2");
        addQuestion("En quelle année est sorti The Legend of Zelda: Spirit Tracks ?","2005","2007","2009","3");
        addQuestion("Combien existe-t-il de versions de GTA ?","5","6","12","3");
    }
}