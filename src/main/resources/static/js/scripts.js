/*!
* Start Bootstrap - Resume v7.0.6 (https://startbootstrap.com/theme/resume)
* Copyright 2013-2023 Start Bootstrap
* Licensed under MIT (https://github.com/StartBootstrap/startbootstrap-resume/blob/master/LICENSE)
*/
//
// Scripts
// 

window.addEventListener('DOMContentLoaded', event => {

    // Activate Bootstrap scrollspy on the main nav element
    const sideNav = document.body.querySelector('#sideNav');
    if (sideNav) {
        new bootstrap.ScrollSpy(document.body, {
            target: '#sideNav',
            rootMargin: '0px 0px -40%',
        });
    }

    // Collapse responsive navbar when toggler is visible
    const navbarToggler = document.body.querySelector('.navbar-toggler');
    const responsiveNavItems = [].slice.call(
        document.querySelectorAll('#navbarResponsive .nav-link')
    );
    responsiveNavItems.map(function (responsiveNavItem) {
        responsiveNavItem.addEventListener('click', () => {
            if (window.getComputedStyle(navbarToggler).display !== 'none') {
                navbarToggler.click();
            }
        });
    });

    // My pages's JS
    function openTab(tabName) {
        // jQuery의 hide() 대신 모든 .mypagesContent 요소를 숨기기
        const tabContents = document.querySelectorAll(".mypagesContent");
        tabContents.forEach(function(content) {
            content.style.display = "none";
        });

        // jQuery의 show() 대신 선택된 탭을 보여주기
        const selectedTab = document.getElementById(tabName);
        if (selectedTab) {
            selectedTab.style.display = "block";
        }
    }

    // 기본으로 첫 번째 탭을 엽니다.
    document.addEventListener('DOMContentLoaded', function() {
        openTab("recipe");

    });

});

$('.nav-link.js-scroll-trigger').click(function(){
    // alert("1");
    $('.navbar-nav a').removeClass('active');
    $(this).addClass('active');
})
