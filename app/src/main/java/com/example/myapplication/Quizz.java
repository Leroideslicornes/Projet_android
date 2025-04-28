package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import com.example.myapplication.databinding.ActivityQuizzBinding;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import android.util.Log;

import java.util.*;

public class Quizz extends AppCompatActivity implements Fragment_Question.OnAnswerSelectedListener {

    private ActivityQuizzBinding binding;
    private List<Fragment_Question> questions;
    private int currentQuestionIndex = 0;
    private int correctAnswers = 0;
    private String themeString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuizzBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        questions = new ArrayList<>();

        // Appel pour récupérer les questions depuis Firestore
        String Numsalle = getIntent().getStringExtra("NunSalle");
        Log.d("FirebaseDebug", "numero salle  : " + Numsalle);
        recupererThemePartie(Numsalle);

        // Bouton Restart (initialement invisible)
        binding.restart.setOnClickListener(v -> {
            currentQuestionIndex = 0;
            correctAnswers = 0;
            binding.restart.setVisibility(View.GONE);
            showNextQuestion();
        });
    }

    private void addQuestionsFromFirestore(String themeName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        questions.clear(); // Réinitialise la liste de questions
        Set<String> seenQuestions = new HashSet<>(); // Pour éviter les doublons

        if (themeName.equalsIgnoreCase("aleatoire")) {
            // Récupère tous les documents (thèmes) de la collection "Quiz"
            db.collection("Quiz")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            Map<String, Object> data = document.getData();

                            for (String key : data.keySet()) {
                                Object value = data.get(key);
                                if (value instanceof List) {
                                    List<?> questionList = (List<?>) value;

                                    if (questionList.size() >= 6) {
                                        String question = questionList.get(0).toString();

                                        if (!seenQuestions.contains(question)) {
                                            seenQuestions.add(question);

                                            String answer1 = questionList.get(1).toString();
                                            String answer2 = questionList.get(2).toString();
                                            String answer3 = questionList.get(3).toString();
                                            String answer4 = questionList.get(4).toString();
                                            String correctAnswer = questionList.get(5).toString();

                                            questions.add(Fragment_Question.newInstance(
                                                    question, answer1, answer2, answer3, answer4, correctAnswer));
                                        }
                                    }
                                }
                            }
                        }

                        if (!questions.isEmpty()) {
                            Collections.shuffle(questions);
                            showNextQuestion();
                        } else {
                            Toast.makeText(this, "Aucune question trouvée dans tous les thèmes.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Erreur de récupération (mode all)", Toast.LENGTH_SHORT).show()
                    );

        } else {
            // Récupère les questions d’un seul thème
            db.collection("Quiz")
                    .document(themeName)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Map<String, Object> data = documentSnapshot.getData();

                            for (String key : data.keySet()) {
                                Object value = data.get(key);
                                if (value instanceof List) {
                                    List<?> questionList = (List<?>) value;

                                    if (questionList.size() >= 6) {
                                        String question = questionList.get(0).toString();

                                        if (!seenQuestions.contains(question)) {
                                            seenQuestions.add(question);

                                            String answer1 = questionList.get(1).toString();
                                            String answer2 = questionList.get(2).toString();
                                            String answer3 = questionList.get(3).toString();
                                            String answer4 = questionList.get(4).toString();
                                            String correctAnswer = questionList.get(5).toString();

                                            questions.add(Fragment_Question.newInstance(
                                                    question, answer1, answer2, answer3, answer4, correctAnswer));
                                        }
                                    }
                                }
                            }

                            if (!questions.isEmpty()) {
                                Collections.shuffle(questions);
                                showNextQuestion();
                            } else {
                                Toast.makeText(this, "Aucune question trouvée dans ce thème.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "Thème '" + themeName + "' non trouvé.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Erreur de récupération du thème " + themeName, Toast.LENGTH_SHORT).show()
                    );
        }
    }

    private void showNextQuestion() {
        if (currentQuestionIndex < questions.size()) {
            Fragment_Question q = questions.get(currentQuestionIndex);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(binding.fragmentQuestion.getId(), q);
            ft.commit();
        }
    }

    @Override
    public void onAnswerSelected(boolean isCorrect, float timeTakenSeconds) {
        Log.d("MATTEO", "Temps pour répondre : " + timeTakenSeconds + " secondes");

        if (isCorrect) {
            correctAnswers++;
        }

        if (currentQuestionIndex < questions.size() - 1) {
            currentQuestionIndex++;
            showNextQuestion();
        } else {
            Intent intent = new Intent(Quizz.this, Classement.class);
            intent.putExtra("FOUND", Integer.toString(correctAnswers));
            intent.putExtra("NB_QUESTION", Integer.toString(questions.size()));
            startActivity(intent);
        }
    }

    private void recupererThemePartie(String numPartie) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference partieRef = db.collection("Partie").document("Partie_en_cours");

        partieRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<Object> partieList = (List<Object>) documentSnapshot.get(numPartie);
                if (partieList != null && partieList.size() > 3) { // au moins 5 éléments (index 0 à 3)
                    Object theme = partieList.get(3);
                    if (theme != null) {
                        themeString = theme.toString();
                        Toast.makeText(this, "Thème récupéré : " + themeString, Toast.LENGTH_SHORT).show();

                        // 🔥 Une fois qu'on a récupéré le thème, on peut charger les questions
                        Log.d("MATTEO", themeString);
                        addQuestionsFromFirestore(themeString);

                    } else {
                        Toast.makeText(this, "Thème vide", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "La liste est trop courte ou inexistante", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Le document Partie_en_cours n'existe pas", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Erreur Firebase : " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

}
