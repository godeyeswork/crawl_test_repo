package jp.co.xxxyyyzzz.ws.search.admin.service;

import jp.co.xxxyyyzzz.ws.search.common.utils.SolrUtil;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.util.NamedList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class QueryLogService {

    private static final String COLLECTION = "query_log";

    private final SolrClient solrClient;

    @SuppressWarnings("unused")
    @Autowired
    public QueryLogService(SolrClient solrClient) {
        this.solrClient = solrClient;
    }

    /**
     * 指定期間のサマリ取得
     * @param from 期間From
     * @param to 期間To
     * @return 集計サマリ
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> dashboard(String collection, Date from, Date to) throws IOException, SolrServerException {
        Map<String, Object> ret = new HashMap<>();
        Map<String, Integer> histogram = new LinkedHashMap<>();
        Map<String, Integer> ranking = new LinkedHashMap<>();
        Map<String, Map<String, Integer>> trend = new LinkedHashMap<>();
        Map<String, Integer> zero = new LinkedHashMap<>();
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery("*:*");
        solrQuery.setRows(0);
        String fromString = SolrUtil.formatDate(from);
        String toString = SolrUtil.formatDate(to);
        solrQuery.addFilterQuery("time:[" + fromString + "+1DAYS/DAY-9HOURS TO " + toString + "+1DAYS/DAY-9HOURS]");
        solrQuery.addFilterQuery("alias:" + collection);
        solrQuery.setFacet(true);
        solrQuery.setParam("facet.limit", "5");

        // 日別検索数
        solrQuery.setParam("facet.range", "{!tag=r1}time");
        solrQuery.setParam("facet.range.start", fromString + "+1DAYS/DAY-9HOURS");
        solrQuery.setParam("facet.range.end", toString + "+1DAYS/DAY-9HOURS");
        solrQuery.setParam("facet.range.gap", "+1DAY");

        // 検索キーワードランキング
        solrQuery.setParam("facet.field", "q");
        solrQuery.setParam("f.q.facet.mincount", "1");

        // トレンド
        solrQuery.setParam("facet.pivot", "{!range=r1}q");

        // 0件ヒット一覧
        SolrQuery zeroQuery = new SolrQuery();
        zeroQuery.setQuery("*:*");
        zeroQuery.setRows(0);
        zeroQuery.addFilterQuery("time:[" + fromString + "+1DAYS/DAY-9HOURS TO " + toString + "+1DAYS/DAY-9HOURS]");
        zeroQuery.addFilterQuery("alias:" + collection);
        zeroQuery.addFilterQuery("hits:0");
        zeroQuery.addFilterQuery("NOT cleared:true");
        zeroQuery.setFacet(true);
        zeroQuery.setParam("facet.field", "q");
        zeroQuery.setParam("facet.mincount", "1");
        zeroQuery.setParam("facet.limit", "5");
        QueryResponse response = solrClient.query(COLLECTION, solrQuery);
        NamedList<Object> res = response.getResponse();
        Map<String, Object> facetCounts = SolrUtil.extractResults(res, "facet_counts");
        {//histogram
            boolean show = false;
            Map<String, Object> facetRanges = (Map<String, Object>) facetCounts.get("facet_ranges");
            Map<String, Object> time = (Map<String, Object>) facetRanges.get("time");
            Map<String, Object> counts = (Map<String, Object>) time.get("counts");
            SimpleDateFormat sdf = new SimpleDateFormat("M/d");
            for (Map.Entry<String, Object> entry : counts.entrySet()) {
                try {
                    Date date = SolrUtil.parseDate(entry.getKey());
                    String dateStr = sdf.format(date);
                    Integer count = (Integer) entry.getValue();
                    if (count > 0) {
                        show = true;
                    }
                    histogram.put(dateStr, count);
                } catch (ParseException e) {
                    //何もしない
                }
            }
            if (show) {
                ret.put("histogram", histogram);
            }
        }
        {//ranking
            Map<String, Object> facetFields = (Map<String, Object>) facetCounts.get("facet_fields");
            Map<String, Object> q = (Map<String, Object>) facetFields.get("q");
            for (Map.Entry<String, Object> entry : q.entrySet()) {
                ranking.put(entry.getKey(), (Integer) entry.getValue());
            }
            ret.put("ranking", ranking);
        }
        {//trend
            Map<String, Object> facetPivot = (Map<String, Object>) facetCounts.get("facet_pivot");
            List<Map<String, Object>> pivots = (List<Map<String, Object>>) facetPivot.get("q");
            for (Map<String, Object> pivot : pivots) {
                String keyword = (String) pivot.get("value");
                Map<String, Object> ranges = (Map<String, Object>) pivot.get("ranges");
                Map<String, Object> time = (Map<String, Object>) ranges.get("time");
                Map<String, Object> counts = (Map<String, Object>) time.get("counts");
                SimpleDateFormat sdf = new SimpleDateFormat("M/d");
                for (Map.Entry<String, Object> entry : counts.entrySet()) {
                    try {
                        Date date = SolrUtil.parseDate(entry.getKey());
                        String dateStr = sdf.format(date);
                        if (!trend.containsKey(dateStr)) {
                            trend.put(dateStr, new LinkedHashMap<>());
                        }
                        trend.get(dateStr).put(keyword, (Integer) entry.getValue());
                    } catch (ParseException e) {
                        // 何もしない
                    }
                }
            }
            ret.put("trend", trend);
        }
        {//zero
            QueryResponse zeroResponse = solrClient.query(COLLECTION, zeroQuery);
            NamedList<Object> zeroRes = zeroResponse.getResponse();
            Map<String, Object> zeroFacetCounts = SolrUtil.extractResults(zeroRes, "facet_counts");
            Map<String, Object> facetFields = (Map<String, Object>) zeroFacetCounts.get("facet_fields");
            Map<String, Object> q = (Map<String, Object>) facetFields.get("q");
            for (Map.Entry<String, Object> entry : q.entrySet()) {
                zero.put(entry.getKey(), (Integer) entry.getValue());
            }
            ret.put("zero", zero);
        }
        return ret;
    }

    /**
     * 0件ヒットエントリーを削除する
     * @param newWord 対象エントリー
     */
    public void clearZeroHit(String newWord) throws IOException, SolrServerException {
        SolrQuery zeroNumQuery = new SolrQuery();
        zeroNumQuery.setQuery("q:" + ClientUtils.escapeQueryChars(newWord));
        zeroNumQuery.setRows(0);
        zeroNumQuery.addFilterQuery("hits:0");
        zeroNumQuery.addFilterQuery("NOT cleared:true");
        int numFound = (int) solrClient.query(COLLECTION, zeroNumQuery).getResults().getNumFound();

        SolrQuery zeroQuery = new SolrQuery();
        zeroQuery.setQuery("q:" + ClientUtils.escapeQueryChars(newWord));
        zeroQuery.setRows(numFound);
        zeroQuery.setFields("itemid");
        zeroQuery.addFilterQuery("hits:0");
        zeroQuery.addFilterQuery("NOT cleared:true");
        QueryResponse zeroResponse = solrClient.query(COLLECTION, zeroQuery);
        SolrDocumentList results = zeroResponse.getResults();

        Map<String,Object> fieldModifier = new HashMap<>(1);
        fieldModifier.put("set","true");
        for (int i = 0; i < numFound; i++) {
            SolrDocument doc = results.get(i);
            SolrInputDocument input = new SolrInputDocument();
            input.addField("itemid", doc.getFieldValue("itemid"));
            input.addField("cleared", fieldModifier);
            solrClient.add(COLLECTION, input);
        }
        solrClient.commit(COLLECTION);
    }
}
