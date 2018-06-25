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

public interface PaapiClient {

    public List<Items> itemSearch(final ItemSearchRequest request);

    public List<BrowseNodes> browseNodeLookup(final BrowseNodeLookupRequest request);

    public List<Items> itemLookup(final ItemLookupRequest request);

    public List<Items> similarityLookup(final SimilarityLookupRequest request);

    public List<Cart> cartAdd(final CartAddRequest request);

    public List<Cart> cartCreate(final CartCreateRequest request);

    public List<Cart> cartClear(final CartClearRequest request);

    public List<Cart> cartGet(final CartGetRequest request);

    public List<Cart> cartModify(final CartModifyRequest request);
}
