package com.bi.barfdog.domain.subscribeRecipe;

import com.bi.barfdog.domain.BaseTimeEntity;
import com.bi.barfdog.domain.recipe.Recipe;
import com.bi.barfdog.domain.subscribe.Subscribe;
import lombok.*;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder @Getter
@Entity
public class SubscribeRecipe extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "subscribe_recipe_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "subscribe_id")
    private Subscribe subscribe;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    public void setSubscribe(Subscribe subscribe) {
        this.subscribe = subscribe;
    }
}
