package com.bi.barfdog.repository;

import java.util.List;

public interface SubscribeRecipeRepositoryCustom {
    List<String> findRecipeNamesBySubscribeId(Long id);
}
