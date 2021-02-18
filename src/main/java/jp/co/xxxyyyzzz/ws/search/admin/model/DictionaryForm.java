package jp.co.xxxyyyzzz.ws.search.admin.model;

import java.util.List;

@SuppressWarnings("unused")
public class DictionaryForm {
    private String key;
    private List<String> words;
    private String newWord;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<String> getWords() {
        return words;
    }

    public void setWords(List<String> words) {
        this.words = words;
    }

    public String getNewWord() {
        return newWord;
    }

    public void setNewWord(String newWord) {
        this.newWord = newWord;
    }
}
