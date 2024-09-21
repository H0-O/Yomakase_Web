package net.datasa.yomakase_web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.yomakase_web.domain.dto.SavedRecipeDTO;
import net.datasa.yomakase_web.service.SavedRecipeService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
@RequestMapping("/api/recipes")
@RequiredArgsConstructor
public class SavedRecipeController {

    private final SavedRecipeService savedRecipeService;

    @PostMapping("/save")
    public ResponseEntity<SavedRecipeDTO> saveRecipe(@RequestBody SavedRecipeDTO savedRecipeDTO) {
        SavedRecipeDTO savedRecipe = savedRecipeService.saveRecipe(savedRecipeDTO);
        return ResponseEntity.ok(savedRecipe);
    }
}