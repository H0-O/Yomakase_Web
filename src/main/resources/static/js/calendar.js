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
        selectedDay,    //선택된 요일
        setDate,        //선택된 날짜
        daysLen = days.length; //<td>요소의 총 개수. 한달의 일수


    let mouseoverDay = year + '-' + monthTag[month] + '-' + this.innerHTML;
    //달력 중앙 '2024년 9월' 부분
    let headDate = document.getElementById('head-date');

    //식단 조회 영역
    let dietList = document.getElementById('diet-list');
    //영양소 조회 영역
    let nutrientList = document.getElementById('nutrient-list');

    //영양소 모달
    let nutrientModal = document.getElementById("nutrient-modal");

    //식단 달력, 영양소 달력 버튼
    let nutrientCalBtn = document.getElementById('nutrientCalBtn');
    let dietCalBtn  = document.getElementById('dietCalBtn');

    //식단 달력, 영양소 달력 제목
    let nutrientCalTitle = document.getElementById('nutrientCalTitle');
    let dietCalTitle = document.getElementById('dietCalTitle');

    //달력 초기화면에서 모달이 안 나오도록 함
    document.getElementById("diet-modal").style.display = 'none';
    nutrientModal.style.display = 'none';

    // console.log(nutrientCalBtn, dietCalBtn);  //일반 회원이 로그인했을 때는 버튼이 null 상태가 됨
    if (dietCalBtn === null || nutrientCalBtn === null) {
        dietCalBtn =  '<button id="dietCalBtn">식단</button>';
        nutrientCalBtn =  '<button id="nutrientCalBtn">식단</button>';
    } else {
        dietCalBtn.addEventListener('click', function()  {
            if (nutrientCalTitle.style.display === 'block' || dietCalTitle.style.display !== 'block') {
                dietCalTitle.style.display = 'block';
                nutrientCalTitle.style.display = 'none';
                dietCalBtn.style.backgroundColor = '#198754';
                nutrientCalBtn.style.backgroundColor = '';
            }
        });

        nutrientCalBtn.addEventListener('click', function()  {
            if (dietCalTitle.style.display === 'block' || nutrientCalTitle.style.display !== 'black') {
                nutrientCalTitle.style.display = 'block';
                dietCalTitle.style.display = 'none';
                nutrientCalBtn.style.backgroundColor = '#198754';
                dietCalBtn.style.backgroundColor = '';
            }
        });
    }


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
        while (daysLen--) {
            days[daysLen].addEventListener('click', function () {
                that.clickDay(this)
            })
            days[daysLen].addEventListener('dblclick', function () {
                that.doubleClickDay(this)
            });
            if(nutrientModal !== null) {
                days[daysLen].addEventListener('mouseover', function () {
                    that.nutrientScore(this)
                });
                days[daysLen].addEventListener('mouseleave', function () {
                    that.nutrientScoreOff(this)
                    let nutrientModalContent = document.getElementById('nutrient-modal-table');
                    /*let listScore = nutrientModalContent.getElementsByTagName('td')[1];
                    listScore.append();*/
                    nutrientList.style.display = 'none';

                });
                }
        }

        Calendar.prototype.doubleClickDay = function(td){
            modalOn();  // 모달 처리 함수는 따로 정의해야 함
        }

        Calendar.prototype.nutrientScore = function(td){
            modalNutrientOn();  // 모달 처리 함수는 따로 정의해야 함
        }

        Calendar.prototype.nutrientScoreOff = function (td) {
            modalNutrientOff();  // 모달 처리 함수는 따로 정의
        }

    }; //draw 함수 end


    /**
     * @param e 사용자가 클릭해서 선택한 날짜(일)
     */
    Calendar.prototype.drawHeader = function(e) {
        var headDay = document.getElementsByClassName('head-day'),
            headMonth = document.getElementsByClassName('head-month');

        e ? headDay[0].innerHTML = e + "일" : headDay[0].innerHTML = day;
        headMonth[0].innerHTML = `${monthTag[month]}월`;
        headDate.innerHTML = year + '년 ' + monthTag[month] + '월';

        let clickedDietDay = document.getElementsByClassName('clickedDietDay')[0];

        let formattedDate = `${year}-${monthTag[month]}-${e}`;
        //console.log(formattedDate); //2024-08-31 선택한 날짜로 잘 나옴
        clickedDietDay.innerHTML = formattedDate;

    }; //drawHeader 함수 end


    //달력 날짜 계산하고 그리는 함수
    Calendar.prototype.drawDays = function () {
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

            //마우스 오버 시 해당 일자 표시 (영양소 모달용)
            days[j].addEventListener('mouseover', function () {
                if (this.innerHTML !== "") {
                    mouseoverDay = year + '-' + monthTag[month] + '-' + this.innerHTML;
                    let parts = mouseoverDay.split('-');    // - 를 기준으로 나눠서 배열을 만듦
                    let formattedDate = `${parts[0]}-${parts[1].padStart(2,'0')}-${parts[2].padStart(2,'0')}`;
                    //console.log(mouseoverDay);  //2024-9-28  백에서 처리하는 날짜 형식과 맞지 않음
                    //console.log(formattedDate);   //2024-09-30
                    let clickedNutrientDay = document.getElementsByClassName('clickedDietDay')[1];
                    clickedNutrientDay.innerHTML = mouseoverDay;

                    let nutrientModalContent = document.getElementById('nutrient-modal-table');
                    let listScore = nutrientModalContent.getElementsByTagName('td')[1];

                    $.ajax({
                        url: '/cal/nutrientScore',
                        type: 'post',
                        data: {mouseoverDay : formattedDate },  // o.toISOString().split('T')[0] T를 기준으로 자르고 첫번째 배열의 요소를 선택하는 방법. 이렇게 하니까 날짜가 선택한 날짜보다 하루 적게 나옴.
                        dataType: 'json',
                        success: function (calDTO) {
                            dietList.style.display = 'none';
                            nutrientList.style.display = 'block';
                            document.getElementById('diet-list-msg').style.display = 'none';
                            console.log(calDTO); //

                            listScore.innerHTML = `${calDTO.score}점`;
                        },
                        error: function () {
                            dietList.style.display = 'none';
                            document.getElementById('diet-list-msg').style.display = 'block';
                        }
                    })
                    nutrientModalContent.getElementsByTagName('td')[1].innerHTML = "";
                    listScore.innerText = "식단 달력에서 식단을 입력해 주세요.";


                }

            });


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
        /*if (!this.isSameDay(selectedDay, o.innerHTML)) {
            this.dietView(o);
        }*/
        // console.log("clickDay함수에서 설정한 seletedDay : ", selectedDay);
        this.drawHeader(o.innerHTML);
        this.setCookie('selected_day', 1);

        if(nutrientCalTitle.style.display === 'block' || dietCalTitle.style.display === 'none'){
            this.nutrientView(selectedDay);
        } else if (nutrientCalTitle.style.display === 'none' || nutrientCalTitle.style.display === ''){
            this.dietView(selectedDay);
        }

    };  //clickDay 함수 end


    // 저장된 식단 확인
    Calendar.prototype.dietView = function(o){
        $.ajax({
            url : '/cal/dietList',
            type : 'post',
            data : {selectedDay: o.toLocaleDateString('en-CA')} ,    // o.toISOString().split('T')[0] T를 기준으로 자르고 첫번째 배열의 요소를 선택하는 방법. 이렇게 하니까 날짜가 선택한 날짜보다 하루 적게 나옴.
            dataType : 'json',
            success : function(calDTO){
                nutrientList.style.display = 'none';
                dietList.style.display = 'block';
                document.getElementById('diet-list-msg').style.display = 'none';
                //console.log(calDTO); //{"id":"alpha@yomakase.test","memberNum":1,"inputDate":"2024-08-02","dname":"손말이고기","lname":"부대찌개","bname":"맥심 커피"}
                let listBName = dietList.getElementsByTagName('td')[1];
                let listLName = dietList.getElementsByTagName('td')[3];
                let listDName = dietList.getElementsByTagName('td')[5];
                listBName.innerText = calDTO.bname;
                listLName.innerText = calDTO.lname;
                listDName.innerText = calDTO.dname;
            },
            error : function (){
                dietList.style.display = 'none';
                document.getElementById('diet-list-msg').style.display = 'block';
            }
        })
    }

    //저장된 식단을 바탕으로 한 영양소 출력
    Calendar.prototype.nutrientView = function(o){
        //alert("영양소 확인");

        $.ajax({
            url : '/cal/nutrientList',
            type : 'post',
            data : {selectedDay: o.toLocaleDateString('en-CA')} ,    // o.toISOString().split('T')[0] T를 기준으로 자르고 첫번째 배열의 요소를 선택하는 방법. 이렇게 하니까 날짜가 선택한 날짜보다 하루 적게 나옴.
            dataType : 'json',
            success : function(calDTO){
                dietList.style.display = 'none';
                nutrientList.style.display = 'block';
                document.getElementById('diet-list-msg').style.display = 'none';
                console.log(calDTO); //
                let listTotalKcal = nutrientList.getElementsByTagName('td')[1];
                let listTooMuch = nutrientList.getElementsByTagName('td')[3];
                let listLack = nutrientList.getElementsByTagName('td')[5];
                let listRecom = nutrientList.getElementsByTagName('td')[7];
                //let listScore = nutrientModalContent.getElementsByTagName('td')[1];
                listTotalKcal.innerHTML = calDTO.totalKcal;
                listTooMuch.innerHTML = calDTO.tooMuch;
                listLack.innerHTML = calDTO.lack;
                listRecom.innerHTML = calDTO.recom;
                //listScore.innerText = calDTO.score + "점";
                listTotalKcal.append();
                listTooMuch.append();
                listLack.append();
                listRecom.append();
                document.getElementById('diet-list-msg').style.display = 'none';
            },
            error : function (){
                dietList.style.display = 'none';
                document.getElementById('diet-list-msg').style.display = 'block';
            }
        })
        document.getElementById('diet-list-msg').style.display = 'none';
    }


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
