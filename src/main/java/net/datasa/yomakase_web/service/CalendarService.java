package net.datasa.yomakase_web.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.yomakase_web.domain.dto.CalendarDTO;
import net.datasa.yomakase_web.domain.entity.CalendarEntity;
import net.datasa.yomakase_web.domain.entity.MemberEntity;
import net.datasa.yomakase_web.repository.CalendarRepository;
import net.datasa.yomakase_web.repository.MemberRepository;
import net.datasa.yomakase_web.security.AuthenticatedUser;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CalendarService {
    private final CalendarRepository calendarRepository;
    private final MemberRepository memberRepository;


    public void dietSave(CalendarDTO calDTO, AuthenticatedUser user) {
        //사용자의 id를 가지고 memberNum을 가져오는 부분 필요
        MemberEntity memberEntity =  memberRepository.findById(user.getUsername()).orElseThrow(null);



        /*CalendarEntity calEntity = CalendarEntity.builder()
                //.member.setMemberId(user.getUsername()) 와 멤버 아이디가 있으니까 아이디가지고 num찾아내는 메소드 만들어서 그걸 꺼내써야되네
                //.member.setId(user.getUsername())
               // .inputDate(calendarDTO.getInputDate())

                .bName(calDTO.getBName())
                .lName(calDTO.getLName())
                .dName(calDTO.getDName())
                .build();*/
        CalendarEntity calEntity = new CalendarEntity();
        calEntity.setInputDate(calDTO.getInputDate());
        calEntity.setMemberNum(memberEntity.getMemberNum());
        calEntity.setBName(calDTO.getBName());
        calEntity.setLName(calDTO.getLName());
        calEntity.setDName(calDTO.getDName());
       /* calEntity.setBName(arr[0]);
        calEntity.setLName(arr[1]);
        calEntity.setDName(arr[2]);
        */
        calendarRepository.save(calEntity);
    }



    
}
