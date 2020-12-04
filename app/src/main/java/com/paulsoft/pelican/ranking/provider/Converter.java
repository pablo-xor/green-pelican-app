package com.paulsoft.pelican.ranking.provider;

public interface Converter<I, O> {

    O convert(I input);

}
