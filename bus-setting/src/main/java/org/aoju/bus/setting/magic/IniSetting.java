/*********************************************************************************
 *                                                                               *
 * The MIT License (MIT)                                                         *
 *                                                                               *
 * Copyright (c) 2015-2020 aoju.org and other contributors.                      *
 *                                                                               *
 * Permission is hereby granted, free of charge, to any person obtaining a copy  *
 * of this software and associated documentation files (the "Software"), to deal *
 * in the Software without restriction, including without limitation the rights  *
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell     *
 * copies of the Software, and to permit persons to whom the Software is         *
 * furnished to do so, subject to the following conditions:                      *
 *                                                                               *
 * The above copyright notice and this permission notice shall be included in    *
 * all copies or substantial portions of the Software.                           *
 *                                                                               *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR    *
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,      *
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE   *
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER        *
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, *
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN     *
 * THE SOFTWARE.                                                                 *
 ********************************************************************************/
package org.aoju.bus.setting.magic;

import org.aoju.bus.setting.Builder;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Ini数据,扩展{@code ArrayList <IniElement>}
 * 如果要向此ini添加空行，只需添加null
 * 如果您想创建Ini，则可以{@link Builder}
 *
 * @author Kimi Liu
 * @version 6.0.3
 * @since JDK 1.8+
 */
public class IniSetting extends ArrayList<IniElement> {

    public IniSetting() {
    }

    public IniSetting(int initialCapacity) {
        super(initialCapacity);
    }

    public IniSetting(Collection<? extends IniElement> c) {
        super(c);
    }

    @Override
    public String toString() {
        if (this.isEmpty()) {
            // if data empty, return empty.
            return "";
        } else {
            // Imitation of super toString method.
            // Imitation ? maybe not ?
            String newLineSplit = getNewLineSplit();
            // use joiner for every line
            StringJoiner joiner = new StringJoiner(newLineSplit);
            for (IniElement iniElement : this) {
                // if null, show a empty line.
                joiner.add(iniElement == null ? "" : iniElement.toString());
            }
            return joiner.toString();
        }
    }

    private String getNewLineSplit() {
        return System.getProperty("line.separator", "\n");
    }

    /**
     * <p> get properties.</p>
     * <p>for example: </p>
     * <p>
     * <code>
     * [se1] # section named 'se1'
     * # key 1
     * key1=value1
     * # key 2
     * key1=value2
     * [se2]
     * </code>
     * </p>
     * <p>will be</p>
     * <p>
     * <code>
     * se1${delimiter}value1=value1
     * se1${delimiter}value2=value2
     * </code>
     * </p>
     * <p>Suppose delimiter is'.'</p>
     * <p>
     * <code>
     * se1.value1=value1
     * se1.value2=value2
     * </code>
     * </p>
     *
     * @param delimiter Connect the property value to the section value. if null, ignore section.
     * @return properties
     */
    public Properties toProperties(String delimiter) {
        final Properties prop = new Properties();
        final Iterator<IniElement> iter = iterator();
        IniElement next;
        while (iter.hasNext()) {
            next = iter.next();
            if (next.isProperty()) {
                String pk;
                IniProperty inip = (IniProperty) next;
                if (delimiter != null) {
                    pk = inip.getSection().value() + delimiter + inip.key();
                } else {
                    pk = inip.key();
                }
                prop.setProperty(pk, next.value());
            }
        }
        return prop;
    }

    /**
     * to properties. delimiter is '.'
     *
     * @return properties
     * @see #toProperties(String)
     */
    public Properties toProperties() {
        return toProperties(".");
    }

    /**
     * write the {@link #toString()} value to output stream.
     *
     * @param out         output stream.
     * @param charset     param for {@link String#getBytes(Charset)}
     * @param withComment write with comment
     * @throws IOException io exception from {@link OutputStream#write(byte[])}
     */
    public void write(OutputStream out, Charset charset, boolean withComment) throws IOException {
        String str;
        for (IniElement element : this) {
            if (!withComment && element.isComment()) {
                continue;
            }
            str = element == null ? getNewLineSplit() :
                    withComment ? element.toString() + getNewLineSplit() : element.toNoCommentString() + getNewLineSplit();
            out.write(str.getBytes(charset));
        }
        out.flush();
    }

    /**
     * write the {@link #toString()} value to output stream.
     * charset is utf-8
     *
     * @param out         output stream.
     * @param withComment write with comment
     * @throws IOException io exception from {@link OutputStream#write(byte[])}
     * @see #write(OutputStream, Charset, boolean)
     */
    public void write(OutputStream out, boolean withComment) throws IOException {
        write(out, StandardCharsets.UTF_8, withComment);
    }


    /**
     * write the {@link #toString()} value to Writer.
     *
     * @param writer      Writer
     * @param withComment write with comment
     * @throws IOException io exception from {@link Writer#write(String)}
     */
    public void write(Writer writer, boolean withComment) throws IOException {
        String str;
        for (IniElement element : this) {
            if (!withComment && element.isComment()) {
                continue;
            }
            str = element == null ? getNewLineSplit() :
                    withComment ? element.toString() + getNewLineSplit() : element.toNoCommentString() + getNewLineSplit();
            writer.write(str);
        }
        writer.flush();
    }

    /**
     * write the {@link #toString()} value to PrintStream.
     *
     * @param print       PrintStream
     * @param withComment write with comment
     */
    public void write(PrintStream print, boolean withComment) {
        String str;
        for (IniElement element : this) {
            if (!withComment && element.isComment()) {
                continue;
            }
            str = element == null ? "" : withComment ? element.toString() : element.toNoCommentString();
            print.println(str);
        }
        print.flush();
    }

    /**
     * write the {@link #toString()} value to File.
     *
     * @param file        file
     * @param charset     charset
     * @param withComment write with comment
     * @throws IOException io exception
     */
    public void write(File file, Charset charset, boolean withComment) throws IOException {
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
        try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file))) {
            write(out, charset, withComment);
        }
    }

    /**
     * write the {@link #toString()} value to File.
     * charset is utf-8
     *
     * @param file        file
     * @param withComment write with comment
     * @throws IOException io exception
     */
    public void write(File file, boolean withComment) throws IOException {
        write(file, StandardCharsets.UTF_8, withComment);
    }

    /**
     * write the {@link #toString()} value to Path(file).
     *
     * @param path        path
     * @param charset     charset
     * @param withComment write with comment
     * @throws IOException io exception
     */
    public void write(Path path, Charset charset, boolean withComment) throws IOException {
        if (!Files.exists(path)) {
            Files.createDirectories(path.getParent());
            Files.createFile(path);
        }
        try (BufferedOutputStream out = new BufferedOutputStream(Files.newOutputStream(path))) {
            write(out, charset, withComment);
        }
    }

    /**
     * write the {@link #toString()} value to Path(file).
     * charset is utf-8
     *
     * @param path        path
     * @param withComment write with comment
     * @throws IOException io exception
     */
    public void write(Path path, boolean withComment) throws IOException {
        write(path, StandardCharsets.UTF_8, withComment);
    }

}
