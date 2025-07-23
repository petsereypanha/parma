package com.parma.user.service;

import com.parma.common.exception.ResponseErrorTemplate;
import com.parma.user.dto.request.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

public interface RoleService {

    @Transactional
    ResponseErrorTemplate create(RoleRequest request);

    @Transactional
    ResponseErrorTemplate update(Long id, RoleRequest request);

    @Transactional(readOnly = true)
    ResponseErrorTemplate findById(Long id);

    @Transactional(readOnly = true)
    ResponseErrorTemplate findAll(RoleFilterRequest filterRequest);

    @Transactional
    ResponseErrorTemplate delete(Long id);

    @Transactional
    ResponseErrorTemplate deleteAll(Set<Long> ids);

    @Transactional(readOnly = true)
    ResponseErrorTemplate findByName(String name);

    @Transactional
    ResponseErrorTemplate disActivateRole(Set<Long> ids, String status);
}
