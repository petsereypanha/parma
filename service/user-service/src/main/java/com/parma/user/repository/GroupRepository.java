package com.parma.user.repository;

import com.parma.user.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

    List<Group> findAllByNameIn(Set<String> groupNames);

    boolean existsByName(String name);
}

