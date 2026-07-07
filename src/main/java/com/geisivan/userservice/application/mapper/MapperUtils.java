package com.geisivan.userservice.application.mapper;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MapperUtils {

    private MapperUtils() {}

    public static <S, T> List<T> mapList(
            List<S> source, Function<S, T> mapper) {

        if (source == null) return List.of();
        return source.stream().map(mapper).toList();
    }

    public static <S, T> Set<T> mapSet(
            Set<S> source, Function<S, T> mapper) {

        if (source == null) return Set.of();
        return source.stream().map(mapper).collect(Collectors.toSet());
    }
}
