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

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.xml.ws.WebServiceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ecs.client.jax.AWSECommerceService;
import com.ecs.client.jax.AWSECommerceServicePortType;
import com.ecs.client.jax.BrowseNodeLookupRequest;
import com.ecs.client.jax.BrowseNodes;
import com.ecs.client.jax.Cart;
import com.ecs.client.jax.CartAddRequest;
import com.ecs.client.jax.CartClearRequest;
import com.ecs.client.jax.CartCreateRequest;
import com.ecs.client.jax.CartGetRequest;
import com.ecs.client.jax.CartModifyRequest;
import com.ecs.client.jax.ItemLookupRequest;
import com.ecs.client.jax.ItemSearchRequest;
import com.ecs.client.jax.Items;
import com.ecs.client.jax.SimilarityLookupRequest;

public class PaapiClientImpl implements PaapiClient {

    private static final Logger LOG = LoggerFactory.getLogger(PaapiClientImpl.class);

    private int retryMax = 3;
    private long retryIntervallMillis = 1000;

    private final PaapiParams params;

    private final ItemRequestHandler itemHandler;
    private final BrowseNodeRequestHandler browseNodeHandler;
    private final CartRequestHandler cartHandler;

    public PaapiClientImpl(final String marketplace, final String accessKeyId, final String secretAccessKey,
            final String tag) {
        checkArgs(marketplace, accessKeyId, secretAccessKey, tag);
        final AWSECommerceService service = new AWSECommerceService();
        service.setHandlerResolver(new AwsHandlerResolver(secretAccessKey));
        final AWSECommerceServicePortType port = service.getAWSECommerceServicePortDE();
        // Create param holder with default values
        params = new PaapiParams(true, false);
        // Create handlers
        itemHandler = new ItemRequestHandler(marketplace, accessKeyId, tag, port);
        browseNodeHandler = new BrowseNodeRequestHandler(marketplace, accessKeyId, tag, port);
        cartHandler = new CartRequestHandler(marketplace, accessKeyId, tag, port);

        LOG.debug("Create PaapiClient for {} - {} - {}", marketplace, accessKeyId, tag);
    }

    private static void checkArgs(final String marketplace, final String accessKeyId, final String secretAccessKeyId,
            final String tag) throws IllegalArgumentException {
        checkNullOrEmpty(marketplace, "marketplace");
        checkNullOrEmpty(accessKeyId, "accessKeyId");
        checkNullOrEmpty(secretAccessKeyId, "secretAccessKeyId");
        checkNullOrEmpty(tag, "tag");
    }

    private static void checkNullOrEmpty(final String string, final String name) throws IllegalArgumentException {
        if (string == null) {
            throw new IllegalArgumentException(name + " is null.");
        }
        if (string.trim().isEmpty()) {
            throw new IllegalArgumentException(name + " is empty.");
        }
    }

    @Override
    public List<Items> itemSearch(final ItemSearchRequest request) {
        return callWithRetry(new WebServiceWrapper<List<Items>>() {
            @Override
            public List<Items> call() throws WebServiceException {
                return itemHandler.itemSearch(request, params);
            }
        });
    }

    @Override
    public List<BrowseNodes> browseNodeLookup(final BrowseNodeLookupRequest request) {
        return callWithRetry(new WebServiceWrapper<List<BrowseNodes>>() {
            @Override
            public List<BrowseNodes> call() throws WebServiceException {
                return browseNodeHandler.browseNodeLookup(request, params);
            }
        });
    }

    @Override
    public List<Items> itemLookup(final ItemLookupRequest request) {
        return callWithRetry(new WebServiceWrapper<List<Items>>() {
            @Override
            public List<Items> call() throws WebServiceException {
                return itemHandler.itemLookup(request, params);
            }
        });
    }

    @Override
    public List<Items> similarityLookup(final SimilarityLookupRequest request) {
        return callWithRetry(new WebServiceWrapper<List<Items>>() {
            @Override
            public List<Items> call() throws WebServiceException {
                return itemHandler.similarityLookup(request, params);
            }
        });
    }

    @Override
    public List<Cart> cartAdd(final CartAddRequest request) {
        return callWithRetry(new WebServiceWrapper<List<Cart>>() {
            @Override
            public List<Cart> call() throws WebServiceException {
                return cartHandler.cartAdd(request, params);
            }
        });

    }

    @Override
    public List<Cart> cartCreate(final CartCreateRequest request) {
        return callWithRetry(new WebServiceWrapper<List<Cart>>() {
            @Override
            public List<Cart> call() throws WebServiceException {
                return cartHandler.cartCreate(request, params);
            }
        });
    }

    @Override
    public List<Cart> cartClear(final CartClearRequest request) {
        return callWithRetry(new WebServiceWrapper<List<Cart>>() {
            @Override
            public List<Cart> call() throws WebServiceException {
                return cartHandler.cartClear(request, params);
            }
        });
    }

    @Override
    public List<Cart> cartGet(final CartGetRequest request) {
        return callWithRetry(new WebServiceWrapper<List<Cart>>() {
            @Override
            public List<Cart> call() throws WebServiceException {
                return cartHandler.cartGet(request, params);
            }
        });
    }

    @Override
    public List<Cart> cartModify(final CartModifyRequest request) {
        return callWithRetry(new WebServiceWrapper<List<Cart>>() {
            @Override
            public List<Cart> call() throws WebServiceException {
                return cartHandler.cartModify(request, params);
            }
        });
    }

    <T> T callWithRetry(final WebServiceWrapper<T> wrapper) throws WebServiceException {
        int retry = 0;
        final boolean completed = false;
        T result = null;
        while (!completed) {
            try {
                result = wrapper.call();
                break;
            } catch (final WebServiceException e) {
                // Only retry on 503 errors
                if (e.getMessage().contains("503")) {
                    LOG.warn("Product Advertising API currently unavailable.", e);
                    if (retry < retryMax) {
                        LOG.debug("Running retry attempt {}/{}", ++retry, retryMax, e);
                    } else {
                        break;
                    }
                    try {
                        TimeUnit.MILLISECONDS.sleep(retryIntervallMillis * retry);
                    } catch (final InterruptedException ie) {
                        break;
                    }
                } else {
                    // Not retry-able
                    throw e;
                }
            }
        }
        return result;
    }

    public int getRetryMax() {
        return retryMax;
    }

    public void setRetryMax(final int retryMax) {
        this.retryMax = retryMax;
    }

    public long getRetryIntervallMillis() {
        return retryIntervallMillis;
    }

    public void setRetryIntervallMillis(final long retryIntervallMillis) {
        this.retryIntervallMillis = retryIntervallMillis;
    }

    public boolean isValidating() {
        return params.isValidate();
    }

    public void setValidating(final boolean validating) {
        params.setValidate(validating);
    }

    public boolean isEscaping() {
        return params.isEscaping();
    }

    public void setEscaping(final boolean escaping) {
        params.setEscaping(escaping);
    }
}
