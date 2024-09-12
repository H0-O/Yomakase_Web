package net.datasa.yomakase_web.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavedRecipeDTO {
    private int indexNum;
    private int memberNum;
    private String foodName;
    private String recipeUrl;
    private String savedRecipe;
}
