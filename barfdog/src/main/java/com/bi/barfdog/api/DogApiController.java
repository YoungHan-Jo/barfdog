package com.bi.barfdog.api;

import com.bi.barfdog.domain.BaseTimeEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.MediaTypes;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping(value = "/api/dogs", produces = MediaTypes.HAL_JSON_VALUE)
@RestController
public class DogApiController extends BaseTimeEntity {



}
