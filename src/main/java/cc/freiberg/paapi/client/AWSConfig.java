/*
 * Copyright (c) 2018 Ben Freiberg <freiberg.ben@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package cc.freiberg.paapi.client;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AWSConfig {

    private static final Logger LOG = LoggerFactory.getLogger(AWSConfig.class);

    private static final String AMAZON_BUNDLE = "amazon";

    private static ResourceBundle resource_bundle;

    private AWSConfig() {
    }

    public static String getMarketplace() {
        return getValue("amazon.paapi.marketplace");
    }

    public static String getAccesKey() {
        return getValue("amazon.accessKeyId");
    }

    public static String getSecretKey() {
        return getValue("amazon.secretKey");
    }

    public static String getTag() {
        return getValue("amazon.paapi.tag");
    }

    private static String getValue(final String key) {
        // Check environment variables first
        try {
            if (System.getenv(key.substring(key.lastIndexOf(".") + 1)) != null) {
                return System.getenv(key);
            }
        } catch (final IndexOutOfBoundsException e) {
            LOG.warn("Substring index out of bounds for {}", key);
        }
        try {
            if (resource_bundle == null) {
                resource_bundle = ResourceBundle.getBundle(AMAZON_BUNDLE);
            }
            return resource_bundle.getString(key);
        } catch (final MissingResourceException e) {
            LOG.error("Could not find property {}", key, e);
            return null;
        }
    }
}
