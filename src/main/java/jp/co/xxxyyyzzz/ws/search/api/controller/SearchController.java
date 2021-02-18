package jp.co.xxxyyyzzz.ws.search.api.controller;

import jp.co.xxxyyyzzz.ws.search.api.model.request.AutocompleteModel;
import jp.co.xxxyyyzzz.ws.search.api.model.request.SearchModel;
import jp.co.xxxyyyzzz.ws.search.api.service.SearchService;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringArrayPropertyEditor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"unused"})
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class SearchController {

    private final SearchService service;

    @Autowired
    public SearchController(SearchService service) {
        this.service = service;
    }

    @RequestMapping("/suggest")
    @CrossOrigin(origins = "*")
    public List<String> suggest(
        @ModelAttribute AutocompleteModel model, HttpServletRequest request
    ) throws IOException, SolrServerException {
        return service.autocomplete(model, (String) request.getAttribute("collection"));
    }

    @RequestMapping("/search")
    @CrossOrigin(origins = "*")
    public Map<String, Object> search(
        @ModelAttribute SearchModel model, HttpServletRequest request,
        @PageableDefault(size = 30) Pageable pageable
    ) throws IOException, SolrServerException {
        return service.search(model, (String) request.getAttribute("collection"), pageable, request);
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String[].class, new StringArrayPropertyEditor(null));
    }
}
