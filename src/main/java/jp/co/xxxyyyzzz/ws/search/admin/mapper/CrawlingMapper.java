package jp.co.xxxyyyzzz.ws.search.admin.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface CrawlingMapper {
    long selectHistoryCount(@Param("owner") String collection);
    List<Map<String, Object>> selectHistory(
        @Param("owner") String collection, @Param("page") int page, @Param("size") int size
    );
}
