(function($) {  //제이쿼리와 충돌을 피하기 위해 제이쿼리를 $로 전달받는 즉시 실행함수

    "use strict";   //엄격모드 활성화

    document.addEventListener('DOMContentLoaded', function(){
        var today = new Date(),
            year = today.getFullYear(),
            month = today.getMonth(),
            monthTag =["1월","2월","3월","4월","5월","6월","7월","8월","9월","10월","11월","12월"],
            day = today.getDate(),
            days = document.getElementsByTagName('td'),
            selectedDay,    //선택된 요일
            setDate,        //선택된 날짜
            daysLen = days.length; //<td>요소의 총 개수. 한달의 일수

        //달력 중앙 '2024년 9월' 부분
        let headDate = document.getElementById('head-date');

        //모달 변수 설정
        let modal = document.getElementById("modal");

        let modalNutrient = document.getElementById("modal-nutrient");

        // options should like '2014-01-01'
        //Calendar 객체 생성자 함수 정의
        function Calendar(selector, options) {
            this.options = options;
            this.draw();
        }

        /**
         * 달력을 계속 새로 그리는 함수
         */
        Calendar.prototype.draw  = function() {
            this.getCookie('selected_day');
            this.getOptions();
            this.drawDays();
            var that = this,
                //리셋 버튼
                reset = document.getElementById('reset'),
                //이전 버튼
                pre = document.getElementsByClassName('pre-button'),
                //다음 버튼
                next = document.getElementsByClassName('next-button');

            //that은 함수가 속한 객체를 뜻함. 여기서는 외부에서 정의한 Calendar 객체에 접근
            //that말고 self, me 등으로도 표현 가능
            pre[0].addEventListener('click', function(){that.preMonth(); });
            next[0].addEventListener('click', function(){that.nextMonth(); });
            reset.addEventListener('click', function(){that.reset(); });

            //페이지의 모든 <td>요소에 클릭 이벤트를 등록하는 반복문
            while(daysLen--) {
                days[daysLen].addEventListener('click', function(){that.clickDay(this)});
                days[daysLen].addEventListener('dblclick', function(){ that.doubleClickDay(this)});
                days[daysLen].addEventListener('mouseover', function(){ that.nutrientScore(this)});
            }

            Calendar.prototype.doubleClickDay = function(td){
                modalOn();
            }

            Calendar.prototype.nutrientScore = function(td){
                modalNutrientOn();
            }

        }; //draw 함수 end

        /**
         *
         * @param e 사용자가 클릭해서 선택한 날짜
         */
        Calendar.prototype.drawHeader = function(e) {
            var headDay = document.getElementsByClassName('head-day'),
                headMonth = document.getElementsByClassName('head-month');
            //headDate = document.getElementsByClassName('head-date');

            //사용자가 선택한 날짜가 있을 때 그 날짜를 headDay에 대입
            //그렇지 않으면 오늘 날짜를 headDay에 대입
            e?headDay[0].innerHTML = e + "일"  : headDay[0].innerHTML = day;
            headMonth[0].innerHTML = monthTag[month];

            //document.getElementById('head-left-date').innerHTML = monthTag[month] + headDay[0].innerHTML + "일";
            headDate.innerHTML =  year + '년 ' + monthTag[month];



        }; //drawHeader 함수 end

        Calendar.prototype.drawDays = function() {
            //선택된 연도와 월의 첫번째 날짜의 요일
            var startDay = new Date(year, month, 1).getDay(),   //getDay() : 0(일요일)부터 6(토요일)까지의 숫자 반환
                //선택된 월의 마지막 날짜
                nDays = new Date(year, month + 1, 0).getDate(), //getDate() : 날짜를 정수로 반환
                //달력에서 날짜 채울 시작 위치
                n = startDay;

            //각 날짜(<td>요소) 초기화
            for(var k = 0; k <42; k++) {
                days[k].innerHTML = '';
                days[k].id = '';
                days[k].className = '';
            }

            //선택된 월의 날짜 그리기
            for(var i  = 1; i <= nDays ; i++) {
                days[n].innerHTML = i;
                n++;
            }

            for(var j = 0; j < 42; j++) {
                if(days[j].innerHTML === ""){
                    //비어있는 날짜 비활성화
                    days[j].id = "disabled";
                } else if(j === day + startDay - 1){
                    if((this.options && (month === setDate.getMonth()) && (year === setDate.getFullYear())) || (!this.options && (month === today.getMonth())&&(year===today.getFullYear()))){
                        this.drawHeader(day);
                        days[j].id = "today";
                    }
                }
                if(selectedDay){
                    if((j === selectedDay.getDate() + startDay - 1)&&(month === selectedDay.getMonth())&&(year === selectedDay.getFullYear())){
                        days[j].className = "selected";
                        this.drawHeader(selectedDay.getDate());
                    }
                }
            }
        };  //drawDays 함수 end


        /**
         * 날짜 클릭 메서드
         * @param o 클릭된 <td>요소
         */
        Calendar.prototype.clickDay = function(o) {
            var selected = document.getElementsByClassName("selected"),
                len = selected.length;
            if(len !== 0){
                selected[0].className = "";
            }
            o.className = "selected";
            selectedDay = new Date(year, month, o.innerHTML);
            //console.log(selectedDay);   //Sat Sep 07 2024 00:00:00 GMT+0900 (한국 표준시)  //클릭한 날짜로 나옴
            this.drawHeader(o.innerHTML);
            this.setCookie('selected_day', 1);

            var headDay = document.getElementsByClassName('head-day');

            // 더블 클릭 이벤트 리스너 등록
            //Calendar.prototype.dateDoubleClick(o);
            // function handleDoubleClick() {
            //     alert('모달창 부분');
            //     o.removeEventListener('dblclick', handleDoubleClick); // 이벤트 리스너 제거
            // }

            // 클릭된 요소에 대해 더블 클릭 이벤트 리스너 등록
            //o.addEventListener('dblclick', handleDoubleClick, { once: true });

        };  //clickDay 함수 end

        // // 위 clickDay함수에넣으니까 클릭 이벤트가 중북되면서 alert가 두번씩 뜸
        // Calendar.prototype.dateDoubleClick = function(o){
        //
        //     //날짜 클릭 이벤트 리스너 (이미 선택된 날짜를 한번더 눌려서 더블클릭처럼 씀)
        //     o.addEventListener('click', function(){
        //         alert('새로운 이벤트 확인');
        //
        //     })
        // }

        Calendar.prototype.preMonth = function() {
            if(month < 1){
                month = 11;
                year = year - 1;
            }else{
                month = month - 1;
            }
            this.drawHeader(1);
            this.drawDays();
        };

        Calendar.prototype.nextMonth = function() {
            if(month >= 11){
                month = 0;
                year =  year + 1;
            }else{
                month = month + 1;
            }
            this.drawHeader(1);
            this.drawDays();
        };

        Calendar.prototype.getOptions = function() {
            if(this.options){
                //this.options르 '-'를 기준으로 분리하여 sets 배열에 저장
                var sets = this.options.split('-');
                // new Date(연도, 월, 날짜)를 setDate에 저장
                setDate = new Date(sets[0], sets[1]-1, sets[2]);
                day = setDate.getDate();        //날짜
                year = setDate.getFullYear();   //연도
                month = setDate.getMonth();     //월 (0부터 시작해서 실제 월보다 1 빼줘야 함)
            }
        };

        Calendar.prototype.reset = function() {
            month = today.getMonth();
            year = today.getFullYear();
            day = today.getDate();
            this.options = undefined;
            this.drawDays();
        };

        //쿠키 설정 함수 function(쿠키 이름, 쿠키의 유효기간){}
        Calendar.prototype.setCookie = function(name, expiredays){
            if(expiredays) {
                var date = new Date();  //현재시간
                date.setTime(date.getTime() + (expiredays*24*60*60*1000));  //유효기간 7일
                //그리니치 표준시 형식으로 변환하여 만료시간 저장
                var expires = "; expires=" +date.toGMTString();
            }else{
                var expires = "";
            }
            document.cookie = name + "=" + selectedDay + expires + "; path=/";
        };

        Calendar.prototype.getCookie = function(name) {
            if(document.cookie.length){
                var arrCookie  = document.cookie.split(';'),
                    nameEQ = name + "=";
                for(var i = 0, cLen = arrCookie.length; i < cLen; i++) {
                    var c = arrCookie[i];
                    while (c.charAt(0)==' ') {
                        c = c.substring(1,c.length);

                    }
                    if (c.indexOf(nameEQ) === 0) {
                        selectedDay =  new Date(c.substring(nameEQ.length, c.length));
                    }
                }
            }
        };
        //달력 객체가 쿠키를 설정하고 읽어올 수 있음
        var calendar = new Calendar();


    }, false);  //addEventListener의 함수 end

})(jQuery);

