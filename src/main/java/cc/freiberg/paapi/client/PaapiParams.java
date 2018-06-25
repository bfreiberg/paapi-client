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

public class PaapiParams {

    private boolean validate;
    private boolean escaping;

    public PaapiParams(final boolean validating, final boolean escaping) {
        validate = validating;
        this.escaping = escaping;
    }

    public boolean isValidate() {
        return validate;
    }

    public void setValidate(final boolean validate) {
        this.validate = validate;
    }

    public boolean isEscaping() {
        return escaping;
    }

    public void setEscaping(final boolean escaping) {
        this.escaping = escaping;
    }

    public String getValidating() {
        return String.valueOf(validate);
    }

    public String getEscaping() {
        return String.valueOf(escaping);
    }
}
