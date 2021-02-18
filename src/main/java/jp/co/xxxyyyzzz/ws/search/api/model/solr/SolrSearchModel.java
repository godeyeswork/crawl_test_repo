package jp.co.xxxyyyzzz.ws.search.api.model.solr;

import org.springframework.data.domain.Sort;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class SolrSearchModel {

    private Map<String, String> fieldAlias = new HashMap<>();

    private Map<String, String> sort = new HashMap<>();

    private Map<String, Object> params = new HashMap<>();

    private Map<String, String> facetReturnFields = new HashMap<>();

    private Map<String, String> facetReturnLabels = new HashMap<>();

    public Map<String, String> getFieldAlias() {
        return fieldAlias;
    }

    public void setFieldAlias(Map<String, String> fieldAlias) {
        this.fieldAlias = fieldAlias;
    }

    public Map<String, String> getSort() {
        return sort;
    }
    public Sort getSortOrder() {
        String direction = sort.get("direction");
        String property = sort.get("property");
        if ("desc".equals(direction)) {
            return Sort.by(Sort.Direction.DESC, property);
        } else {
            return Sort.by(Sort.Direction.ASC, property);
        }
    }

    public void setSort(Map<String, String> sort) {
        this.sort = sort;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public Map<String, String> getFacetReturnFields() {
        return facetReturnFields;
    }

    public void setFacetReturnFields(Map<String, String> facetReturnFields) {
        this.facetReturnFields = facetReturnFields;
    }

    public Map<String, String> getFacetReturnLabels() {
        return facetReturnLabels;
    }

    public void setFacetReturnLabels(Map<String, String> labels) {
        this.facetReturnLabels = labels;
    }

}
