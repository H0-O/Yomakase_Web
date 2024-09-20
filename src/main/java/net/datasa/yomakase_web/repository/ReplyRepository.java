package net.datasa.yomakase_web.repository;

import java.util.List;

import net.datasa.yomakase_web.domain.entity.ReplyEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReplyRepository extends JpaRepository<ReplyEntity, Integer> {
	// 글 번호를 리플 목적으로 조회. 리플번호 순으로 정렬
	// select * from web5_reply where board_num = 1 order by reply_num;
	List<ReplyEntity> findByBoard_BoardNum(Integer n, Sort sort);
}
