package alex.com.myplaces.data.network.utils;


import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for building a Uri path
 */
public class UriBuilder {

    public static final String URI_CHARSET = "UTF-8";

    private StringBuilder stringBuilder;

    private Map<String, Object> queryParameters;

    private String queryString;

    public UriBuilder() {
        this(null);
    }

    public UriBuilder(String basePath) {
        this.stringBuilder = new StringBuilder();
        if (StringUtils.isNotEmpty(basePath)) {
            parse(basePath);
        }
    }


    public UriBuilder parse(String uri) {
        this.queryParameters = new HashMap<>();
        uri = StringUtils.trimToEmpty(uri);
        uri = cleanTrailingSlash(uri);
        this.stringBuilder.append(uri);
        return this;
    }

    public UriBuilder appendPath(String path) {
        String[] paths = path.split("/");
        for (String segment : paths) {
            appendSinglePath(segment);
        }
        return this;
    }

    private UriBuilder appendSinglePath(String path) {
        path = cleanTrailingSlash(path);
        try {
            if (!StringUtils.startsWith(path, "/")) {
                this.stringBuilder.append("/");
            }
            this.stringBuilder.append(URLEncoder.encode(path, URI_CHARSET));
        } catch (UnsupportedEncodingException e) {
            IllegalStateException illegalStateException = new IllegalStateException("Unsupported charset.", e);
            throw illegalStateException;
        }
        return this;
    }

    public UriBuilder appendQueryParameter(String key, String value) {
        try {
            if (this.queryParameters != null) {
                this.queryParameters.put(URLEncoder.encode(key, URI_CHARSET), URLEncoder.encode(value, URI_CHARSET));
            }
        } catch (UnsupportedEncodingException e) {
            IllegalStateException illegalStateException = new IllegalStateException("Unsupported charset.", e);
            throw illegalStateException;
        }
        return this;
    }

    public UriBuilder appendQueryListParameter(String key, List<String> valuesList) {
        try {
            List<String> list = new ArrayList<>();
            if (this.queryParameters != null) {
                for (int i = 0; i < valuesList.size(); i++) {
                    list.add(URLEncoder.encode(valuesList.get(i), URI_CHARSET));
                }
            }
            this.queryParameters.put(URLEncoder.encode(key, URI_CHARSET), list);
        } catch (UnsupportedEncodingException e) {
            IllegalStateException illegalStateException = new IllegalStateException("Unsupported charset.", e);
            throw illegalStateException;
        }
        return this;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    public String build() {
        if (this.stringBuilder != null) {
            if (this.queryParameters != null && !this.queryParameters.isEmpty()) {
                this.stringBuilder.append("?");
                boolean isFirstQueryParameter = true;
                for (Map.Entry<String, Object> queryEntry : queryParameters.entrySet()) {
                    if (queryEntry.getValue() instanceof String) {
                        if (!isFirstQueryParameter) {
                            this.stringBuilder.append("&");
                        }
                        isFirstQueryParameter = false;
                        this.stringBuilder.append(queryEntry.getKey()).append("=").append(queryEntry.getValue());
                    } else if (queryEntry.getValue() instanceof List) {
                        List<String> list = (List<String>) queryEntry.getValue();
                        for (String string : list) {
                            if (!isFirstQueryParameter) {
                                this.stringBuilder.append("&");
                            }
                            isFirstQueryParameter = false;
                            this.stringBuilder.append(queryEntry.getKey()).append("=").append(string);
                        }
                    }
                }
                if (queryString != null) {
                    if (!isFirstQueryParameter) {
                        this.stringBuilder.append("&");
                    }
                    this.stringBuilder.append(queryString);
                }
            }
            return this.stringBuilder.toString();
        }
        return null;
    }

    private static String cleanTrailingSlash(String uri) {
        while (StringUtils.endsWith(uri, "/")) {
            uri = StringUtils.substring(uri, 0, uri.length() - 1);
        }
        return uri;
    }
}
