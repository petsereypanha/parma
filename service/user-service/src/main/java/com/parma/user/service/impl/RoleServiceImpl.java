package com.parma.user.service.impl;

import com.parma.common.constant.ApiConstant;
import com.parma.common.exception.BusinessException;
import com.parma.common.exception.ResponseErrorTemplate;
import com.parma.common.repository.BaseRepository;
import com.parma.user.dto.request.RoleFilterRequest;
import com.parma.user.dto.request.RoleRequest;
import com.parma.user.model.Role;
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
            if( validatedError != null && validatedError.isError()) {
                return validatedError;
            }
            // Chack for duplicate role name
            if (roleRepository.findFirstByName(request.name()).isPresent()) {
                return createErrorResponse("Role name already exists", ApiConstant.BUSINESS_ERROR.getKey());
            }
            Role role = new Role();
            role = roleHandlerService.convertRoleRequestToRole(request, role);
            role.setStatus(ApiConstant.ACTIVE.getKey());
            role = roleRepository.save(role);
            return createSuccessResponse(roleHandlerService.convertRoleToRoleResponse(role));
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
    @Transactional
    public ResponseErrorTemplate update(Long id, RoleRequest request) {
        try {
            // Validate the request
            ResponseErrorTemplate validationError = roleHandlerService.roleRequestValidation(request);
            if (validationError != null && validationError.isError()) {
                return validationError;
            }
            // Find existing role
            Role role = roleRepository.findFirstById(id)
                    .orElseThrow(() -> new BusinessException(
                            String.format(ApiConstant.ROLE_ID_NOT_FOUND.getDescription(), id)));
            // Check for duplicate name if name is being updated
            if (request.name() != null && !request.name().equals(role.getName())) {
                roleRepository.findFirstByName(request.name())
                        .ifPresent(existingRole -> {
                            if (!existingRole.getId().equals(id)) {
                                throw new BusinessException("Role name already exists");
                            }
                        });
            }
            // Update role
            role = roleHandlerService.convertRoleRequestToRole(request, role);
            role.setStatus(request.status() != null ? request.status() : role.getStatus());
            role = roleRepository.saveAndFlush(role);

            return createSuccessResponse(roleHandlerService.convertRoleToRoleResponse(role));
        } catch (BusinessException e) {
            log.error("Business error updating role: {}", e.getMessage());
            return createErrorResponse(e.getMessage(), ApiConstant.BUSINESS_ERROR.getKey());
        } catch (Exception e) {
            log.error("Unexpected error updating role: {}", e.getMessage());
            return createErrorResponse(ApiConstant.INTERNAL_SERVER_ERROR.getDescription(),
                    ApiConstant.INTERNAL_SERVER_ERROR.getKey());
        }
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
    // Method to create a standardized success response
    private ResponseErrorTemplate createSuccessResponse(Object data) {
        return new ResponseErrorTemplate(
                ApiConstant.SUCCESS.getDescription(),
                ApiConstant.SUCCESS.getKey(),
                data,
                false
        );
    }
    // create a standardized error response
    private ResponseErrorTemplate createErrorResponse(String message, String code) {
        return new ResponseErrorTemplate(
                message,
                code,
                new Object(),
                true
        );
    }
}
