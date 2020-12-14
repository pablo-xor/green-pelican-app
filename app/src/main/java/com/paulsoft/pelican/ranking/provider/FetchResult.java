package com.paulsoft.pelican.ranking.provider;

public interface FetchResult<T> {

    void afterFetched(T result);

}
