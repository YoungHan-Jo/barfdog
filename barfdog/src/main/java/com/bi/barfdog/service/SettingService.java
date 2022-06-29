package com.bi.barfdog.service;

import com.bi.barfdog.api.settingDto.UpdateSettingDto;
import com.bi.barfdog.domain.setting.Setting;
import com.bi.barfdog.repository.setting.SettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class SettingService {

    private final SettingRepository settingRepository;


    @Transactional
    public void updateSetting(UpdateSettingDto requestDto) {
        Setting setting = settingRepository.findAll().get(0);
        setting.update(requestDto);
    }
}
