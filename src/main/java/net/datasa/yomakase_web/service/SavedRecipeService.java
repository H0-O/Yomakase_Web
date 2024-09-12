package net.datasa.yomakase_web.service;

import lombok.RequiredArgsConstructor;
import net.datasa.yomakase_web.repository.SavedRecipeRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SavedRecipeService {

    private final SavedRecipeRepository savedRecipeRepository;

}
