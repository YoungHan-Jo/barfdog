package com.bi.barfdog.repository;

import com.bi.barfdog.domain.setting.Setting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettingRepository extends JpaRepository<Setting, Long> {

}
