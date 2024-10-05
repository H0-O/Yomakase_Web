document.addEventListener('DOMContentLoaded', function(){
    "use strict";   //엄격모드 활성화

    //달력 관련 변수
    var today = new Date(),
        year = today.getFullYear(),
        month = today.getMonth(),
        monthTag =[1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12],
        day = today.getDate(),
        calendarTable = document.getElementById('calendarTable'),
        days = calendarTable.getElementsByTagName('td'),
        selectedDay,    //사용자가 선택한 날짜 정보 : Sun Oct 27 2024 00:00:00 GMT+0900 (한국 표준시)
        setDate,
        daysLen = days.length, //<td>요소의 총 개수. 한달의 일수
        headDate = document.getElementById('head-date');    //달력 중앙 '2024년 9월' 부분

    let clickedDietDay = document.getElementsByClassName('clickedDietDay')[0];

    //사용자 인증 정보 (일반, 구독자 계정 구분용)
    const userRole = document.getElementById('userRole').value;
    //버튼 선택 영역
    let btnDiv = document.getElementById('cal-title-btn');
    //식단 버튼
    let dietCalBtn  = document.getElementById('dietCalBtn');
    //영양소 버튼
    let nutrientCalBtn = document.getElementById('nutrientCalBtn');
    //버튼 활성화 확인
    let btnActiveCheck = true;  //기본적으로 식단 버튼 클릭 상태일 때 true, 영양소 버튼 상태일 때 false
    //식단 조회 영역
    let dietList = document.getElementById('diet-list');
    //영양소 조회 영역
    let nutrientList = document.getElementById('nutrient-list');
    //추천 식단 조회 영역
    let recomTable = document.getElementById('recomTable');

    //초기화면 확인
    let firstPageCheck = true;  //초기화면일 때 true, 사용자가 클릭을 했다면 false


    /**
     * Calendar 객체 생성자 함수 정의
     * @param selector
     * @param options
     * @constructor
     */
    function Calendar(selector, options) {
        this.options = options;
        this.draw();
    }


    //버튼 선택 영역, 추천 식단 영역 : 구독 사용자에게만 보이도록 함
    if(userRole === 'ROLE_SUBSCRIBER'){
        btnDiv.style.visibility = 'visible';
        recomTable.style.visibility = 'visible';
    } else{
        btnDiv.style.visibility = 'hidden';
       recomTable.style.visibility = 'hidden';
    }


    function btnActive(){
        dietCalBtn.style.backgroundColor = '#FFB524';
        nutrientCalBtn.addEventListener('click', function (){
            btnActiveCheck = false;
            dietCalBtn.style.backgroundColor = '';
            nutrientCalBtn.style.backgroundColor = '#FFB524';
            document.getElementById('nutrientCalTitle').style.display = 'block';
            document.getElementById('dietCalTitle').style.display = 'none';
            nutrientView(selectedDay.getDate());
        });
        dietCalBtn.addEventListener('click', function (){
            btnActiveCheck = true;
            nutrientCalBtn.style.backgroundColor = '';
            dietCalBtn.style.backgroundColor = '#FFB524';
            document.getElementById('dietCalTitle').style.display = 'block';
            document.getElementById('nutrientCalTitle').style.display = 'none';
            dietView(selectedDay.getDate());
        })

    }
    btnActive();



    /**
     * 달력을 계속 새로 그리는 함수
     */
    Calendar.prototype.draw  = function() {
        this.getCookie('selected_day');
        this.getOptions();
        //this.drawHeader();
        this.drawDays();

        var that = this,
            //리셋 버튼
            /*reset = document.getElementById('reset'),*/
            //이전 버튼
            pre = document.getElementsByClassName('pre-button'),
            //다음 버튼
            next = document.getElementsByClassName('next-button');

        pre[0].addEventListener('click', function () {
            that.preMonth();
        });
        next[0].addEventListener('click', function () {
            that.nextMonth();
        });
        /*reset.addEventListener('click', function () {
            that.reset();
        });*/


        //페이지의 모든 <td>요소에 이벤트를 등록하는 반복문
        while (daysLen--) {
            days[daysLen].addEventListener('click', function () {
                 const dayNumber = parseInt(this.innerHTML, 10); // 숫자로 변환
                console.log(dayNumber);
                if(btnActiveCheck) {
                    dietView(dayNumber);
                } else{
                    console.log(this)   //클릭한 td태그
                    nutrientView(dayNumber);
                }
            });

            /*nutrientCalBtn.addEventListener('click', function (){
                days[daysLen].addEventListener('click', function(){

                })
                console.log(day[daysLen])
                nutrientView();
            });*/

            days[daysLen].addEventListener('dblclick', function () {
                if(btnActiveCheck){
                    modalOn();
                }
            });

        }

    }
    dietView(day);
    document.addEventListener('click', function(event) {
       /* // 예: 특정 클래스가 있는 요소는 제외
        if (event.target.classList.contains('exclude-class')) {
            return; // 해당 요소는 무시
        }*/
        firstPageCheck = false;
    });

    /**
     * 식단 조회 함수
     * @param td
     */
    function dietView(td){
        //alert('식단 입력')
        console.log('실행됨'+td);
        console.log(day);

        selectedDay = new Date(year, month, td);
        console.log(selectedDay);

        if(!firstPageCheck){
            calendar.drawHeader(td);
            calendar.setCookie('selected_day', 1);
        }

        $.ajax({
            url : '/cal/dietList',
            type : 'post',
            data : {selectedDay: selectedDay.toLocaleDateString('en-CA')} ,    // o.toISOString().split('T')[0] T를 기준으로 자르고 첫번째 배열의 요소를 선택하는 방법. 이렇게 하니까 날짜가 선택한 날짜보다 하루 적게 나옴.
            dataType : 'json',
            success : function(calDTO){
                nutrientList.style.display = 'none';
                dietList.style.display = 'block';
                document.getElementById('diet-list-msg').style.display = 'none';
                //console.log(calDTO); //{"id":"alpha@yomakase.test","memberNum":1,"inputDate":"2024-08-02","dname":"손말이고기","lname":"부대찌개","bname":"맥심 커피"}
                if(!calDTO.bname && !calDTO.lname && !calDTO.dname){
                    dietList.style.display = 'none';
                    document.getElementById('diet-list-msg').style.display = 'block';

                } else{
                    let listBName = dietList.getElementsByTagName('td')[1];
                    let listLName = dietList.getElementsByTagName('td')[3];
                    let listDName = dietList.getElementsByTagName('td')[5];
                    listBName.innerHTML = calDTO.bname;
                    listLName.innerHTML = calDTO.lname;
                    listDName.innerHTML = calDTO.dname;

                    //모달에도 해당 날짜의 식단을 보여줌
                    document.getElementById('bName').value = calDTO.bname;
                    document.getElementById('lName').value = calDTO.lname;
                    document.getElementById('dName').value = calDTO.dname;
                    recomTable.getElementsByTagName('td')[0].innerHTML = "<p>입력된 식단을 바탕으로 권장 식단을 추천해드립니다.</p>" +
                                                                                    "<p>영양소 달력에서 날짜를 클릭해 주세요.</p>";
                }
            },
            error : function (){
                dietList.style.display = 'none';
                document.getElementById('diet-list-msg').style.display = 'block';
            }

        })
        document.getElementById('bName').value = "";
        document.getElementById('lName').value = "";
        document.getElementById('dName').value = "";
    }


    /**
     * 영양소 조회 함수
     * @param td
     */
    function nutrientView(td){
        //alert('영양소 입력');

        selectedDay = new Date(year, month, td);

        calendar.drawHeader(td);
        calendar.setCookie('selected_day', 1);

        $.ajax({
            url : '/cal/nutrientList',
            type : 'post',
            data : {selectedDay: selectedDay.toLocaleDateString('en-CA')} ,    // o.toISOString().split('T')[0] T를 기준으로 자르고 첫번째 배열의 요소를 선택하는 방법. 이렇게 하니까 날짜가 선택한 날짜보다 하루 적게 나옴.
            dataType : 'json',
            success : function(calDTO){
                dietList.style.display = 'none';
                nutrientList.style.display = 'block';
                document.getElementById('diet-list-msg').style.display = 'none';
                if(!calDTO.totalKcal && !calDTO.tooMuch && !calDTO.lack && !calDTO.recom){
                    dietList.style.display = 'none';
                    nutrientList.style.display = 'none';
                    document.getElementById('diet-list-msg').style.display = 'block';
                } else {
                    dietList.style.display = 'none';
                    nutrientList.style.display = 'block';
                    document.getElementById('diet-list-msg').style.display = 'none';
                    console.log(calDTO);

                    let listTotalKcal = nutrientList.getElementsByTagName('td')[0];
                    let listTooMuch = nutrientList.getElementsByTagName('td')[1];
                    let listLack = nutrientList.getElementsByTagName('td')[2];
                    //let listRecom = nutrientList.getElementsByTagName('td')[7];
                    //let listScore = nutrientModalContent.getElementsByTagName('td')[1];
                    listTotalKcal.innerHTML = calDTO.totalKcal;
                    listTooMuch.style.color = 'blue';
                    listTooMuch.innerHTML = calDTO.tooMuch;
                    listLack.style.color = 'red';
                    listLack.innerHTML = calDTO.lack;
                    nutrientList.getElementsByTagName('td')[3].innerHTML = calDTO.score;
                    let recomSplit = calDTO.recom.split(/\[|\]/);
                    //split(/[,;:]/)
                    console.log(recomSplit[1]);

                    let recomHighlight = recomSplit[1].replace(recomSplit[1], `<span style="background-color: yellow;">${recomSplit[1]}</span>`);

                    //recomTable.getElementsByTagName('td')[0].innerHTML = calDTO.recom;
                    recomTable.getElementsByTagName('td')[0].innerHTML = recomSplit[0]+recomHighlight+recomSplit[2];

                    document.getElementById('diet-list-msg').style.display = 'none';
                }
            },
            error : function (){
                dietList.style.display = 'none';
                document.getElementById('diet-list-msg').style.display = 'block';
            }
        })

        recomTable.getElementsByTagName('td')[0].innerHTML = "<p>입력된 식단을 바탕으로 권장 식단을 추천해드립니다.</p>" +
                                                                        "<p>영양소 달력에서 날짜를 클릭해 주세요.</p>";
    }


    /**
     * @param e 사용자가 클릭해서 선택한 날짜(일), 타입 숫자
     */
    Calendar.prototype.drawHeader = function(e) {
        var headDay = document.getElementsByClassName('head-day'),
            headMonth = document.getElementsByClassName('head-month');

        e ? headDay[0].innerHTML = e + "일" : headDay[0].innerHTML = day + "일";
        /*if(!e){
            headDay[0].innerHTML = day
        } else {
            headDay[0].innerHTML = e + "일"
        }*/
        //headDay[0].innerHTML = `${e}일`;
        headMonth[0].innerHTML = `${monthTag[month]}월`;
        headDate.innerHTML = year + '년 ' + monthTag[month] + '월';
        if (selectedDay){
            let formattedDay = selectedDay.toLocaleDateString('en-CA')
            //console.log(selectedDay);
            //console.log(formattedDay); //2024-08-31 선택한 날짜로 잘 나옴
            clickedDietDay.innerHTML = formattedDay;
        } else {
            console.warn('선택된 날짜가 정의되지 않음')
            selectedDay = new Date();
        }

    }; //drawHeader 함수 end


    /**
     * 달력의 날짜를 계산하고 그리는 함수
     */
    Calendar.prototype.drawDays = function () {
        var startDay = new Date(year, month, 1).getDay(),
            nDays = new Date(year, month + 1, 0).getDate(), //달의 마지막날
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
                days[j].className = "disabledDay";
            } else if(j === day + startDay - 1){
                if((this.options && (month === setDate.getMonth()) && (year === setDate.getFullYear())) || (!this.options && (month === today.getMonth()) && (year===today.getFullYear()))){
                    this.drawHeader(day);
                    days[j].id = "today";
                   /* console.log(day);
                    dietView(day);*/

                }
            }
            if(selectedDay){
                if((j === selectedDay.getDate() + startDay - 1)&&(month === selectedDay.getMonth())&&(year === selectedDay.getFullYear())){
                    days[j].className = "selected";
                    this.drawHeader(selectedDay.getDate());
                }
            }

        }

    } //drawDays 함수 end




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

    /*Calendar.prototype.reset = function() {
        month = today.getMonth();
        year = today.getFullYear();
        day = today.getDate();
        this.options = undefined;
        this.drawDays();
    };*/

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

    var calendar = new Calendar();  //해당 부분 필수! 없으면 달력이 그려지지 않음

}, false);  //전체 문서 addEventListener의 함수 end