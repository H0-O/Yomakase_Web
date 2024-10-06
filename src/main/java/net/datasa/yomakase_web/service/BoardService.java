package net.datasa.yomakase_web.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import net.datasa.yomakase_web.domain.dto.BoardDTO;
import net.datasa.yomakase_web.domain.dto.ReplyDTO;
import net.datasa.yomakase_web.domain.entity.BoardEntity;
import net.datasa.yomakase_web.domain.entity.MemberEntity;
import net.datasa.yomakase_web.domain.entity.ReplyEntity;
import net.datasa.yomakase_web.repository.BoardRepository;
import net.datasa.yomakase_web.repository.MemberRepository;
import net.datasa.yomakase_web.repository.ReplyRepository;
import net.datasa.yomakase_web.security.AuthenticatedUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BoardService {
	
	private final BoardRepository boardRepository;
	private final MemberRepository memberRepository;
	private final ReplyRepository replyRepository;

	// BoardEntity를 BoardDTO 타입으로 변경
	private BoardDTO convertToDTO(BoardEntity entity) {
				
		return BoardDTO.builder()
				.boardNum(entity.getBoardNum())
				.memberNum(entity.getMember().getMemberNum())
				.name(entity.getMember().getName())
				.userRole(entity.getMember().getUserRole())
				.title(entity.getTitle())
				.category(entity.getCategory())
				.contents(entity.getContents())
				.createDate(entity.getCreateDate())
				.updateDate(entity.getUpdateDate())
				.fileName(entity.getFileName())
				.recommended(entity.getRecommended())
				.build();
	}

	public void save(BoardDTO dto, String uploadPath, MultipartFile uploadFile) {
		
		MemberEntity memberEntity = memberRepository.findById(dto.getMemberNum())
				.orElseThrow(() -> new EntityNotFoundException("아이디가 없습니다"));
		
		BoardEntity boardEntity = new BoardEntity();
		boardEntity.setMember(memberEntity);
		boardEntity.setCategory(dto.getCategory());
		boardEntity.setTitle(dto.getTitle());
		boardEntity.setContents(dto.getContents());
		
		log.debug("저장되는 엔티티 : {}", boardEntity);
		
		if(uploadFile != null && !uploadFile.isEmpty()) {
			
			try {
				
				File directoryPath = new File(uploadPath);
				
				if (!directoryPath.isDirectory()) {
		            directoryPath.mkdirs();
		        }
				
				String fileName = uploadFile.getOriginalFilename();
				
				File filePath = new File(directoryPath + "/" + fileName);
				uploadFile.transferTo(filePath);
				
				boardEntity.setFileName(fileName);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		boardRepository.save(boardEntity);
		
	}

	public Page<BoardDTO> getList(int page, int pageSize, String searchType, String searchWord, String category) {

		Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "boardNum"));

		Page<BoardEntity> entityPage = null;

		if (category == null || category.isEmpty()) { // category가 비어있을 경우
			switch (searchType) {
				case "title":
					entityPage = boardRepository.findByTitleContaining(searchWord, pageable);
					break;
				case "contents":
					entityPage = boardRepository.findByContentsContaining(searchWord, pageable);
					break;
				case "name":
					entityPage = boardRepository.findByMember_NameContaining(searchWord, pageable);
					break;
				default:
					entityPage = boardRepository.findAll(pageable); // 모든 게시글을 조회
					break;
			}
		} else { // category가 있을 경우
			switch (searchType) {
				case "title":
					entityPage = boardRepository.findByCategoryAndTitleContaining(category, searchWord, pageable);
					break;
				case "contents":
					entityPage = boardRepository.findByCategoryAndContentsContaining(category, searchWord, pageable);
					break;
				case "name":
					entityPage = boardRepository.findByCategoryAndMember_NameContaining(category, searchWord, pageable);
					break;
				default:
					entityPage = boardRepository.findByCategory(category, pageable);
					break;
			}
		}

		log.debug("조회된 결과 엔티티페이지 : {}", entityPage.getContent());
		Page<BoardDTO> boardDTOPage = entityPage.map(this::convertToDTO);

		return boardDTOPage;
	}

	// ReplyEntity객체를 ReplyDTO 객체로 변환
	private ReplyDTO convertToReplyDTO(ReplyEntity entity) {
		return ReplyDTO.builder()
				.replyNum(entity.getReplyNum())
				.boardNum(entity.getBoard().getBoardNum())
				.memberNum(entity.getMember().getMemberNum())
				.memberName(entity.getMember().getName())
				.replyContents(entity.getReplyContents())
				.createDate(entity.getCreatDate())
				.build();
	}


	public BoardDTO getBoard(Integer boardNum) {
		// 글 번호로 BoardEntity 조회하여 없으면 예외, 있으면 BoardDTO로 변환하여 리턴

		BoardEntity entity = boardRepository.findById(boardNum)
				.orElseThrow(() -> new EntityNotFoundException("글이 없습니다."));

		// 조회결과 출력
		log.debug("조회된 게시글 정보: {}", entity);

		BoardDTO dto = convertToDTO(entity);

		List<ReplyDTO> replyDTOList = new ArrayList<>();
		for (ReplyEntity replyEntity : entity.getReplyList()) {
			ReplyDTO replyDTO = convertToReplyDTO(replyEntity);
			replyDTOList.add(replyDTO);
		}
		dto.setReplyList(replyDTOList);
		return dto;
	}

	public void download(Integer boardNum, String uploadPath, HttpServletResponse response) {

		// 전달된 글 번호로 파일명 확인
		BoardEntity boardEntity = boardRepository.findById(boardNum)
				.orElseThrow(() -> new EntityNotFoundException("게시글이 없습니다."));
		// 원래의 파일명을 헤더정보에 세팅
		try {
			response.setHeader("Content-Disposition", "attachment;filename="+ URLEncoder.encode(boardEntity.getFileName(), "UTF-8"));
		} catch(UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		// 저장된 파일 경로
		String fullPath = uploadPath + "/" + boardEntity.getFileName();

		// 서버의 파일을 읽을 입력 스트림과 클라이언트에게 전달할 출력스트림
		FileInputStream filein = null;
		ServletOutputStream fileout = null;

		// 파일읽기
		try {
			filein = new FileInputStream(fullPath);
			fileout = response.getOutputStream();
			// Spring과 파일 관련 유틸 이용하여 출력
			FileCopyUtils.copy(filein, fileout);

			filein.close();
			fileout.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
		// response를 통해 출력

	}

	public void replyWrite(ReplyDTO replyDTO) {

			MemberEntity memberEntity = memberRepository.findById(replyDTO.getMemberNum())
					.orElseThrow(() -> new EntityNotFoundException("사용자 아이디가 없습니다."));

			BoardEntity boardEntity = boardRepository.findById(replyDTO.getBoardNum())
					.orElseThrow(() -> new EntityNotFoundException("게시글이 없습니다."));

			ReplyEntity entity = ReplyEntity.builder()
					.board(boardEntity)
					.member(memberEntity)
					.replyContents(replyDTO.getReplyContents())
					.build();

			replyRepository.save(entity);

	}

	public void replyDelete(Integer replyNum, AuthenticatedUser user) {

		ReplyEntity replyEntity = replyRepository.findById(replyNum)
				.orElseThrow(() -> new EntityNotFoundException("리플이 없습니다."));

		if (!replyEntity.getMember().getMemberNum().equals(user.getMemberNum()) && !user.getRoleName().equals("ROLE_ADMIN")) {
			throw new RuntimeException("삭제 권한이 없습니다.");
		}
		replyRepository.delete(replyEntity);

	}

	public void delete(int boardNum, Integer memberNum, String uploadPath, String roleName) {

		BoardEntity boardEntity = boardRepository.findById(boardNum)
				.orElseThrow(() -> new EntityNotFoundException("게시글이 없습니다."));

		if (!boardEntity.getMember().getMemberNum().equals(memberNum) && !roleName.equals("ROLE_ADMIN")) {
			throw new RuntimeException("삭제 권한이 없습니다.");
		}

		try {
			Path filePath = Paths.get(uploadPath, boardEntity.getFileName());
			Files.deleteIfExists(filePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		boardRepository.delete(boardEntity);
		log.debug("2");


	}

	public void update(BoardDTO boardDTO, AuthenticatedUser user, String uploadPath, MultipartFile upload) {

		BoardEntity boardEntity = boardRepository.findById(boardDTO.getBoardNum())
				.orElseThrow(() -> new EntityNotFoundException("게시글이 없습니다."));

		log.debug("수정할 엔티티: {}", boardEntity);
		log.debug("바꿀 내용 DTO: {}", boardDTO);
		if (!boardEntity.getMember().getMemberNum().equals(user.getMemberNum()) && !user.getRoleName().equals("ROLE_ADMIN") ) {
			throw new RuntimeException("수정 권한이 없습니다.");
		}

		// 수정하면서 새로 첨부한 파일이 있을 때
		if(upload != null && !upload.isEmpty()) {
			// 그 전에 업로드한 기존 파일이 있으면 먼저 파일 삭제
			File file = new File(uploadPath, boardEntity.getFileName());
			file.delete();
			// 새로 첨부한 파일의 이름
			String fileName = upload.getOriginalFilename();

			try {
				File filePath = new File(uploadPath + "/" + fileName);
				upload.transferTo(filePath);

				boardEntity.setFileName(fileName);
			} catch(IOException e) {
				e.printStackTrace();
			}

		}

		boardEntity.setCategory(boardDTO.getCategory());
		boardEntity.setTitle(boardDTO.getTitle());
		boardEntity.setContents(boardDTO.getContents());
	}

	public void recommend(Integer boardNum) {

		BoardEntity boardEntity = boardRepository.findById(boardNum)
				.orElseThrow(() -> new EntityNotFoundException("존재하지 않는 게시글 입니다."));

		Integer recommentCount = boardEntity.getRecommended() + 1;
		boardEntity.setRecommended(recommentCount);
	}
}
