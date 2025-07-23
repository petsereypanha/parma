package com.parma.common.mapper;

import java.util.List;

public interface BaseMapper<E, D, V> {
    D convertEntityToDto(E entity);
    E convertViewToEntity(V viewDto);

    List<D> convertEntityListToDtoList(List<E> entityList);
    List<E> convertViewsListToEntityList(List<V> dtoList);
}
