package com.parma.user.service.impl;

import com.parma.common.criteria.BaseSearchCriteria;
import com.parma.common.criteria.JoinCriteria;
import com.parma.common.criteria.SearchCriteria;
import com.parma.common.criteria.SearchOperation;
import com.parma.common.dto.PageableRequest;
import com.parma.common.dto.PageableResponse;
import com.parma.user.dto.request.UserFilterRequest;
import com.parma.user.model.User;
import com.parma.user.service.UserSearchService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserSearchServiceImpl implements UserSearchService {

    private final EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public PageableResponse<User> searchUsers(UserFilterRequest filterRequest) {
        try{
            BaseSearchCriteria searchCriteria = buildSearchCriteria(filterRequest);
            return searchUsersWithCriteria(searchCriteria, filterRequest);
        } catch (Exception e) {
            log.error("Error searching users with filter {}: {}", filterRequest, e.getMessage());
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PageableResponse<User> searchUsersWithCriteria(BaseSearchCriteria searchCriteria, PageableRequest pageable) {
        try {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
            Root<User> root = criteriaQuery.from(User.class);
            // Apply search criteria
            if (searchCriteria.hasSearchCriteria()) {
                criteriaQuery.where(searchCriteria.getPredicate(criteriaBuilder, root));
            }
            // Apply sorting pagination
            if(pageable.hasSorting()){
                criteriaQuery.orderBy(
                        pageable.isDesc()
                                ? criteriaBuilder.desc(root.get(pageable.getSortBy()))
                                : criteriaBuilder.asc(root.get(pageable.getSortBy()))
                        );

            }
            // Create and execute the query
            TypedQuery<User> query = entityManager.createQuery(criteriaQuery);
            // Get total count
            CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
            Root<User> countRoot = countQuery.from(User.class);
            countQuery.select(criteriaBuilder.count(countRoot));
            if (searchCriteria.hasSearchCriteria()) {
                countQuery.where(searchCriteria.getPredicate(criteriaBuilder, countRoot));
            }
            Long totalCount = entityManager.createQuery(countQuery).getSingleResult();
            // Set pagination
            query.setFirstResult(pageable.getOffset());
            query.setMaxResults(pageable.getPageSize());
            // Get results and Execute the query
            List<User> results = query.getResultList();
            // Create response using the new PageableResponse.of() method
            return PageableResponse.of(
                    results,
                    totalCount.intValue(),
                    pageable.getPageNumber(),
                    pageable.getPageSize()
            );
        } catch (Exception e) {
            log.error("Error searching users with criteria : {}", e.getMessage());
            throw e;
        }
    }

    private BaseSearchCriteria buildSearchCriteria(UserFilterRequest filterRequest) {
        BaseSearchCriteria searchCriteria = new BaseSearchCriteria();
        try {
            // Name search - case-insensitive partial match
            if( filterRequest.hasNameFilter()) {
                searchCriteria.addCriteria(
                        new SearchCriteria(
                                "name",
                                "%" + filterRequest.getName().toLowerCase() + "%",
                                SearchOperation.MATCH
                        )
                );
            }
            // Username search - case-insensitive partial match
            if( filterRequest.hasUsernameFilter()) {
                searchCriteria.addCriteria(
                        new SearchCriteria(
                                "username",
                                filterRequest.getUsername().toLowerCase() ,
                                SearchOperation.EQUAL
                        )
                );
            }
            // Email search - case-insensitive partial match
            if( filterRequest.hasEmailFilter()) {
                searchCriteria.addCriteria(
                        new SearchCriteria(
                                "email",
                                "%" + filterRequest.getEmail().toLowerCase() + "%",
                                SearchOperation.MATCH
                        )
                );
            }
            // Status search - case-insensitive exact match
            if( filterRequest.hasStatusFilter()) {
                searchCriteria.addCriteria(
                        new SearchCriteria(
                                "status",
                                filterRequest.getStatus().toLowerCase(),
                                SearchOperation.EQUAL
                        )
                );
            }
            // Role search - case-insensitive exact match
            if( filterRequest.hasRoleFilter()){
                JoinCriteria joinCriteria = new JoinCriteria();
                joinCriteria.setJoinEntity("roles");
                joinCriteria.setPropertyField("name");
                joinCriteria.setJointValue(filterRequest.getRole().toUpperCase());
                joinCriteria.setSearchOperation(SearchOperation.EQUAL);
                searchCriteria.addJoinCriteria(joinCriteria);
            }
            // Date range search
            if( filterRequest.hasDateRange()) {
                if( filterRequest.getStartDate().isAfter(filterRequest.getEndDate())) {
                    throw new IllegalArgumentException("Start date cannot be after end date");
                }
                searchCriteria.addCriteria(
                        new SearchCriteria(
                                "createdAt",
                                filterRequest.getStartDate(),
                                SearchOperation.GREATER_THAN_EQUAL
                        )
                );
                searchCriteria.addCriteria(
                        new SearchCriteria(
                                "createdAt",
                                filterRequest.getEndDate(),
                                SearchOperation.LESS_THAN_EQUAL
                        )
                );
            }
            return searchCriteria;
        } catch (Exception e) {
            log.error("Error building search criteria : {}", e.getMessage());
            throw new IllegalArgumentException("Invalid search criteria provided: " + e.getMessage());
        }
    }
}
