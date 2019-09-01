/*
 * The MIT License
 *
 * Copyright (c) 2017, aoju.org All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.aoju.bus.pager.cache;

import org.aoju.bus.pager.plugin.PageFromObject;
import org.apache.ibatis.cache.decorators.FifoCache;
import org.apache.ibatis.cache.impl.PerpetualCache;
import org.apache.ibatis.mapping.CacheBuilder;

import java.util.Properties;

/**
 * Simple MyBatis Cache
 *
 * @author Kimi Liu
 * @version 3.1.8
 * @since JDK 1.8
 */
public class SimpleCache<K, V> implements Cache<K, V> {

    private final org.apache.ibatis.cache.Cache CACHE;

    public SimpleCache(Properties properties, String prefix) {
        CacheBuilder cacheBuilder = new CacheBuilder("SQL_CACHE");
        String typeClass = properties.getProperty(prefix + ".typeClass");
        if (PageFromObject.isNotEmpty(typeClass)) {
            try {
                cacheBuilder.implementation((Class<? extends org.apache.ibatis.cache.Cache>) Class.forName(typeClass));
            } catch (ClassNotFoundException e) {
                cacheBuilder.implementation(PerpetualCache.class);
            }
        } else {
            cacheBuilder.implementation(PerpetualCache.class);
        }
        String evictionClass = properties.getProperty(prefix + ".evictionClass");
        if (PageFromObject.isNotEmpty(evictionClass)) {
            try {
                cacheBuilder.addDecorator((Class<? extends org.apache.ibatis.cache.Cache>) Class.forName(evictionClass));
            } catch (ClassNotFoundException e) {
                cacheBuilder.addDecorator(FifoCache.class);
            }
        } else {
            cacheBuilder.addDecorator(FifoCache.class);
        }
        String flushInterval = properties.getProperty(prefix + ".flushInterval");
        if (PageFromObject.isNotEmpty(flushInterval)) {
            cacheBuilder.clearInterval(Long.parseLong(flushInterval));
        }
        String size = properties.getProperty(prefix + ".size");
        if (PageFromObject.isNotEmpty(size)) {
            cacheBuilder.size(Integer.parseInt(size));
        }
        cacheBuilder.properties(properties);
        CACHE = cacheBuilder.build();
    }

    @Override
    public V get(K key) {
        Object value = CACHE.getObject(key);
        if (value != null) {
            return (V) value;
        }
        return null;
    }

    @Override
    public void put(K key, V value) {
        CACHE.putObject(key, value);
    }
}
