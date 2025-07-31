package com.parma.user.service;

import com.parma.common.exception.ResponseErrorTemplate;
import com.parma.user.dto.request.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

public interface RoleService {

    ResponseErrorTemplate create(RoleRequest request);

    ResponseErrorTemplate update(Long id, RoleRequest request);

    ResponseErrorTemplate findById(Long id);

    ResponseErrorTemplate findAll(RoleFilterRequest filterRequest);

    ResponseErrorTemplate delete(Long id);

    ResponseErrorTemplate deleteAll(Set<Long> ids);

    ResponseErrorTemplate findByName(String name);

    ResponseErrorTemplate disActivateRole(Set<Long> ids, String status);
}
