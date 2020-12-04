package com.paulsoft.pelican.ranking.provider;

import android.util.Log;

import lombok.AllArgsConstructor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AllArgsConstructor
public class ConvertedResultWrapper<T, O> implements Callback<T> {

    private final FetchResult<O> fetchResult;
    private final Converter<T, O> converter;

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        T result = response.body();
        Log.d(getClass().getSimpleName(), "Result: " + result);
        fetchResult.afterFetched(converter.convert(result));
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        Log.e(getClass().getSimpleName(), "Err: " + t.getClass().getSimpleName() + ":" + t.getMessage(), t);
    }

}
