package net.datasa.yomakase_web.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.datasa.yomakase_web.domain.dto.BoardDTO;
import net.datasa.yomakase_web.domain.dto.ReplyDTO;
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

	@GetMapping("list")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> list(
			@AuthenticationPrincipal AuthenticatedUser user,
			@RequestParam(name="searchWord", defaultValue="") String searchWord,
			@RequestParam(name="page", defaultValue = "1") int page,
			@RequestParam(name="searchType", defaultValue = "") String searchType,
			@RequestParam(name="category", defaultValue = "") String category // 카테고리 파라미터 추가
	) {
		log.debug("요청 파라미터 : page={}, searchType={}, searchWord={}, category={}", page, searchType, searchWord, category);

		Page<BoardDTO> boardPage = boardService.getList(page, pageSize, searchType, searchWord, category);

		Map<String, Object> response = new HashMap<>();
		response.put("boardPage", boardPage);
		response.put("page", page);
		response.put("linkSize", linkSize);
		response.put("searchType", searchType);
		response.put("searchWord", searchWord);
		response.put("category", category);
		response.put("user", user);

		return ResponseEntity.ok(response);
	}
	
	// 글쓰기 페이지로 이동
	@GetMapping("write")
	public String write(@AuthenticationPrincipal AuthenticatedUser user, Model model) {

		model.addAttribute("user", user);

		return "boardWrite";
	}
	
	// 작성한 게시글을 DB에 저장
	@PostMapping("write")
	public String write(@ModelAttribute BoardDTO dto,
						@RequestParam("upload") MultipartFile upload,		// 변수명은 input태그의 name과 같아야 함
						@AuthenticationPrincipal AuthenticatedUser user) {
		// log.debug("게시판 글 : {}, 사용자정보: {}", dto, user);

		dto.setMemberNum(user.getMemberNum());

		boardService.save(dto, uploadPath, upload);	// 작성글, 저장경로, 업로드파일에 대한 정보
		
		return "redirect:/";
	}

	// 선택한 게시글 조회
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

	// 게시글 지우기
	@GetMapping("delete")
	public String delete(
			@RequestParam("boardNum") int boardNum
			, @AuthenticationPrincipal AuthenticatedUser user) {

		try {
			log.debug("1");
			boardService.delete(boardNum, user.getMemberNum(), uploadPath, user.getRoleName());
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return "redirect:/";
	}

	// 게시글 수정 폼으로 이동
	@GetMapping("update")
	public String update(
			Model model
			, @RequestParam("boardNum") int boardNum
			, @AuthenticationPrincipal AuthenticatedUser user) {

		try {
			BoardDTO boardDTO = boardService.getBoard(boardNum);

			if (!user.getMemberNum().equals(boardDTO.getMemberNum()) && !user.getRoleName().equals("ROLE_ADMIN")) {
				throw new RuntimeException("수정 권한이 없습니다.");
			}
			model.addAttribute("board", boardDTO);
			model.addAttribute("user", user);
			return "boardUpdate";
		}
		catch (Exception e) {
			e.printStackTrace();
			return "redirect:/";
		}
	}

	// 수정된 게시글 저장
	@PostMapping("update")
	public String update(
			@ModelAttribute BoardDTO boardDTO
			, @AuthenticationPrincipal AuthenticatedUser user
			, @RequestParam("upload") MultipartFile upload) {

		try {

			boardService.update(boardDTO, user, uploadPath, upload);
			return "redirect:read?boardNum=" + boardDTO.getBoardNum();

		}
		catch (Exception e) {
			e.printStackTrace();
			return "redirect:/";
		}
	}

	@PostMapping("recommend")
	@ResponseBody
	public Map<String, String> recommend(@RequestParam("boardNum") Integer boardNum, HttpServletResponse response, HttpServletRequest request){
		String cookieName = "recommend_" + boardNum;
		Map<String, String> result = new HashMap<>();

		// 쿠키 확인
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(cookieName)) {
					result.put("status", "already");
					result.put("message", "이미 추천한 게시글입니다.");
					return result;
				}
			}
		}

		// 추천 처리
		boardService.recommend(boardNum);

		// 추천 기록을 쿠키에 저장
		Cookie cookie = new Cookie(cookieName, "true");
		cookie.setMaxAge(60 * 60 * 24 * 365); // 1년 동안 유지
		response.addCookie(cookie);

		result.put("status", "success");
		result.put("message", "추천이 완료되었습니다.");
		return result;
	}

	// 글에 첨부된 파일 다운로드
	@GetMapping("download")
	public void download(@RequestParam("boardNum") Integer boardNum
			, HttpServletResponse response) {

		boardService.download(boardNum, uploadPath, response);
	}

	// 댓글 저장
	@PostMapping("replyWrite")
	public String replyWrite(@ModelAttribute ReplyDTO replyDTO
			, @AuthenticationPrincipal AuthenticatedUser user) {
		replyDTO.setMemberNum(user.getMemberNum());
		boardService.replyWrite(replyDTO);
		return "redirect:read?boardNum=" + replyDTO.getBoardNum();
	}

	// 댓글 삭제
	@GetMapping("replyDelete")
	public String replyDelete(
			@ModelAttribute ReplyDTO replyDTO
			, @AuthenticationPrincipal AuthenticatedUser user) {

		try {
			boardService.replyDelete(replyDTO.getReplyNum(), user);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:read?boardNum=" + replyDTO.getBoardNum();
	}



}
