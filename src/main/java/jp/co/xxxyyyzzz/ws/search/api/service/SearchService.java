package jp.co.xxxyyyzzz.ws.search.api.service;

import jp.co.xxxyyyzzz.ws.search.api.model.customize.Customizer;
import jp.co.xxxyyyzzz.ws.search.api.model.request.AutocompleteModel;
import jp.co.xxxyyyzzz.ws.search.api.model.request.SearchModel;
import jp.co.xxxyyyzzz.ws.search.api.model.solr.SolrSearchModel;
import jp.co.xxxyyyzzz.ws.search.common.utils.SolrUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.RangeFacet;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

@Service
public class SearchService {

    private static final String TRUE = "true";

    private final Logger logger = LoggerFactory.getLogger(SearchService.class);

    private final SolrClient solrClient;

    @SuppressWarnings("unused")
    @Autowired
    private Customizer customizer;

    @SuppressWarnings({"WeakerAccess"})
    @Autowired
    public SearchService(SolrClient solrClient) {
        this.solrClient = solrClient;
    }

/*
    @Autowired
    public void setCustomizer(Customizer customizer) {
        this.customizer = customizer;
    }
*/

    /**
     * サジェスト
     * @param model リクエストパラメータ
     * @param collection コレクション
     * @return サジェストリスト
     * @throws IOException If there is a low-level I/O error.
     * @throws SolrServerException if there is an error on the server
     */
    public List<String> autocomplete(AutocompleteModel model, String collection) throws IOException, SolrServerException {
        // リクエストから検索パラメータ取得
        Optional<String> keyword = Optional.ofNullable(model.getKeyword());
        Optional<Integer> count = Optional.of(model.getCount());

        // デフォルトパラメータを上書き
        HashMap<String, Object> params = new HashMap<>();
        params.put("count", 100);
        keyword.ifPresent(v -> params.put("keyword", ClientUtils.escapeQueryChars(v)));
        count.ifPresent(v -> params.put("count", v));

        Map<String, Float> map = new HashMap<>();
        String[] fields = customizer.getSuggestFields();
        for (String field : fields) {
            // 検索クエリ作成
            SolrQuery solrQuery = new SolrQuery();
            solrQuery.setQuery(field + ":" + cast(params.get("keyword")));
            solrQuery.setRows(cast(params.get("count")));
            solrQuery.setFields(field, "score");

            customizer.overrideSuggestRequest(solrQuery);

            //検索ログに残さない
            solrQuery.set("nolog", "true");

            // 検索実施
            debugQuery(solrQuery);
            QueryResponse res = solrClient.query(collection, solrQuery, SolrRequest.METHOD.POST);

            //検索結果整形
            SolrDocumentList results = customizer.overrideSuggestResponse(res);
            for (SolrDocument result : results) {
                map.put(cast(result.get(field)), cast(result.get("score")));
            }
        }

        List<Map.Entry<String, Float>> entries = new ArrayList<>(map.entrySet());
        entries.sort((entry1, entry2) -> {
            Float key1 = entry1.getValue();
            Float key2 = entry2.getValue();
            if (key1 < key2) {
                return 1;
            } else if (key1 > key2) {
                return -1;
            } else {
                return entry1.getKey().length() - entry2.getKey().length();
            }
        });
        List<String> list = new ArrayList<>();
        int size = 0;
        int max = cast(params.get("count"));
        for (Map.Entry<String, Float> entry : entries) {
            list.add(entry.getKey());
            if (++size >= max) {
                break;
            }
        }
        return list;
    }

    /**
     * Solrクエリをログ出力
     * @param q クエリ
     */
    private void debugQuery(SolrQuery q) {
        logger.debug(q.toString());
    }

