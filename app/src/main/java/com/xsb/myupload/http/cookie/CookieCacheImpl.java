package com.xsb.myupload.http.cookie;

import android.support.annotation.NonNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import okhttp3.Cookie;

public class CookieCacheImpl implements CookieCache {
    private Set<IdentifiableCookie> cookies;

    public CookieCacheImpl() {
        cookies = new HashSet<>();
    }


   /*这个方法貌似没有用 */
    @Override
    public void addAll(Collection<Cookie> cookies) {
        for (IdentifiableCookie cookie : IdentifiableCookie.decorateAll(cookies)) {
            this.cookies.remove(cookie);
            this.cookies.add(cookie);
        }
    }

    @Override
    public void clear() {
        cookies.clear();
    }

    @NonNull
    @Override
    public Iterator<Cookie> iterator() {
        return new SetCookieCacheIterator();
    }

    private class SetCookieCacheIterator implements Iterator<Cookie> {

        private Iterator<IdentifiableCookie> iterator;

        public SetCookieCacheIterator() {
            iterator = cookies.iterator();
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Cookie next() {
            return iterator.next().getCookie();
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }
}
