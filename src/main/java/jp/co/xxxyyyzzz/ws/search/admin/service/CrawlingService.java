package jp.co.xxxyyyzzz.ws.search.admin.service;

import jp.co.xxxyyyzzz.ws.search.admin.dto.CrawlingHistoryInfo;
import jp.co.xxxyyyzzz.ws.search.admin.mapper.CrawlingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CrawlingService {
    private final CrawlingMapper mapper;

    @SuppressWarnings("unused")
    @Autowired
    public CrawlingService(CrawlingMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * クローリング履歴取得
     * @param collection コレクション名
     * @param pageable ページャー
     * @return 変更可否
     */
    public Page<CrawlingHistoryInfo> getHistory(String collection, Pageable pageable) {
        List<CrawlingHistoryInfo> list = new ArrayList<>();
        long count = mapper.selectHistoryCount(collection);
        if (count > 0) {
            List<Map<String, Object>> history = mapper.selectHistory(collection, pageable.getPageNumber(), pageable.getPageSize());
            for (Map<String, Object> map : history) {
                CrawlingHistoryInfo crawlingHistoryInfo = new CrawlingHistoryInfo();
                crawlingHistoryInfo.setStarttime(new Date((Long) map.get("starttime")));
                crawlingHistoryInfo.setEndtime(new Date((Long) map.get("endtime")));
                crawlingHistoryInfo.setSuccess(1L);
                crawlingHistoryInfo.setFailure(0L);
                list.add(crawlingHistoryInfo);
            }
        }
        return new PageImpl<>(list, pageable, count);
    }

}
