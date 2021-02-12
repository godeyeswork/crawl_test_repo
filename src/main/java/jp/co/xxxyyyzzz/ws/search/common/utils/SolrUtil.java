package jp.co.xxxyyyzzz.ws.search.common.utils;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.common.util.SimpleOrderedMap;
import org.springframework.data.solr.core.DefaultQueryParser;
import org.springframework.data.solr.core.mapping.SimpleSolrMappingContext;
import org.springframework.data.solr.core.query.Query;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class SolrUtil {
    /**
     * 日付フォーマット
     */
    private static final DateFormat SOLR_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    static {
        SOLR_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    /**
     * 日付オブジェクトをSolrデフォルトフォーマットの文字列に変換
     * @param date 日付
     * @return 文字列
     */
    public static String formatDate(Date date) {
        return SOLR_DATE_FORMAT.format(date);
    }

    /**
     * 文字列をSolrデフォルトフォーマットとして日付オブジェクトに変換
     * @param date 文字列
     * @return 日付
     * @throws ParseException cannot be parsed
     */
    public static Date parseDate(String date) throws ParseException {
        return SOLR_DATE_FORMAT.parse(date);
    }

    /**
     * レスポンスのリストから指定されたキー配下をMapとして取得する
     * @param response Solrレスポンス（raw）
     * @param key キー
     * @return Map
     */
    public static Map<String, Object> extractResults(NamedList response, String key) {
        Object val = response.get(key);
        SimpleOrderedMap map = (SimpleOrderedMap) val;
        return namedList2Map(map);
    }

    /**
     * レスポンスのリストから指定されたキー配下をListとして取得する
     * @param response Solrレスポンス（raw）
     * @param key キー
     * @return List
     */
    @SuppressWarnings("unchecked")
    public static List<Map<String, Object>> extractResultsList(NamedList response, String key) {
        Object val = response.get(key);
        List<NamedList> list = (List) val;
        List<Map<String, Object>> ret = new LinkedList<>();
        for (NamedList namedList : list) {
            ret.add(namedList2Map(namedList));
        }
        return ret;
    }

    /**
     * 指定されたNamedListをMapに変換する（再帰）
     * @param list NamedList
     * @return Map
     */
    private static Map<String, Object> namedList2Map(NamedList list) {
        Map<String, Object> ret = new LinkedHashMap<>();
        int size = list.size();
        for (int i = 0; i < size; i++) {
            Object val = list.getVal(i);
            if (val instanceof NamedList) {
                val = namedList2Map((NamedList) val);
            } else if (val instanceof List) {
                LinkedList<Object> objects = new LinkedList<>();
                for (Object o : ((List) val)) {
                    objects.add(namedList2Map((NamedList) o));
                }
                val = objects;
            }
            ret.put(list.getName(i), val);
        }
        return ret;
    }

    /**
     * spring-data-solrのQueryをsolrjのQueryに変換する
     *
     * @param query SolrDataQuery
     * @return SolrQuery
     */
    public static SolrQuery solrQuery(Query query) {
        DefaultQueryParser defaultQueryParser = new DefaultQueryParser(new SimpleSolrMappingContext());
        return defaultQueryParser.doConstructSolrQuery(query, null);
    }

}
