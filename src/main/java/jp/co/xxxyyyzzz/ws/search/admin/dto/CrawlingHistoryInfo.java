package jp.co.xxxyyyzzz.ws.search.admin.dto;

import java.util.Date;

@SuppressWarnings("unused")
public class CrawlingHistoryInfo {
    private Date starttime;
    private Date endtime;
    private long success;
    private long failure;

    public Date getStarttime() {
        return starttime;
    }

    public void setStarttime(Date starttime) {
        this.starttime = starttime;
    }

    public Date getEndtime() {
        return endtime;
    }

    public void setEndtime(Date endtime) {
        this.endtime = endtime;
    }

    public long getSuccess() {
        return success;
    }

    public void setSuccess(long success) {
        this.success = success;
    }

    public long getFailure() {
        return failure;
    }

    public void setFailure(long failure) {
        this.failure = failure;
    }
}
