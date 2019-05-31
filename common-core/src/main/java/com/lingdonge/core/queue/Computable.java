package com.lingdonge.core.queue;

public interface Computable<V> {
    V compute(String k);
}