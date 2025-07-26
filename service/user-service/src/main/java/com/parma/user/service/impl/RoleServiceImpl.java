package com.parma.user.service.impl;

import com.parma.common.constant.ApiConstant;
import com.parma.common.exception.BusinessException;
import com.parma.common.exception.ResponseErrorTemplate;
import com.parma.common.repository.BaseRepository;
import com.parma.user.dto.request.RoleFilterRequest;
import com.parma.user.dto.request.RoleRequest;
import com.parma.user.repository.RoleRepository;
import com.parma.user.service.RoleService;
import com.parma.user.service.handler.PageableResponseHandlerService;
import com.parma.user.service.handler.RoleHandlerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final RoleHandlerService roleHandlerService;
    private final BaseRepository baseRepository;
    private final PageableResponseHandlerService pageableResponseHandlerService;

    @Override
    @Transactional
    public ResponseErrorTemplate create(RoleRequest request) {
        try {
            // Validate the request
            ResponseErrorTemplate validatedError = roleHandlerService.roleRequestValidation(request);
        } catch (BusinessException e) {
            log.error("Business error retrieving roles with filter {}: {}", request, e.getMessage());
            return createErrorResponse(e.getMessage(), ApiConstant.BUSINESS_ERROR.getKey());
        } catch (Exception e) {
            log.error("Unexpected error creating role: {}", e.getMessage());
            return createErrorResponse(ApiConstant.INTERNAL_SERVER_ERROR.getDescription(),
                    ApiConstant.INTERNAL_SERVER_ERROR.getKey());
        }
    }

    @Override
    public ResponseErrorTemplate update(Long id, RoleRequest request) {
        return null;
    }

    @Override
    public ResponseErrorTemplate findById(Long id) {
        return null;
    }

    @Override
    public ResponseErrorTemplate findAll(RoleFilterRequest filterRequest) {
        return null;
    }

    @Override
    public ResponseErrorTemplate delete(Long id) {
        return null;
    }

    @Override
    public ResponseErrorTemplate deleteAll(Set<Long> ids) {
        return null;
    }

    @Override
    public ResponseErrorTemplate findByName(String name) {
        return null;
    }

    @Override
    public ResponseErrorTemplate disActivateRole(Set<Long> ids, String status) {
        return null;
    }
    private ResponseErrorTemplate createErrorResponse(String message, String code) {
        return new ResponseErrorTemplate(
                message,
                code,
                new Object(),
                true
        );
    }
}
