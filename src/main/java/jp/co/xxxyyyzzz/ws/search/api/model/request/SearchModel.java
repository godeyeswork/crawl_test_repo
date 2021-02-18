package jp.co.xxxyyyzzz.ws.search.api.model.request;

@SuppressWarnings("unused")
public class SearchModel {
    /* Common Query Parameters */
    /**
     * クエリパーサー
     * key: defType
     * possible values: lucene dismax edismax
     */
    private String defType;
    /**
     * フィルタリングクエリ
     * key: fq
     */
    private String[] filterQueries;
    /**
     * 取得フィールド
     * key: fl
     */
    private String[] fields;
    /**
     * レスポンスタイプ
     * key: wt
     * possible values: json xml
     */
    private String writer;
    /* Common Query Parameters */

    /* Standard Query Parameters */
    /**
     * クエリ
     * key: q
     */
    private String query;
    /**
     * デフォルトの接続詞
     * key: q.op
     * possible values: AND OR
     */
    private String defaultOperator;
    /**
     * デフォルトフィールド
     * key: df
     */
    private String defaultField;
    /* Standard Query Parameters */

    /* DisMax Parameters */
    /**
     * Standard Query Parserで検索するクエリ（queryパラメータを使わない場合）
     * key: q.alt
     */
    private String altQuery;
    /**
     * クエリフィールド
     * key: df
     */
    private String[] queryFields;
    /**
     * ブーストクエリ
     * key: bq
     */
    private String[] boostQueries;  // bq
    /**
     * ブーストファンクション
     */
    private String[] boostFunctions;  // bq
    /* DisMax Parameters */

    /* Extended DisMax Parameters */
    /* Extended DisMax Parameters */


    public String getDefType() {
        return defType;
    }

    public void setDefType(String defType) {
        this.defType = defType;
    }

    public String[] getFilterQueries() {
        return filterQueries;
    }

    public void setFilterQueries(String[] filterQueries) {
        this.filterQueries = filterQueries;
    }

    public String[] getFields() {
        return fields;
    }

    public void setFields(String[] fields) {
        this.fields = fields;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getDefaultOperator() {
        return defaultOperator;
    }

    public void setDefaultOperator(String defaultOperator) {
        this.defaultOperator = defaultOperator;
    }

    public String getDefaultField() {
        return defaultField;
    }

    public void setDefaultField(String defaultField) {
        this.defaultField = defaultField;
    }

    public String getAltQuery() {
        return altQuery;
    }

    public void setAltQuery(String altQuery) {
        this.altQuery = altQuery;
    }

    public String[] getQueryFields() {
        return queryFields;
    }

    public void setQueryFields(String[] queryFields) {
        this.queryFields = queryFields;
    }

    public String[] getBoostQueries() {
        return boostQueries;
    }

    public void setBoostQueries(String[] boostQueries) {
        this.boostQueries = boostQueries;
    }

    public String[] getBoostFunctions() {
        return boostFunctions;
    }

    public void setBoostFunctions(String[] boostFunctions) {
        this.boostFunctions = boostFunctions;
    }
}