    /**
     * 検索処理
     * @param model リクエストパラメータ
     * @param collection コレクション
     * @param pageable ページングパラメータ
     * @param request リクエスト
     * @return 検索結果
     */
    public Map<String, Object> search(SearchModel model, String collection, Pageable pageable, HttpServletRequest request) throws IOException, SolrServerException {
        // デフォルトパラメータを取得
        SolrSearchModel solrSearchModel = customizer.getModel(collection);

        // リクエストから検索パラメータ取得
        Optional<String> defType = Optional.ofNullable(model.getDefType());
        Optional<String[]> filterQueries = Optional.ofNullable(model.getFilterQueries());
        Optional<String[]> fields = Optional.ofNullable(model.getFields());
        Optional<String> writer = Optional.ofNullable(model.getWriter());
        Optional<String> query = Optional.ofNullable(model.getQuery());
        Optional<String> defaultOperator = Optional.ofNullable(model.getDefaultOperator());
        Optional<String> defaultField = Optional.ofNullable(model.getDefaultField());
        Optional<String> altQuery = Optional.ofNullable(model.getAltQuery());
        Optional<String[]> queryFields = Optional.ofNullable(model.getQueryFields());
        Optional<String[]> boostQueries = Optional.ofNullable(model.getBoostQueries());
        Optional<String[]> boostFunctions = Optional.ofNullable(model.getBoostFunctions());

        // デフォルトパラメータを上書き
        Map<String, Object> params = serializeParams(solrSearchModel.getParams());
        Map<String, String> fieldAlias = solrSearchModel.getFieldAlias();
        defType.ifPresent(v -> params.put("defType", v));
        filterQueries.ifPresent(v -> {
            replaceFieldAlias(v, fieldAlias, ":");
            Object def = params.get("fq");
            if (def != null) {
                if (def instanceof String[]) {
                    params.put("fq", ArrayUtils.addAll((String[]) def, v));
                } else if (def instanceof String) {
                    params.put("fq", ArrayUtils.addAll(new String[]{(String) def}, v));
                }
            } else {
                params.put("fq", v);
            }
        });
        fields.ifPresent(v -> params.put("fl", v));
        writer.ifPresent(v -> params.put("wt", v));
        query.ifPresent(v -> params.put("q", v));
        defaultOperator.ifPresent(v -> params.put("q.op", v));
        defaultField.ifPresent(v -> params.put("df", v));
        altQuery.ifPresent(v -> params.put("q.alt", v));
        queryFields.ifPresent(v -> params.put("qf", v));
        boostQueries.ifPresent(v -> params.put("bq", v));
        boostFunctions.ifPresent(v -> params.put("bf", v));

        customizer.overrideRequest(collection, model, params, request);

        if (pageable.getSort() == null || pageable.getSort().isUnsorted()) {
            pageable = customizer.getDefaultPageable(collection, pageable.getPageNumber(), pageable.getPageSize());
        }

        pageable = customizer.overridePageable(collection, pageable, request);

        //集計用のAliasを追加
        params.put("alias", collection);

        Set<String> keys = params.keySet();
        //ファセットパラメータがあるときはfacetをtrueに変える
        if (keys.contains("facet.field") || keys.contains("facet.range") | keys.contains("facet.query")) {
            params.put("facet", TRUE);
        }
        //ハイライトパラメータがあるときはhighlightをtrueに変える
        if (keys.contains("hl.fl")) {
            params.put("hl", TRUE);
        }

        // 検索クエリ作成
        SimpleQuery dataQuery = new SimpleQuery();
        dataQuery.addCriteria(new Criteria());
        dataQuery.setPageRequest(pageable);
        SolrQuery solrQuery = SolrUtil.solrQuery(dataQuery);
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof String) {
                solrQuery.set(key, (String) value);
            } else if (value instanceof Integer) {
                solrQuery.set(key, (Integer) value);
            } else if (value instanceof Boolean) {
                solrQuery.set(key, (Boolean) value);
            } else if (value instanceof List) {
                //noinspection unchecked
                final List<Object> list = (List) value;
                final String[] strings = new String[list.size()];
                solrQuery.set(key, list.toArray(strings));
            } else if (value != null) {
                //noinspection SuspiciousToArrayCall
                solrQuery.set(key, Arrays.asList((Object[]) value).toArray(new String[((Object[]) value).length]));
            }
        }

        // 検索実施
        debugQuery(solrQuery);
        QueryResponse res = solrClient.query(collection, solrQuery, SolrRequest.METHOD.POST);
        LinkedHashMap<String, Object> ret = new LinkedHashMap<>();

        //検索結果整形
        SolrDocumentList results = res.getResults();
        ret.put("docs", results);
        HashMap<String, Object> page = new HashMap<>();
        long numFound = results.getNumFound();
        page.put("totalElements", numFound);
        long totalPages = (numFound / pageable.getPageSize()) + 1;
        page.put("totalPages", totalPages);
        long start = pageable.getOffset();
        page.put("start", start + 1);
        long end = start + pageable.getPageSize();
        if (end > numFound) {
            end = numFound;
        }
        page.put("end", end);
        ret.put("pageable", page);
        if (keys.contains("facet.field")) {
            Map<String, Object> facetFieldsResponse = getFacetFieldsResponse(res.getFacetFields());
            if (!facetFieldsResponse.isEmpty()) {
                ret.put("facetFields", facetFieldsResponse);
            }
        }
        if (keys.contains("facet.range")) {
            Map<String, Object> facetFieldsResponse = getFacetRangesResponse(res.getFacetRanges());
            if (!facetFieldsResponse.isEmpty()) {
                ret.put("facetRanges", facetFieldsResponse);
            }
        }
        if (keys.contains("facet.query")) {
            ret.put("facetQueries", res.getFacetQuery());
        }
        if (keys.contains("hl.fl")) {
            ret.put("highlighting", res.getHighlighting());
        }

        customizer.overrideResponse(collection, model, params, res, ret, request);
        return ret;
    }

    @SuppressWarnings("SameParameterValue")
    private void replaceFieldAlias(String[] v, Map<String, String> fieldAlias, String suffix) {
        int length = v.length;
        if (length > 0) {
            for (Map.Entry<String, String> entry : fieldAlias.entrySet()) {
                for (int i = 0; i < length; i++) {
                    v[i] = v[i].replace(entry.getKey() + suffix, entry.getValue());
                }
            }
        }
    }

    /**
     * パラメータを直列化する
     * @param params パラメータ
     * @return 直列化したパラメータ
     */
    private Map<String, Object> serializeParams(Map<String, Object> params) {
        HashMap<String, Object> ret = new HashMap<>();
        serializeParam("", params, ret);
        return ret;
    }

    /**
     * パラメータを直列化する
     * @param parentKey 親のキー
     * @param src パラメータのマップ
     * @param dest 直列化したパラメータ
     */
    private void serializeParam(String parentKey, Map<String, Object> src, Map<String, Object> dest) {
        for (Map.Entry<String, Object> entry : src.entrySet()) {
            String key = entry.getKey();
            if (!parentKey.isEmpty()) {
                key = parentKey + "." + key;
            }
            Object value = entry.getValue();
            if (value instanceof Map) {
                boolean isList = true;
                for (Object o : ((Map) value).keySet()) {
                    if (!(o instanceof Integer || (o instanceof String && NumberUtils.isDigits((String) o)))) {
                        isList = false;
                        break;
                    }
                }
                if (!isList) {
                    //noinspection unchecked
                    serializeParam(key, (Map<String, Object>) value, dest);
                } else {
                    dest.put(key, ((Map) value).values().toArray(new Object[0]));
                }
            } else {
                dest.put(key, value);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T cast(Object obj) {
        return (T) obj;
    }

    private Map<String, Object> getFacetFieldsResponse(List<FacetField> facets) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (FacetField facet : facets) {
            Map<String, Object> values = new LinkedHashMap<>();
            for (FacetField.Count count : facet.getValues()) {
                values.put(count.getName(), count.getCount());
            }
            map.put(facet.getName(), values);
        }
        return map;
    }

    private Map<String,Object> getFacetRangesResponse(List<RangeFacet> facets) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (RangeFacet facet : facets) {
            Map<String, Object> values = new LinkedHashMap<>();
            values.put("before", facet.getBefore());
            for (Object count : facet.getCounts()) {
                RangeFacet.Count c = (RangeFacet.Count) count;
                values.put(c.getValue(), c.getCount());
            }
            values.put("after", facet.getAfter());
            map.put(facet.getName(), values);
        }
        return map;
    }

}
