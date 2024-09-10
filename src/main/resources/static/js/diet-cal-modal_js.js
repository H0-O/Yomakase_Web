//식단 달력 모달 자바스크립트 파일
const modal = document.getElementById("modal");

function modalOn() {
    modal.style.display = "flex"
}

function isModalOn() {
    return modal.style.display === "flex"
}

function modalOff() {
    modal.style.display = "none"
}

// 특정 버튼을 누르면 모달창이 켜지게 하기
//let btnModal = document.getElementById('btn-modal');


// 모달창의 클로즈(x) 버튼을 누르면 모달창이 꺼지게 하기
const closeBtn = modal.querySelector(".close-area")
closeBtn.addEventListener("click", e => {
    calBackgroundChange();
    modalOff()

})
// 모달창 바깥 영역을 클릭하면 모달창이 꺼지게 하기
modal.addEventListener("click", e => {
    const evTarget = e.target
    if (evTarget.classList.contains("modal-overlay")) {
        calBackgroundChange();
        modalOff()

    }
})
// 모달창이 켜진 상태에서 ESC 버튼을 누르면 모달창이 꺼지게 하기
window.addEventListener("keyup", e => {
    if (isModalOn() && e.key === "Escape") {
        modalOff()
        calBackgroundChange();
    }
})


//달력 배경화면 바꾸는 함수 (모달창이 꺼질 때마다 실행)
let index = 0;
function calBackgroundChange() {
    let path = "url(/img/cal_food_";
    let calBackgroundAddress = [0, 1, 2, 3, 4];
//url(assets/img/calBackgroundAddress[index])
    if(index >= calBackgroundAddress.length){
        index = 0;
    }
    else if(index === 0) {
        index++;
    }
    let bgAddress = path + calBackgroundAddress[index] + '.png)';
    $('.wrap-header').css('background-image', bgAddress);
    index++;
}

//영양소 부분의 모달
let modalNutrient = document.getElementById("modal-nutrient");
function modalNutrientOn() {
    modalNutrient.style.display = "flex"
}

function isModalNutrientOn() {
    return modalNutrient.style.display === "flex"
}

function modalNutrientOff() {
    modalNutrient.style.display = "none"
}

// 특정 버튼을 누르면 모달창이 켜지게 하기
//let btnModal = document.getElementById('btn-modal');


// 모달창의 클로즈(x) 버튼을 누르면 모달창이 꺼지게 하기
const closeNutrientBtn = modalNutrient.querySelector(".close-area")
closeNutrientBtn.addEventListener("click", e => {
    modalNutrientOff()

})
// 모달창 바깥 영역을 클릭하면 모달창이 꺼지게 하기
modalNutrient.addEventListener("click", e => {
    const evTarget = e.target
    if (evTarget.classList.contains("modal-overlay")) {
        modalNutrientOff()

    }
})
// 모달창이 켜진 상태에서 ESC 버튼을 누르면 모달창이 꺼지게 하기
window.addEventListener("keyup", e => {
    if (isModalNutrientOn() && e.key === "Escape") {
        modalNutrientOff()
    }
})


