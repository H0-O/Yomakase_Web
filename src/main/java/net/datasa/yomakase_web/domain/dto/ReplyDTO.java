package net.datasa.yomakase_web.domain.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReplyDTO {
	Integer replyNum;
	Integer boardNum;
	String memberNum;
	String memberName;
	String replyContents;
	LocalDateTime createDate;

}
