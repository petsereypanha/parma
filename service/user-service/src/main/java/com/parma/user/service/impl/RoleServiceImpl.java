package com.parma.user.service.impl;

import com.parma.common.constant.ApiConstant;
import com.parma.common.criteria.BaseSearchCriteria;
import com.parma.common.criteria.SearchCriteria;
import com.parma.common.criteria.SearchOperation;
import com.parma.common.dto.Metadata;
import com.parma.common.dto.PageableResponse;
import com.parma.common.exception.BusinessException;
import com.parma.common.exception.ResponseErrorTemplate;
import com.parma.common.repository.BaseRepository;
import com.parma.user.dto.request.RoleFilterRequest;
import com.parma.user.dto.request.RoleRequest;
import com.parma.user.dto.response.RolePaginationResponse;
import com.parma.user.dto.response.RoleResponse;
import com.parma.user.model.Role;
import com.parma.user.repository.RoleRepository;
import com.parma.user.service.RoleService;
import com.parma.user.service.handler.PageableResponseHandlerService;
import com.parma.user.service.handler.RoleHandlerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
    @Transactional(readOnly = true)
    public ResponseErrorTemplate findById(Long id) {
        try {
            Role role = roleRepository.findById(id)
                    .orElseThrow(() -> new BusinessException(
                            String.format(ApiConstant.ROLE_ID_NOT_FOUND.getDescription(), id)));

            return createSuccessResponse(roleHandlerService.convertRoleToRoleResponse(role));
        } catch (BusinessException e) {
            log.error("Business error finding role by id {}: {}", id, e.getMessage());
            return createErrorResponse(e.getMessage(), ApiConstant.BUSINESS_ERROR.getKey());
        } catch (Exception e) {
            log.error("Unexpected error finding role by id {}: {}", id, e.getMessage());
            return createErrorResponse(ApiConstant.INTERNAL_SERVER_ERROR.getDescription(),
                    ApiConstant.INTERNAL_SERVER_ERROR.getKey());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseErrorTemplate findAll(RoleFilterRequest filterRequest) {
        try {
            // Build search criteria
            BaseSearchCriteria baseSearchCriteria = buildSearchCriteria(filterRequest);
            // Get paginated results
            PageableResponse<Role> pageResponse = baseRepository.listPage(
                    Role.class, baseSearchCriteria, filterRequest);
            if (pageResponse == null || pageResponse.getContent().isEmpty()) {
                log.info("No roles found with filter: {}", filterRequest);
                return createErrorResponse(ApiConstant.ROLE_NOT_FOUND.getDescription(),
                        ApiConstant.ROLE_NOT_FOUND.getKey());
            }
            // Convert roles to response DTOs
            List<RoleResponse> roleResponses = pageResponse.getContent().stream()
                    .map(roleHandlerService::convertRoleToRoleResponse)
                    .collect(Collectors.toList());
            // Create pagination response
            RolePaginationResponse rolePaginationResponse = new RolePaginationResponse(
                    roleResponses,
                    pageableResponseHandlerService.handlePaginationResponse(
                            pageResponse.getTotalElements(),
                            pageResponse.getPageNumber(),
                            pageResponse.getPageSize()
                    ),
                    Metadata.builder()
                            .hasNext(!pageResponse.isLast() && !pageResponse.isEmpty())
                            .totalUsers(pageResponse.getTotalElements())
                            .hasPrevious(!pageResponse.isFirst() && !pageResponse.isEmpty())
                            .currentPage(pageResponse.getPageNumber())
                            .pageSize(pageResponse.getPageSize())
                            .build()
            );

            return createSuccessResponse(rolePaginationResponse);
        } catch (BusinessException e) {
            log.error("Business error retrieving roles with filter {}: {}", filterRequest, e.getMessage());
            return createErrorResponse(e.getMessage(), ApiConstant.BUSINESS_ERROR.getKey());
        } catch (Exception e) {
            log.error("Unexpected error retrieving roles with filter {}: {}", filterRequest, e.getMessage());
            return createErrorResponse(ApiConstant.INTERNAL_SERVER_ERROR.getDescription(),
                    ApiConstant.INTERNAL_SERVER_ERROR.getKey());
        }
    }

    @Override
    @Transactional
    public ResponseErrorTemplate delete(Long id) {
        try {
            Role role = roleRepository.findFirstById(id)
                    .orElseThrow(() -> new BusinessException(
                            String.format(ApiConstant.ROLE_ID_NOT_FOUND.getDescription(), id)));

            roleRepository.delete(role);
            return createSuccessResponse(null);
        } catch (BusinessException e) {
            log.error("Business error deleting role {}: {}", id, e.getMessage());
            return createErrorResponse(e.getMessage(), ApiConstant.BUSINESS_ERROR.getKey());
        } catch (Exception e) {
            log.error("Unexpected error deleting role {}: {}", id, e.getMessage());
            return createErrorResponse(ApiConstant.INTERNAL_SERVER_ERROR.getDescription(),
                    ApiConstant.INTERNAL_SERVER_ERROR.getKey());
        }
    }

    @Override
    @Transactional
    public ResponseErrorTemplate deleteAll(Set<Long> ids) {
        try {
            List<Role> roles = roleRepository.findAllByIdIn(ids);
            if (roles.isEmpty()) {
                return createErrorResponse(ApiConstant.ROLE_NOT_FOUND.getDescription(),
                        ApiConstant.ROLE_NOT_FOUND.getKey());
            }

            roleRepository.deleteAll(roles);
            return createSuccessResponse(null);
        } catch (BusinessException e) {
            log.error("Business error deleting roles {}: {}", ids, e.getMessage());
            return createErrorResponse(e.getMessage(), ApiConstant.BUSINESS_ERROR.getKey());
        } catch (Exception e) {
            log.error("Unexpected error deleting roles {}: {}", ids, e.getMessage());
            return createErrorResponse(ApiConstant.INTERNAL_SERVER_ERROR.getDescription(),
                    ApiConstant.INTERNAL_SERVER_ERROR.getKey());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseErrorTemplate findByName(String name) {
        try {
            Role role = roleRepository.findFirstByNameAndStatus(name, ApiConstant.ACTIVE.getKey())
                    .orElseThrow(() -> new BusinessException(
                            String.format(ApiConstant.ROLE_NAME_NOT_FOUND.getDescription(), name)));

            return createSuccessResponse(roleHandlerService.convertRoleToRoleResponse(role));
        } catch (BusinessException e) {
            log.error("Business error finding role by name {}: {}", name, e.getMessage());
            return createErrorResponse(e.getMessage(), ApiConstant.BUSINESS_ERROR.getKey());
        } catch (Exception e) {
            log.error("Unexpected error finding role by name {}: {}", name, e.getMessage());
            return createErrorResponse(ApiConstant.INTERNAL_SERVER_ERROR.getDescription(),
                    ApiConstant.INTERNAL_SERVER_ERROR.getKey());
        }
    }

    @Override
    @Transactional
    public ResponseErrorTemplate disActivateRole(Set<Long> ids, String status) {
        try {
            List<Role> roles = roleRepository.findAllByIdIn(ids);
            if (roles.isEmpty()) {
                return createErrorResponse(ApiConstant.ROLE_NOT_FOUND.getDescription(),
                        ApiConstant.ROLE_NOT_FOUND.getKey());
            }

            roles.forEach(role -> {
                role.setStatus(Optional.ofNullable(status).orElse(ApiConstant.IN_ACTIVE.getKey()));
                roleRepository.saveAndFlush(role);
            });

            return createSuccessResponse(null);
        } catch (BusinessException e) {
            log.error("Business error deactivating roles: {}", e.getMessage());
            return createErrorResponse(e.getMessage(), ApiConstant.BUSINESS_ERROR.getKey());
        } catch (Exception e) {
            log.error("Unexpected error deactivating roles: {}", e.getMessage());
            return createErrorResponse(ApiConstant.INTERNAL_SERVER_ERROR.getDescription(),
                    ApiConstant.INTERNAL_SERVER_ERROR.getKey());
        }
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
    // Method to build search criteria from the filter request
    private BaseSearchCriteria buildSearchCriteria(RoleFilterRequest roleFilterRequest) {
        BaseSearchCriteria baseSearchCriteria = new BaseSearchCriteria();
        if (roleFilterRequest.hasId()) {
            baseSearchCriteria.addCriteria(new SearchCriteria(
                    "id", roleFilterRequest.getId(), SearchOperation.EQUAL));
        }
        if (roleFilterRequest.hasName()) {
            baseSearchCriteria.addCriteria(new SearchCriteria(
                    "name", "%" + roleFilterRequest.getName().toLowerCase() + "%", SearchOperation.MATCH));
        }
        if (roleFilterRequest.hasStatus()) {
            baseSearchCriteria.addCriteria(new SearchCriteria(
                    "status", roleFilterRequest.getStatus(), SearchOperation.EQUAL));
        }
        if (StringUtils.hasText(roleFilterRequest.getSortBy())) {
            baseSearchCriteria.setSortBy(roleFilterRequest.getSortBy());
            baseSearchCriteria.setDesc(roleFilterRequest.isDesc());
        }
        return baseSearchCriteria;
    }
}
