package com.xsb.myupload.http.cookie;

import okhttp3.CookieJar;
/*okhttp3 管理cookie的类*/
public interface ClearableCookieJar extends CookieJar {
    /**
     * Clear all the session cookies while maintaining the persisted ones.
     */
    void clearSession();

    /**
     * Clear all the cookies from persistence and from the cache.
     */
    void clear();
}
