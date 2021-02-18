package jp.co.xxxyyyzzz.ws.controller;

import org.apache.solr.client.solrj.SolrClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

@SuppressWarnings({"unused"})
@Controller
@RequestMapping("/maint")
public class MaintController {
    private final Logger logger = LoggerFactory.getLogger(MaintController.class);

    private final DataSource dataSource;

    @SuppressWarnings("FieldCanBeLocal")
    private final SolrClient solrClient;

    @Autowired
    public MaintController(DataSource dataSource, SolrClient solrClient) {
        this.dataSource = dataSource;
        this.solrClient = solrClient;
    }

    @RequestMapping("/check")
    @ResponseBody
    public ResponseEntity<String> check() {
        String message = "OK";
        boolean ret = true;
        Exception ex = null;
        ResponseEntity<String> res;

        {
            Connection connection = null;
            try {
                connection = dataSource.getConnection();
                if (connection == null || connection.isClosed()) {
                    message = "connection invalid";
                    ret = false;
                }
            } catch (Exception e) {
                message = e.getMessage();
                ex = e;
                ret = false;
            } finally {
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        message = e.getMessage();
                        ex = e;
                        ret = false;
                    }
                }
            }
        }
/*
        {
            try {
                SolrPingResponse ping = solrClient.ping();
                if (ping.getStatus() != 0) {
                    message = "solr ping failed";
                    ret = false;
                }
            } catch (SolrServerException | IOException e) {
                message = e.getMessage();
                ex = e;
                ret = false;
            }
        }
*/
        if (!ret) {
            res = new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
            if (ex == null) {
                logger.error(message);
            } else {
                logger.error(message, ex);
            }
        } else {
            res = new ResponseEntity<>(message, HttpStatus.OK);
        }
        return res;
    }

    @RequestMapping(value = "check2", method = {RequestMethod.GET, RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public HashMap<String, String> test(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with");
        HashMap<String, String> map = new HashMap<>();
        map.put("key", "value");
        return map;
    }
}
