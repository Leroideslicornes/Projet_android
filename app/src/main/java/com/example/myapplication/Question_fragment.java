package com.example.myapplication;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.example.myapplication.databinding.ActivityQuestionFragmentBinding;

public class Question_fragment extends Fragment {

    private ActivityQuestionFragmentBinding binding;
    private String question;
    private String answer1;
    private String answer2;
    private String answer3;
    private String correctAnswer;

    public static Question_fragment newInstance(String question, String answer1, String answer2, String answer3, String correctAnswer) {
        Question_fragment fragment = new Question_fragment();
        Bundle args = new Bundle();
        args.putString("QUESTION",question);
        args.putString("ANSWER1",answer1);
        args.putString("ANSWER2",answer2);
        args.putString("ANSWER3",answer3);
        args.putString("CORRECT_ANSWER",correctAnswer);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        question = getArguments().getString("QUESTION", "");
        answer1 = getArguments().getString("ANSWER1", "");
        answer2 = getArguments().getString("ANSWER2", "");
        answer3 = getArguments().getString("ANSWER3", "");
        correctAnswer = getArguments().getString("CORRECT_ANSWER", "");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = ActivityQuestionFragmentBinding.inflate(inflater,container,false);
        binding.question.setText(question);
        binding.answer1.setText(answer1);
        binding.answer2.setText(answer2);
        binding.answer3.setText(answer3);
        return binding.getRoot();
    }

    public boolean isCorrect() {
        RadioButton selected = getView().findViewById(binding.radioGroup.getCheckedRadioButtonId());
        if(selected == null) {
            return false;
        }
        return correctAnswer.equals(selected.getTag());
    }
}
