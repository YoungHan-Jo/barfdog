package com.bi.barfdog.api;

import com.bi.barfdog.auth.CurrentUser;
import com.bi.barfdog.common.ErrorsResource;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.repository.card.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping(value = "/api/cards",produces = MediaTypes.HAL_JSON_VALUE)
@RestController
public class CardApiController {

    private final CardRepository cardRepository;

    @GetMapping
    public ResponseEntity queryCards(@CurrentUser Member member) {

        cardRepository.findSubscribeCards(member);

        return ResponseEntity.ok(null);
    }















    private ResponseEntity<EntityModel<Errors>> badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }

    private ResponseEntity<Object> notFound() {
        return ResponseEntity.notFound().build();
    }

}
