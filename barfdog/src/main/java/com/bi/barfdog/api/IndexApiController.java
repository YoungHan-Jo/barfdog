package com.bi.barfdog.api;

import com.bi.barfdog.api.memberDto.MemberSaveRequestDto;
import com.bi.barfdog.common.ErrorsResource;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.repository.MemberRepository;
import com.bi.barfdog.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequiredArgsConstructor
@RequestMapping(produces = MediaTypes.HAL_JSON_VALUE)
@RestController
public class IndexApiController {

    @GetMapping("/api")
    public RepresentationModel index(){
        RepresentationModel index = new RepresentationModel();
        index.add(linkTo(IndexApiController.class).withRel("banners"));
        return index;
    }

    @PostMapping("/api/join")
    public ResponseEntity join(@RequestBody @Valid MemberSaveRequestDto requestDto, Errors errors) {
        if (errors.hasErrors()) {
            return badRequest(errors);
        }

        RepresentationModel representationModel = new RepresentationModel();

        return ResponseEntity.created(null).body(representationModel);
    }




    private ResponseEntity<EntityModel<Errors>> badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }

}
