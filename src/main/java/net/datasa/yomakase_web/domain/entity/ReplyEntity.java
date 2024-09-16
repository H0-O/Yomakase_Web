package net.datasa.yomakase_web.domain.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Builder
@Data
@ToString(exclude = "board")
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "reply")
@EntityListeners(AuditingEntityListener.class)
public class ReplyEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "reply_num")
	private Integer replyNum;
	
	// 게시글 정보(외래키로 참조)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "board_num")
	private BoardEntity board;
	
	@ManyToOne(fetch = FetchType.LAZY)	// LAZY: 필요할 때만 가져옴(지연시켜서)
	@JoinColumn(name = "member_num", referencedColumnName = "member_num")
	private MemberEntity member;
	
	@Column(name = "replyContents", nullable = false)
	private String replyContents;
	
	@CreatedDate
	@Column(name = "create_date", columnDefinition = "timestamp default current_timestamp")
	private LocalDateTime creatDate;

}
