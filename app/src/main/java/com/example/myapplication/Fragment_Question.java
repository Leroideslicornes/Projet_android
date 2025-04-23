package com.example.myapplication;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import com.example.myapplication.databinding.FragmentQuestionBinding;

public class Fragment_Question extends Fragment {

    private FragmentQuestionBinding binding;
    private String question;
    private String answer1;
    private String answer2;
    private String answer3;
    private String answer4;  // Ajout du quatrième choix
    private String correctAnswer;

    // Méthode pour créer une nouvelle instance avec les 4 réponses
    public static Fragment_Question newInstance(String question, String answer1, String answer2, String answer3, String answer4, String correctAnswer) {
        Fragment_Question fragment = new Fragment_Question();
        Bundle args = new Bundle();
        args.putString("QUESTION", question);
        args.putString("ANSWER1", answer1);
        args.putString("ANSWER2", answer2);
        args.putString("ANSWER3", answer3);
        args.putString("ANSWER4", answer4);  // Ajouter la 4e réponse
        args.putString("CORRECT_ANSWER", correctAnswer);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Récupération des données passées au fragment
        question = getArguments().getString("QUESTION", "");
        answer1 = getArguments().getString("ANSWER1", "");
        answer2 = getArguments().getString("ANSWER2", "");
        answer3 = getArguments().getString("ANSWER3", "");
        answer4 = getArguments().getString("ANSWER4", "");
        correctAnswer = getArguments().getString("CORRECT_ANSWER", "");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentQuestionBinding.inflate(inflater, container, false);

        // Affichage des données
        binding.question.setText(question);
        binding.answer1.setText(answer1);
        binding.answer2.setText(answer2);
        binding.answer3.setText(answer3);
        binding.answer4.setText(answer4);

        // Ajouter un listener sur les réponses
        binding.radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selected = getView().findViewById(checkedId);
            if (selected != null) {
                // Vérifier si la réponse est correcte
                boolean isCorrect = correctAnswer.equals(selected.getText().toString());
                if (getActivity() instanceof OnAnswerSelectedListener) {
                    ((OnAnswerSelectedListener) getActivity()).onAnswerSelected(isCorrect);
                }
            }
        });

        return binding.getRoot();
    }

    // Interface pour la communication avec l'activité principale
    public interface OnAnswerSelectedListener {
        void onAnswerSelected(boolean isCorrect);
    }
}
