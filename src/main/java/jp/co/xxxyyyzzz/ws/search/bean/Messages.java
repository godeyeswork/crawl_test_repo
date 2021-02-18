package jp.co.xxxyyyzzz.ws.search.bean;

import org.springframework.context.MessageSource;

public class Messages {
    private final MessageSource messageSource;

    public Messages(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String getMessage(String key) {
        return getMessage(key, new Object[]{});
    }

    public String getMessage(String key, Object[] objects) {
        return messageSource.getMessage(key, objects, null);
    }
}
