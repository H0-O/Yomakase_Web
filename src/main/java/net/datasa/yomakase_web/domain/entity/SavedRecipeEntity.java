package net.datasa.yomakase_web.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "saved_recipe")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavedRecipeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "index_num")
    private int indexNum;

    @ManyToOne
    @JoinColumn(name = "member_num", nullable = false)
    private MemberEntity member; // Member 엔티티에서 member_num 가져오기

    @Column(name = "food_name", nullable = false, length = 100)
    private String foodName;

    @Column(name = "recipe_url", nullable = false, length = 700)
    private String recipeUrl;

    @Lob
    @Column(name = "saved_recipe", nullable = false, columnDefinition = "mediumtext")
    private String savedRecipe;
}
