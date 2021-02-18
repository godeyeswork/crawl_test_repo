package jp.co.xxxyyyzzz.ws;

import jp.co.xxxyyyzzz.ws.search.admin.mapper.LoginAdminMapper;
import jp.co.xxxyyyzzz.ws.search.bean.Messages;
import jp.co.xxxyyyzzz.ws.security.AdminAuthenticationProvider;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.filter.ShallowEtagHeaderFilter;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.extras.springsecurity5.dialect.SpringSecurityDialect;

import java.util.Collections;

@SuppressWarnings({"WeakerAccess"})
@SpringBootApplication
@EnableConfigurationProperties
@MapperScan(basePackages = {"jp.co.xxxyyyzzz.ws.search.admin.mapper"})
public class Application extends SpringBootServletInitializer {

    @Autowired
    private LoginAdminMapper mapper;
    @Autowired
    private MessageSource messageSource;

    public Application() {

    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }

    @SuppressWarnings("unused")
    @Bean
    public DefaultCookieSerializer defaultCookieSerializer() {
        DefaultCookieSerializer defaultCookieSerializer = new DefaultCookieSerializer();
        defaultCookieSerializer.setCookieName("jsessionid");
        return defaultCookieSerializer;
    }

    @SuppressWarnings("unused")
    @Bean
    public TemplateEngine templateEngine() {
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.addDialect(new SpringSecurityDialect());
        return templateEngine;
    }

    @SuppressWarnings("unused")
    @Bean
    AuthenticationProvider authenticationProvider() {
        return new AdminAuthenticationProvider(mapper, passwordEncoder(), messages());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public Messages messages() {
        return new Messages(messageSource);
    }

    @SuppressWarnings("unused")
    @Bean
    public FilterRegistrationBean<GenericFilterBean> filterRegistrationBean() {
        FilterRegistrationBean<GenericFilterBean> filterBean = new FilterRegistrationBean<>();
        filterBean.setFilter(new ShallowEtagHeaderFilter());
        filterBean.setUrlPatterns(Collections.singletonList("*"));
        return filterBean;
    }

}
