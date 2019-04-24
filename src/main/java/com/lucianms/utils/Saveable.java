package com.lucianms.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface Saveable<T> {

    default Logger getLogger() {
        return LoggerFactory.getLogger(getClass());
    }

    boolean save(T t);

    boolean load(T t);
}
