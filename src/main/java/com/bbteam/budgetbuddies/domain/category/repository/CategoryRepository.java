package com.bbteam.budgetbuddies.domain.category.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bbteam.budgetbuddies.domain.category.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
	@Query(value = "SELECT c FROM Category AS c WHERE c.isDefault=TRUE OR c.user.id=:id")
	List<Category> findUserCategoryByUserId(@Param("id") Long id);

	boolean existsByUserIdAndName(Long userId, String name);
}