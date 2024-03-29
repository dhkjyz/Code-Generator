package com.mango.maker.meta;

public class MetaException extends RuntimeException {

    private String text;

    public MetaException(String text) {
        super(text);
    }
}
