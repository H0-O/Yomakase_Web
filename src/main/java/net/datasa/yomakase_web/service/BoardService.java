package net.datasa.yomakase_web.service;

import java.io.File;
import java.io.IOException;

import net.datasa.yomakase_web.domain.dto.BoardDTO;
import net.datasa.yomakase_web.domain.entity.BoardEntity;
import net.datasa.yomakase_web.domain.entity.MemberEntity;
import net.datasa.yomakase_web.repository.BoardRepository;
import net.datasa.yomakase_web.repository.MemberRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardService {
	
	private final BoardRepository boardRepository;
	private final MemberRepository memberRepository;

	private BoardDTO convertToDTO(BoardEntity entity) {
				
		return BoardDTO.builder()
				.boardNum(entity.getBoardNum())
				.memberNum(entity.getMember().getMemberNum())
				.name(entity.getMember().getName())
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

    public Page<BoardDTO> getList(int page, int pageSize, String searchType, String searchWord) {

		Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "boardNum"));

		Page<BoardEntity> entityPage = null;

			switch (searchType) {
				case "title" :
					entityPage = boardRepository.findByTitleContaining(searchWord, pageable);     break;
				case "contents" :
					entityPage = boardRepository.findByContentsContaining(searchWord, pageable);     break;
				case "name" :
					entityPage = boardRepository.findByMember_Name(searchWord, pageable);     break;
				default :
					entityPage = boardRepository.findAll(pageable);     break;

			}
		log.debug("조회된 결과 엔티티페이지 : {}", entityPage.getContent());

		Page<BoardDTO> boardDTOPage = entityPage.map(this::convertToDTO);

		return boardDTOPage;
	}

	public Page<BoardDTO> getCategoryList(int page, int pageSize, String searchType, String searchWord, String category) {

		Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "boardNum"));

		Page<BoardEntity> entityPage = null;

		switch (searchType) {
			case "title" :
				entityPage = boardRepository.findByCategoryAndTitle(category, searchWord, pageable);     break;
			case "contents" :
				entityPage = boardRepository.findByCategoryAndContents(category, searchWord, pageable);     break;
			case "name" :
				entityPage = boardRepository.findByCategoryAndMember_Name(category, searchWord, pageable);     break;
			default :
				entityPage = boardRepository.findByCategory(category, pageable);     break;
		}
		log.debug("조회된 결과 엔티티페이지 : {}", entityPage.getContent());
		Page<BoardDTO> boardDTOPage = entityPage.map(this::convertToDTO);

		return boardDTOPage;
	}
}
