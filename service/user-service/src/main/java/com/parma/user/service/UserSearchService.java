package com.parma.user.service;

import com.parma.common.criteria.BaseSearchCriteria;
import com.parma.common.dto.*;
import com.parma.user.dto.request.UserFilterRequest;
import com.parma.user.model.User;

public interface UserSearchService {
    PageableResponse<User> searchUsers(UserFilterRequest filterRequest);

    PageableResponse<User> searchUsersWithCriteria(BaseSearchCriteria searchCriteria, PageableRequest pageable);
}
