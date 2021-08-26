package com.example;

import java.io.*;
import java.nio.file.*;
import java.text.BreakIterator;
import java.util.*;
import java.util.stream.Collectors;

public class Translator {

    private static final Map<List<String>, String> dictionaries = Translator.getDictionaries();

    /**
     * @param translateFrom         the language from which the translation takes place
     * @param translateTo           the language to which the translation takes place
     * @param pathToFileToTranslate path to the file that needs to be translated
     * @return returns the translated version of the entire file
     * @throws IOException
     */
    public static String translateFile(String translateFrom, String translateTo, String pathToFileToTranslate) throws IOException {

        String text = Files.readAllLines(Paths.get(pathToFileToTranslate)).stream().reduce((a, b) -> a + b).get();
        BreakIterator iterator = BreakIterator.getSentenceInstance();
        iterator.setText(text);
        List<String> sentences = new ArrayList<>();

        int start = iterator.first();
        int end = iterator.next();

        /**
         * splitting the file into multiple sentences
         */
        while (end != BreakIterator.DONE) {

            int newEnd = iterator.next();
            if (newEnd == BreakIterator.DONE) {

                sentences.add(text.substring(start, end).trim());
                end = newEnd;

            } else {

                sentences.add(text.substring(start, end - 1).trim());
                start = end;
                end = newEnd;
            }
        }

        /**
         * converting each sentence and joining them into a single string which is then
         * returned
         */

        return sentences.parallelStream().map(sentence -> translateSentence(translateFrom, translateTo, sentence))
                .collect(Collectors.joining(" "));
    }

    /**
     * @param translateFrom the language from which the translation takes place
     * @param translateTo   the language to which the translation takes place
     * @param sentence      the sentence that needs to be translated
     * @return returns the translated version of the sentence
     */
    public static String translateSentence(String translateFrom, String translateTo, String sentence) {

        StringBuilder sb = new StringBuilder();
        String punctuation = sentence.substring(sentence.length() - 1);

        /**
         * reading all the words from the dictionary
         */
        List<Word> wordsDictionary = readWordsFromDictionary(getPathToDictionary(translateFrom, translateTo));
        String[] sentenceSplit = sentence.split("[,.;!? ]+");
        List<Integer> nounsAlreadyConverted = new ArrayList<>();

        for (int i = 0; i < sentenceSplit.length; i++) {

            if (translateFrom.equals("English") && translateTo.equals("Romanian")) {
                translateFromEnglishToRomanian(sb, wordsDictionary, sentenceSplit, i, nounsAlreadyConverted);
            } else if (translateFrom.equals("English") && translateTo.equals("English")){
                return "Yes yes";
            }
        }
        String result = sb.toString().trim();
        if(result.length() != 0){
            return (result.substring(0, 1).toUpperCase() + result.substring(1) + punctuation);
        }
            return null;
    }

    /**
     * @param indexWordToConvert the position of the word that needs to be converted
     *                           inside the sentence
     * @param sentenceSplit      the sentence split into words
     * @param wordsDictionary    the list of words from the dictionary
     * @return returns the correct conversion of the desired word from the sentence
     */
    public static String wordConvert(int indexWordToConvert, String[] sentenceSplit, List<Word> wordsDictionary) {

        if (Word.isPartOfSpeech(wordsDictionary, sentenceSplit[indexWordToConvert]) != null) {

            if (Word.isPartOfSpeech(wordsDictionary, sentenceSplit[indexWordToConvert]).get(0).equals("n")) {

                return Word.convertNoun(wordsDictionary, indexWordToConvert, sentenceSplit);

            } else if (Word.isPartOfSpeech(wordsDictionary, sentenceSplit[indexWordToConvert]).get(0).equals("v")) {

                return Word.convertVerb(wordsDictionary, indexWordToConvert, sentenceSplit);

            } else if (Word.isPartOfSpeech(wordsDictionary, sentenceSplit[indexWordToConvert]).get(0).equals("adj")) {

                return Word.convertAdjective(wordsDictionary, indexWordToConvert, sentenceSplit);

            } else if (Word.isPartOfSpeech(wordsDictionary, sentenceSplit[indexWordToConvert]).get(0).equals("num")) {

                return Word.convertNumeral(wordsDictionary, indexWordToConvert, sentenceSplit);

            } else if (Word.isPartOfSpeech(wordsDictionary, sentenceSplit[indexWordToConvert]).get(0).equals("pp")) {

                return Word.convertPersonalPronoun(wordsDictionary, indexWordToConvert, sentenceSplit);

            } else if (Word.isPartOfSpeech(wordsDictionary, sentenceSplit[indexWordToConvert]).get(0).equals("adv")
                    || Word.isPartOfSpeech(wordsDictionary, sentenceSplit[indexWordToConvert]).get(0).equals("prep")
                    || Word.isPartOfSpeech(wordsDictionary, sentenceSplit[indexWordToConvert]).get(0).equals("conj")) {

                return Word.convertSimpleWord(wordsDictionary, sentenceSplit[indexWordToConvert]);
            }

        }

        /**
         * if the word is not in the dictionary the method will just return the word in
         * english (I observed that this is how Google Translate behaves in situations
         * like these)
         */
        return sentenceSplit[indexWordToConvert];
    }

