package org.uom.cse.cs4262.api.message.response;

import org.uom.cse.cs4262.api.message.Message;

/**
 * @author Chandu Herath
 * @date 23/10/2017
 * @since 1.0
 */
public class UnregisterResponse extends Message {

    private int value;

    public UnregisterResponse(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}