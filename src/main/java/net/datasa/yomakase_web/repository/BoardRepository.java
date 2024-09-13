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

	//전달된 문자열을 제목에서 검색한 후 지정한 한페이지 분량 리턴
	Page<BoardEntity> findByTitleContaining(String s, Pageable p);

	//전달된 문자열을 본문에서 검색한 후 지정한 한페이지 분량 리턴
	Page<BoardEntity> findByContentsContaining(String s, Pageable p);

	//전달된 문자열을 이름에서 검색 후 지정한 한페이지 분량 리턴
	Page<BoardEntity> findByMember_Name(String s, Pageable p);

	//전달된 문자열을 카테고리에서 검색 후 지정한 한페이지 분량 리턴
	Page<BoardEntity> findByCategory(String s, Pageable p);

	// 카테고리와 제목으로 검색
	Page<BoardEntity> findByCategoryAndTitle(String s, String x, Pageable p);

	// 카테고리와 내용으로 검색
	Page<BoardEntity> findByCategoryAndContents(String s, String x, Pageable p);

	// 카테고리와 작성자로 검색
	Page<BoardEntity> findByCategoryAndMember_Name(String s, String x, Pageable p);
}
