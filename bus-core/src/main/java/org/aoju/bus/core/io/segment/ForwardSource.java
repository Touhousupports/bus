/*
 * The MIT License
 *
 * Copyright (c) 2017 aoju.org All rights reserved.
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
package org.aoju.bus.core.io.segment;

import java.io.IOException;

/**
 * 将调用转发给另一个调用的{@link Source}
 *
 * @author Kimi Liu
 * @version 5.0.2
 * @since JDK 1.8+
 */
public abstract class ForwardSource implements Source {

    private final Source delegate;

    public ForwardSource(Source delegate) {
        if (delegate == null) throw new IllegalArgumentException("delegate == null");
        this.delegate = delegate;
    }

    public final Source delegate() {
        return delegate;
    }

    @Override
    public long read(Buffer sink, long byteCount) throws IOException {
        return delegate.read(sink, byteCount);
    }

    @Override
    public Timeout timeout() {
        return delegate.timeout();
    }

    @Override
    public void close() throws IOException {
        delegate.close();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + delegate.toString() + ")";
    }

}