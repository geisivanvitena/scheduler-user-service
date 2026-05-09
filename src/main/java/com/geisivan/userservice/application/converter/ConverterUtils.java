package com.geisivan.userservice.application.converter;

import java.util.List;
import java.util.function.Function;

public class ConverterUtils {

    private ConverterUtils(){}

    public static <S, T> List<T> mapList(
            List<S> source, Function<S, T> mapper) {

        if (source == null) return List.of();
        return source.stream().map(mapper).toList();
    }
}
