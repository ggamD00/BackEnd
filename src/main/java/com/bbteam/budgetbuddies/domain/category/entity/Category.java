package com.bbteam.budgetbuddies.domain.category.entity;

import com.bbteam.budgetbuddies.common.BaseEntity;
import com.bbteam.budgetbuddies.domain.user.entity.User;
import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ColumnDefault("1")
    private Boolean isDefault;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

}
