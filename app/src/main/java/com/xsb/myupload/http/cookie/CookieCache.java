package com.xsb.myupload.http.cookie;

import java.util.Collection;

import okhttp3.Cookie;

public interface CookieCache extends Iterable<Cookie> {

    /**
     *添加所有的cookie到会话中 现有的cookie会被覆盖
     *
     * @param cookies
     */
    void addAll(Collection<Cookie> cookies);

    /**
     * Clear all the cookies from the session.
     */
    void clear();
}
