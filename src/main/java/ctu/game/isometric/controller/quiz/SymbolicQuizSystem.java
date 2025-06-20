package ctu.game.isometric.controller.quiz;

import ctu.game.isometric.model.dictionary.Word;
import ctu.game.isometric.model.dictionary.WordDefinition;
import ctu.game.isometric.util.WordNetValidator;

import java.util.*;
import java.util.regex.Pattern;

public class SymbolicQuizSystem {
    private final Set<String> learnedWords;
    private final Random random;
    private final WordNetValidator wordNetValidator;
    private final List<String> learnedWordsList;

    public SymbolicQuizSystem(Set<String> learnedWords, WordNetValidator wordNetValidator) {
        this.learnedWords = learnedWords;
        this.random = new Random();
        this.wordNetValidator = wordNetValidator;
        this.learnedWordsList = new ArrayList<>(learnedWords);
    }




    public Map<String, Object> generateContextualSentenceQuiz() {
        if (learnedWords.isEmpty()) {
            return createErrorResponse("No words available");
        }





        String wordUpperCase = getRandomWord(); // IS ALL UPPER CASE
        String word = wordUpperCase.toLowerCase(); // Normalize for dictionary lookup
        Word details = wordNetValidator.getWordDetails(word);

        if (details == null || details.getDefinitions().isEmpty()) {
            return createErrorResponse("No details available for word: " + word);
        }




        List<String> examples = new ArrayList<>();

        for (WordDefinition def : details.getDefinitions()) {
            if (def.getExamples() != null) {
                examples.addAll(def.getExamples());
            }
        }

        String sentence;
        if (examples.isEmpty()) {
            sentence = "The word ____ means: " + (details.getDefinitions().isEmpty()
                    ? "No definition available"
                    : details.getDefinitions().get(0).getDefinition().split(";")[0]);
        } else {
            sentence = examples.get(random.nextInt(examples.size()))
                    .replaceAll("(?i)\\b" + word + "\\b", "____");
        }

        Map<String, Object> quizData = new HashMap<>();
        quizData.put("type", "contextual_sentence");
        quizData.put("question", sentence);
        quizData.put("answer", wordUpperCase); // Use original uppercase for answer
        quizData.put("difficulty", 3);
        quizData.put("points", 2); // Example scoring

        return quizData;
    }

    private String getRandomWord() {
        return learnedWordsList.get(random.nextInt(learnedWordsList.size()));
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", true);
        response.put("message", message);
        return response;
    }
}