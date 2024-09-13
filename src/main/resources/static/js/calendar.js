document.addEventListener('DOMContentLoaded', function(){
    "use strict";   //엄격모드 활성화

    var today = new Date(),
        year = today.getFullYear(),
        month = today.getMonth(),
        monthTag =[1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12],
        day = today.getDate(),
        calendarTable = document.getElementById('calendar'),
        days = calendarTable.getElementsByTagName('td'),
        selectedDay,    //선택된 요일
        setDate,        //선택된 날짜
        daysLen = days.length; //<td>요소의 총 개수. 한달의 일수

    //달력 중앙 '2024년 9월' 부분
    let headDate = document.getElementById('head-date');

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

        pre[0].addEventListener('click', function(){that.preMonth(); });
        next[0].addEventListener('click', function(){that.nextMonth(); });
        reset.addEventListener('click', function(){that.reset(); });

        //페이지의 모든 <td>요소에 클릭 이벤트를 등록하는 반복문
        while(daysLen--) {
            days[daysLen].addEventListener('click', function(){that.clickDay(this)});
            days[daysLen].addEventListener('dblclick', function(){ that.doubleClickDay(this)});
            days[daysLen].addEventListener('mouseover', function(){ that.nutrientScore(this)});
        }
       /* for (let i = 0; i < days.length; i++) {
            days[i].addEventListener('click', function() { that.clickDay(this) });
            days[i].addEventListener('dblclick', function() { that.doubleClickDay(this) });
            days[i].addEventListener('mouseover', function() { that.nutrientScore(this) });
        }
*/

        Calendar.prototype.doubleClickDay = function(td){
            modalOn();  // 모달 처리 함수는 따로 정의해야 함
        }

        Calendar.prototype.nutrientScore = function(td){
            modalNutrientOn();  // 모달 처리 함수는 따로 정의해야 함
        }


        // input 요소 클릭 시 이벤트 전파를 방지
        document.querySelectorAll('input').forEach(input => {
            input.addEventListener('click', function(event) {
                event.stopPropagation();
            });
        });

        // dietBtn(식단 저장하는 버튼) 요소에 대해 이벤트 전파 방지
        let dietBtn = document.getElementById('dietBtn');
            dietBtn.addEventListener('click', function(event) {
                event.stopPropagation();
            });


    }; //draw 함수 end

    /**
     * @param e 사용자가 클릭해서 선택한 날짜(일)
     */
    Calendar.prototype.drawHeader = function(e) {
        var headDay = document.getElementsByClassName('head-day'),
            headMonth = document.getElementsByClassName('head-month');

        e ? headDay[0].innerHTML = e + "일"  : headDay[0].innerHTML = day;
        headMonth[0].innerHTML = `${monthTag[month]}월`;
        headDate.innerHTML =  year + '년 ' + monthTag[month] + '월';

        let clickedDietDay = document.getElementById('clickedDietDay');

        let formattedDate = `${year}-${monthTag[month]}-${e}`;
        console.log(formattedDate);
        clickedDietDay.value = formattedDate;
        clickedDietDay.innerHTML = clickedDietDay.value;
        //console.log(clickedDietDay.value);
        //document.getElementsByClassName('clickedDay').innerHTML = clickedDay;
    }; //drawHeader 함수 end

    Calendar.prototype.drawDays = function() {
        var startDay = new Date(year, month, 1).getDay(),
            nDays = new Date(year, month + 1, 0).getDate(),
            n = startDay;

        for(var k = 0; k < 42; k++) {
            days[k].innerHTML = '';
            days[k].id = '';
            days[k].className = '';
        }

        for(var i = 1; i <= nDays; i++) {
            days[n].innerHTML = i;
            n++;
        }

        for(var j = 0; j < 42; j++) {
            if(days[j].innerHTML === ""){
                days[j].id = "disabled";
            } else if(j === day + startDay - 1){
                if((this.options && (month === setDate.getMonth()) && (year === setDate.getFullYear())) || (!this.options && (month === today.getMonth()) && (year===today.getFullYear()))){
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

    Calendar.prototype.clickDay = function(o) {
      /*  var selected = document.getElementsByClassName("selected"),
            len = selected.length;
        if(len !== 0){
            selected[0].className = "";
        }
        o.className = "selected";*/
        selectedDay = new Date(year, month, o.innerHTML);      //new Date(연, 월 ,일);
        this.drawHeader(o.innerHTML);
        this.setCookie('selected_day', 1);
    };  //clickDay 함수 end

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
            var sets = this.options.split('-');
            setDate = new Date(sets[0], sets[1]-1, sets[2]);
            day = setDate.getDate();
            year = setDate.getFullYear();
            month = setDate.getMonth();
        }
    };

    Calendar.prototype.reset = function() {
        month = today.getMonth();
        year = today.getFullYear();
        day = today.getDate();
        this.options = undefined;
        this.drawDays();
    };

    Calendar.prototype.setCookie = function(name, expiredays){
        var date = new Date();
        date.setTime(date.getTime() + (expiredays * 24 * 60 * 60 * 1000));
        var expires = "; expires=" + date.toUTCString();
        document.cookie = name + "=" + selectedDay + expires + "; path=/";
    };

    Calendar.prototype.getCookie = function(name) {
        var nameEQ = name + "=";
        var ca = document.cookie.split(';');
        for(var i=0; i < ca.length; i++) {
            var c = ca[i];
            while (c.charAt(0) == ' ') c = c.substring(1, c.length);
            if (c.indexOf(nameEQ) == 0) selectedDay = new Date(c.substring(nameEQ.length, c.length));
        }
    };

    var calendar = new Calendar();

}, false);  //addEventListener의 함수 end
