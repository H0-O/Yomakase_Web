package net.datasa.yomakase_web.domain.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "board")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class BoardEntity {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_num")
    private Integer boardNum;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "member_num", nullable = false)
    private MemberEntity member;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "category", nullable = false, length = 10)
    private String category;

    @Column(name = "contents", nullable = false, columnDefinition = "MEDIUMTEXT")
    private String contents;

    @CreatedDate
    @Column(name = "create_date", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createDate;
    
    @LastModifiedDate
    @Column(name = "update_date", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updateDate;

    @Column(name = "file_name", length = 100)
    private String fileName;

    @Column(name = "recommended", nullable = false, columnDefinition = "int DEFAULT 0")
    private Integer recommended = 0;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<ReplyEntity> replyList;

}
