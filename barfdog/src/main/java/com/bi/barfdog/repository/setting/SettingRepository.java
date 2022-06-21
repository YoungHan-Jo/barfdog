package com.bi.barfdog.repository.setting;

import com.bi.barfdog.domain.setting.Setting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettingRepository extends JpaRepository<Setting, Long>,SettingRepositoryCustom {

}