    /**
     * Reads all the words from the txt file and constructs corresponding com.example.Word
     * objects
     *
     * @param pathToDictionary path to the dictionary
     * @return returns the list of com.example.Word objects newly created
     */
    public static List<Word> readWordsFromDictionary(String pathToDictionary) {
        List<String> dictionary = new ArrayList<>();

        try {
            dictionary = Files.readAllLines(Paths.get(pathToDictionary));
        } catch (Exception e) {

            System.out.println("Path to dictionary is null.");
            return new ArrayList<>();
        }

        return dictionary.stream().map(line -> new Word(line.split(","))).toList();
    }

    public static Map<List<String>, String> getDictionaries() {

        Map<List<String>, String> dictionaries = new HashMap<>();
        dictionaries.put(Arrays.asList("English", "Romanian"), "/Users/georgeile/IdeaProjects/project-translator/translator/src/main/resources/scripts/dictionary.txt");

        return dictionaries;
    }

    public static StringBuilder translateFromEnglishToRomanian(StringBuilder sb, List<Word> wordsDictionary,
                                                               String[] sentenceSplit, int indexWordToCheck,
                                                               List<Integer> nounsAlreadyConverted) {

        /**
         * the following lines of code deal with situations in which in English the
         * adjectives normally sit in front of the noun that they determine, however in
         * Romanian usually they are situated right after the noun
         */
        if (Word.isPartOfSpeech(wordsDictionary, sentenceSplit[indexWordToCheck]) != null
                && Word.isPartOfSpeech(wordsDictionary, sentenceSplit[indexWordToCheck]).get(0).equals("adj")
                && Word.checkAdjectiveDeterminesNoun(wordsDictionary, indexWordToCheck, sentenceSplit) != -1) {

            if (!nounsAlreadyConverted
                    .contains(Word.checkAdjectiveDeterminesNoun(wordsDictionary, indexWordToCheck, sentenceSplit))) {

                nounsAlreadyConverted.add(Word.checkAdjectiveDeterminesNoun(wordsDictionary, indexWordToCheck, sentenceSplit));

                sb.append(" ").append(wordConvert(Word.checkAdjectiveDeterminesNoun(wordsDictionary, indexWordToCheck, sentenceSplit),
                        sentenceSplit, wordsDictionary));
            }

        }

        /**
         * here we firstly translate the noun, and then we will add the adjectives
         * afterwards
         */
        if (!nounsAlreadyConverted.contains(indexWordToCheck)) {

            sb.append(" ").append(wordConvert(indexWordToCheck, sentenceSplit, wordsDictionary));
        }
        return sb;
    }

    public static String getPathToDictionary(String translateFrom, String translateTo) {

        for (Map.Entry<List<String>, String> entry : dictionaries.entrySet()) {

            if (entry.getKey().get(0).equals(translateFrom) && entry.getKey().get(1).equals(translateTo)) {

                return entry.getValue();
            }
        }
        return null;
    }
}