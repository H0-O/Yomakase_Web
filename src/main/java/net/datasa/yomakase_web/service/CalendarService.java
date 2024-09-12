package net.datasa.yomakase_web.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.yomakase_web.domain.dto.CalendarDTO;
import net.datasa.yomakase_web.domain.entity.CalendarEntity;
import net.datasa.yomakase_web.domain.entity.MemberEntity;
import net.datasa.yomakase_web.repository.CalendarRepository;
import net.datasa.yomakase_web.security.AuthenticatedUser;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CalendarService {
    private final CalendarRepository calendarRepository;

    /**
     * 식단 저장 메소드
     * @param calendarDTO   사용자 선택 날짜, 아침 메뉴, 점심 메뉴, 저녁 메뉴
     */
    public void dietSave(String[] arr) {
        /* MemberEntity memberEntity = new MemberEntity();
        memberEntity.setId(user.getUsername());*/

/*        CalendarEntity calEntity = CalendarEntity.builder()
                //.member.setMemberId(user.getUsername()) 와 멤버 아이디가 있으니까 아이디가지고 num찾아내는 메소드 만들어서 그걸 꺼내써야되네
                //.member.setId(user.getUsername())
               // .inputDate(calendarDTO.getInputDate())
                .bName(arr[0])
                .lName(arr[1])
                .dName(arr[2])
                .build();*/

        CalendarEntity calEntity = new CalendarEntity();
        calEntity.setBName(arr[0]);
        calEntity.setLName(arr[1]);
        calEntity.setDName(arr[2]);
        calendarRepository.save(calEntity);
    }
    
    
}
