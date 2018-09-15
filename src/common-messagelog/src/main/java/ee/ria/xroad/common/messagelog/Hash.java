/**
 * The MIT License
 * Copyright (c) 2018 Estonian Information System Authority (RIA), Nordic Institute for Interoperability Solutions (NIIS), Population Register Centre (VRK)
 * Copyright (c) 2015-2017 Estonian Information System Authority (RIA), Population Register Centre (VRK)
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
package ee.ria.xroad.common.messagelog;

import lombok.Data;
import org.apache.commons.lang.StringUtils;

/**
 * Encapsulates a hash value and algorithm ID used to compute it.
 */
@Data
public final class Hash {

    private static final char SEPARATOR = ':';

    private final String algoId;
    private final String hashValue;

    /**
     * Constructs a hash object from a colon-separated hash string.
     * @param hashString hash string containing algorithm ID and hash value
     */
    public Hash(String hashString) {
        if (hashString == null) {
            throw new IllegalArgumentException("hashString must not be null");
        }

        int idx = hashString.indexOf(SEPARATOR);
        if (idx == -1) {
            throw new IllegalArgumentException("hash string must be in the "
                    + "form of '<algorithm id>" + SEPARATOR + "<value>'");
        }

        algoId = hashString.substring(0, idx);
        hashValue = hashString.substring(idx + 1);

        verifyFields();
    }

    /**
     * Constructs a hash object from a algorithm ID and hash value.
     * @param algoId the algorithm ID
     * @param hashValue the hash value
     */
    public Hash(String algoId, String hashValue) {
        this.algoId = algoId;
        this.hashValue = hashValue;

        verifyFields();
    }

    @Override
    public String toString() {
        return algoId + SEPARATOR + hashValue;
    }

    private void verifyFields() {
        if (StringUtils.isBlank(algoId)) {
            throw new IllegalArgumentException("algoId must not be blank");
        }

        if (StringUtils.isBlank(hashValue)) {
            throw new IllegalArgumentException("hashValue must not be blank");
        }
    }
}
