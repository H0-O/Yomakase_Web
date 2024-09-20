package net.datasa.yomakase_web.repository;

import net.datasa.yomakase_web.domain.entity.BoardEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository<BoardEntity, Integer> {

	// 모든 게시글을 페이지 단위로 조회
	Page<BoardEntity> findAll(Pageable p);

	// 전달된 문자열을 카테고리에서 검색 후 지정한 한페이지 분량 리턴
	Page<BoardEntity> findByCategory(String s, Pageable p);

	// 제목으로 검색 (부분 일치)
	Page<BoardEntity> findByTitleContaining(String title, Pageable p);

	// 내용으로 검색 (부분 일치)
	Page<BoardEntity> findByContentsContaining(String contents, Pageable p);

	// 작성자로 검색 (부분 일치)
	Page<BoardEntity> findByMember_NameContaining(String name, Pageable p);

	// 카테고리와 제목으로 검색 (부분 일치)
	Page<BoardEntity> findByCategoryAndTitleContaining(String category, String title, Pageable p);

	// 카테고리와 내용으로 검색 (부분 일치)
	Page<BoardEntity> findByCategoryAndContentsContaining(String category, String contents, Pageable p);

	// 카테고리와 작성자로 검색 (부분 일치)
	Page<BoardEntity> findByCategoryAndMember_NameContaining(String category, String name, Pageable p);
}
