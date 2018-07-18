package com.xsb.myupload.http.cookie;

import android.util.Log;

import com.xsb.myupload.http.cookie.ClearableCookieJar;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

public class NovateCookieManager implements ClearableCookieJar {

    private CookieCache cache;
    private CookiePersistor persistor;

    public NovateCookieManager(CookieCache cache, CookiePersistor persistor) {
        this.cache=cache;
        this.persistor=persistor;
        /*SP 中的cookie 存储到 cache中？？？*/
        this.cache.addAll(persistor.loadAll());
    }

    @Override
    public void clearSession() {
        cache.clear();
        cache.addAll(persistor.loadAll());
    }

    @Override
    public void clear() {
        cache.clear();
        persistor.clear();
    }
    /*貌似只有返回的header中包含cookie 才会执行这个回掉*/
    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        cache.addAll(cookies);
        persistor.saveAll(filterPersistentCookies(cookies));
    }
    /*返回每次请求携带的cookie*/
    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {

        List<Cookie> cookiesToRemove = new ArrayList<>();
        List<Cookie> validCookies = new ArrayList<>();

        for (Iterator<Cookie> it = cache.iterator(); it.hasNext(); ) {
            Cookie currentCookie = it.next();

            if (isCookieExpired(currentCookie)) {
                cookiesToRemove.add(currentCookie);
                it.remove();

            } else if (currentCookie.matches(url)) {
                validCookies.add(currentCookie);
            }
        }
        persistor.removeAll(cookiesToRemove);
        return validCookies;
    }

    private static List<Cookie> filterPersistentCookies(List<Cookie> cookies) {

        List<Cookie> persistentCookies = new ArrayList<>();

        for (Cookie cookie : cookies) {
            /*persistent  判断cookie 在当前会话中是否失效*/
            if (cookie.persistent()) {
                persistentCookies.add(cookie);
            }
        }
        return persistentCookies;
    }

    private static boolean isCookieExpired(Cookie cookie) {
        /*expiresAt 返回cookie的过期时间*/
        return cookie.expiresAt() < System.currentTimeMillis();
    }
}
