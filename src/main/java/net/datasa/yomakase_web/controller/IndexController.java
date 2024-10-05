package net.datasa.yomakase_web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.yomakase_web.domain.dto.MemberDTO;
import net.datasa.yomakase_web.domain.dto.SavedRecipeDTO;
import net.datasa.yomakase_web.service.MemberInfoService;
import net.datasa.yomakase_web.service.MemberService;
import net.datasa.yomakase_web.service.SavedRecipeService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class IndexController {

    private final MemberService memberService;
    private final MemberInfoService memberInfoService;
    private final SavedRecipeService savedRecipeService;

    @GetMapping({"", "/"})
    public String index(@AuthenticationPrincipal UserDetails user, Model model) {

        if (user != null) {
            String username = user.getUsername();
            if (StringUtils.hasLength(username)) {
                // 이메일로 사용자 정보를 조회
                MemberDTO member = memberService.getUserByEmail(username);
                Map<String, Boolean> allergy = memberInfoService.getAllergyByEmail(username);

                if (member == null) {
                    // 만약 사용자를 찾을 수 없다면, 오류 처리 또는 리다이렉트
                    return "redirect:/";
                }
                List<SavedRecipeDTO> recipes = savedRecipeService.getSavedRecipes(member.getMemberNum());

                // 모델에 사용자 정보를 추가하여 템플릿에서 접근 가능하게 함
                model.addAttribute("member", member);
                model.addAttribute("allergy", allergy);
                model.addAttribute("recipes", recipes);
            }
        }

        return "index";
    }
}
