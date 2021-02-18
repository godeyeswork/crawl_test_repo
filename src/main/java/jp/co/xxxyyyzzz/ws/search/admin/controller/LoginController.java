package jp.co.xxxyyyzzz.ws.search.admin.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@SuppressWarnings({"SameReturnValue", "unused"})
@Controller
public class LoginController {

    @SuppressWarnings("unused")
    private final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @RequestMapping(path = {"/admin/login"}, method = RequestMethod.GET)
    public String login(HttpServletRequest request) {
        return "admin/login";
    }

}
