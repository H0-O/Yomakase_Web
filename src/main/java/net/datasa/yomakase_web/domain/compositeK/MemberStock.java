package net.datasa.yomakase_web.domain.compositeK;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
// 복합키 클래스
public class MemberStock implements Serializable {
	
	private String ingredientName;
	private int memberNum;

}
