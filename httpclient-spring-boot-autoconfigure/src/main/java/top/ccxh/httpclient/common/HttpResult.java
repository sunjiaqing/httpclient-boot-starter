package top.ccxh.httpclient.common;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * http统一返回结果集
 *
 * @author ccxh
 */
public class HttpResult implements Closeable {
    private final static Logger log = LoggerFactory.getLogger(HttpResult.class);
    private Integer httpStatus=0;
    private CloseableHttpResponse response;
    private String entityStr;
    private Exception error;

    public HttpResult() {
    }

    public HttpResult(Exception error) {
        this.error = error;
    }

    public HttpResult(CloseableHttpResponse response) {
        this.response = response;
        if (this.response != null) {
            this.httpStatus = response.getStatusLine().getStatusCode();
        }
    }

    public Integer getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(Integer httpStatus) {
        this.httpStatus = httpStatus;
    }

    public CloseableHttpResponse getResponse() {
        return response;
    }


    /**
     * 输出字符串 形式
     *
     * @param encode 字符编码
     * @return String
     */
    public String getEntityStr(String encode) {
        try {
            if (httpStatus.equals(HttpStatus.SC_OK) && StringUtils.isEmpty(this.entityStr)) {
                this.entityStr = EntityUtils.toString(this.response.getEntity(), encode);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        consume();
        return this.entityStr;
    }

    public String getEntityStr() {
        return this.getEntityStr("UTF-8");
    }

    private void consumeHttpEntity(HttpEntity httpEntity) {
        if (httpEntity != null) {
            try {
                EntityUtils.consume(httpEntity);
            } catch (IOException e) {
            } finally {
                httpEntity = null;
            }
        }
    }

    public void consume() {
        if (response != null) {
            consumeHttpEntity(response.getEntity());
            try {
                response.close();
            } catch (IOException e) {
            } finally {
                response = null;
            }
        }
    }

    @Override
    public void close() throws IOException {
        this.consume();
    }

    public static Logger getLog() {
        return log;
    }
}
