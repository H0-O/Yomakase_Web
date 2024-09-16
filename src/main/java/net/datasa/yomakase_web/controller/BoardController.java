package net.datasa.yomakase_web.controller;

import jakarta.servlet.http.HttpServletResponse;
import net.datasa.yomakase_web.domain.dto.BoardDTO;
import net.datasa.yomakase_web.security.AuthenticatedUser;
import net.datasa.yomakase_web.service.BoardService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("board")
@Slf4j
@RequiredArgsConstructor
public class BoardController {
	
	private final BoardService boardService;
	
	@Value("${board.pageSize}")		// board.pageSize: application.properties에서 설정
	int pageSize;		// 페이지 당 글 수
	
	@Value("${board.linkSize}")
	int linkSize;		// 페이지 이동 링크 수
	
	@Value("${board.uploadPath}")
	String uploadPath;	// 첨부파일 저장 경로
	
	// 게시글 목록
	@GetMapping("list")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> list(Model model,
													@AuthenticationPrincipal AuthenticatedUser user,
													@RequestParam(name="searchWord", defaultValue="") String searchWord,
													@RequestParam(name="page", defaultValue = "1") int page,
													@RequestParam(name="searchType", defaultValue = "") String searchType   // 제목, 내용, 작성자, 카테고리
			) {

		log.debug("요청 파라미터 : page={}, searchType={}, searchWord={}", page, searchType, searchWord);

		Page<BoardDTO> boardPage = boardService.getList(page, pageSize, searchType, searchWord);

		Map<String, Object> response = new HashMap<>();
		response.put("boardPage", boardPage);
		response.put("page", page);
		response.put("linkSize", linkSize);
		response.put("searchType", searchType);
		response.put("searchWord", searchWord);
		response.put("user", user);

		return ResponseEntity.ok(response);
	}

	@GetMapping("category")
	public String category(@RequestParam("category") String category,
						   @RequestParam(name="searchWord", defaultValue="") String searchWord,
						   @RequestParam(name="page", defaultValue = "1") int page,
						   @RequestParam(name="searchType", defaultValue = "") String searchType,
						   @AuthenticationPrincipal AuthenticatedUser user,
						   Model model) {
		log.debug("요청 파라미터 : page={}, searchType={}, searchWord={}, category={}", page, searchType, searchWord, category);
		Page<BoardDTO> boardPage = boardService.getCategoryList(page, pageSize, searchType, searchWord, category);

		model.addAttribute("boardPage", boardPage);
		model.addAttribute("page", page);
		model.addAttribute("linkSize", linkSize);
		model.addAttribute("searchType", searchType);
		model.addAttribute("searchWord", searchWord);
		model.addAttribute("user", user);
		model.addAttribute("category", category);

		return "listByCategory";
	}
	
	@GetMapping("write")
	public String write(@AuthenticationPrincipal AuthenticatedUser user, Model model) {

		model.addAttribute("user", user);

		return "boardWrite";
	}
	
	@PostMapping("write")
	public String write(@ModelAttribute BoardDTO dto,
						@RequestParam("upload") MultipartFile upload,		// 변수명은 input태그의 name과 같아야 함
						@AuthenticationPrincipal AuthenticatedUser user) {
		// log.debug("게시판 글 : {}, 사용자정보: {}", dto, user);

		dto.setMemberNum(user.getMemberNum());

		boardService.save(dto, uploadPath, upload);	// 작성글, 저장경로, 업로드파일에 대한 정보
		
		return "redirect:/";
	}

	@GetMapping("read")
	public String read(Model model, @RequestParam("boardNum") Integer boardNum) {
		try {
			BoardDTO dto = boardService.getBoard(boardNum);
			model.addAttribute("board", dto);
			return "boardRead";
		}
		catch(Exception e){
			e.printStackTrace();
			return "redirect:list";
		}
	}

	@GetMapping("download")
	public void download(@RequestParam("boardNum") Integer boardNum
			, HttpServletResponse response) {

		boardService.download(boardNum, uploadPath, response);
	}

}
