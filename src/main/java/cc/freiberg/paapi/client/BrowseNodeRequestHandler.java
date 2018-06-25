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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.ws.Holder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ecs.client.jax.AWSECommerceServicePortType;
import com.ecs.client.jax.BrowseNodeLookupRequest;
import com.ecs.client.jax.BrowseNodes;
import com.ecs.client.jax.Errors;
import com.ecs.client.jax.OperationRequest;

public class BrowseNodeRequestHandler {

    private static final Logger LOG = LoggerFactory.getLogger(BrowseNodeRequestHandler.class);
    private final String marketplace;
    private final String accessKeyId;
    private final String tag;
    private final AWSECommerceServicePortType port;

    public BrowseNodeRequestHandler(final String marketplace, final String accessKeyId, final String tag,
            final AWSECommerceServicePortType port) {
        this.marketplace = marketplace;
        this.accessKeyId = accessKeyId;
        this.tag = tag;
        this.port = port;
    }

    List<BrowseNodes> browseNodeLookup(final BrowseNodeLookupRequest request, final PaapiParams params) {
        final List<BrowseNodeLookupRequest> requests = new ArrayList<>();
        final Holder<OperationRequest> operationRequest = new Holder<>();
        final Holder<List<BrowseNodes>> result = new Holder<>();
        port.browseNodeLookup(marketplace,
                accessKeyId,
                tag,
                params.getValidating(),
                params.getEscaping(),
                request,
                requests,
                operationRequest,
                result);
        LOG.info("browseNodeLookup took {}", operationRequest.value.getRequestProcessingTime());
        if (params.isValidate()) {
            checkForErrors(result);
        }
        return result.value;
    }

    static void checkForErrors(final Holder<List<BrowseNodes>> result) {
        if (!result.value.isEmpty() && result.value.get(0).getRequest().getErrors() == null
                && "True".equalsIgnoreCase(result.value.get(0).getRequest().getIsValid())) {
            return;
        }
        final String errorMessage = result.value.stream().flatMap(i -> i.getRequest().getErrors().getError().stream())
                .map(e -> makeString(e)).collect(Collectors.joining(System.lineSeparator()));
        throw new IllegalArgumentException(errorMessage);
    }

    static String makeString(final Errors.Error error) {
        return error.getCode() + ": " + error.getMessage();
    }
}
