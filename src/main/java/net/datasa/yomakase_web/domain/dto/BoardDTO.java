package net.datasa.yomakase_web.domain.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoardDTO {
	
	int boardNum;
	int memberNum;
	String name;
	String title;
	String category;
	String contents;
	LocalDateTime createDate;
	LocalDateTime updateDate;
	String fileName;
	Integer recommended;
	
	// 리플 목록
	//private List<ReplyDTO> replyList;	

}
