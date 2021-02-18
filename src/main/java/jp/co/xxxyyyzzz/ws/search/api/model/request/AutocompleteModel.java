package jp.co.xxxyyyzzz.ws.search.api.model.request;

@SuppressWarnings("unused")
public class AutocompleteModel {
    private String keyword;
    private int count = 10;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
