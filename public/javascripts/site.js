var initStyle = function() {

    // CSS3 rounded corners / shadows
    $("div#header li.active a").css({ '-moz-border-radius': '6px', '-webkit-border-radius': '6px', 'border-radius': '6px' });
    $("div.sidebar_box").css({ '-moz-border-radius': '8px', '-webkit-border-radius': '8px', 'border-radius': '8px' });
    $("div#price_table table").css({ '-moz-border-radius': '8px', '-webkit-border-radius': '8px', 'border-radius': '8px' });
    $("span.highlight_dark, span.highlight_light").css({ '-moz-border-radius': '2px', '-webkit-border-radius': '2px', 'border-radius': '2px' });
    $("div#about .team ul li a").css({ '-moz-border-radius': '8px', '-webkit-border-radius': '8px', 'border-radius': '8px' });
    $(".text_field").css({ '-moz-border-radius': '8px', '-webkit-border-radius': '8px', 'border-radius': '8px' });
    $("a.button span").css({ 'text-shadow': '#000 0px -0px 2px' });
    $("div#page .section_title h3").css({ 'text-shadow': '#3e2828 0px 0px 2px' });
    // Default text field values
    $(".text_field").focus(function(srcc) {
        if ($(this).val() == $(this)[0].title) {
            $(this).addClass("default_text_active");
            $(this).val("");
        }
    });
    $(".text_field").blur(function() {
        if ($(this).val() == "") {
            $(this).removeClass("default_text_active");
            $(this).val($(this)[0].title);
        }
    });
    $(".text_field").blur();

    // Button Hover
    if ($.browser.msie && $.browser.version == "7.0") {
        $(".button").css("padding-top", "0px");
    } else {
        jQuery('.button').hover(
                function() {
                    jQuery(this).stop().animate({opacity:0.8}, 400);
                },
                function() {
                    jQuery(this).stop().animate({opacity:1}, 400);
                }
                );
    }

    // Add form submit capability to buttons
    $("a.submit").click(function() {
        $(this).parent().submit();
    });

};


var _gaq = _gaq || [];
_gaq.push(['_setAccount', 'UA-20073483-1']);
_gaq.push(['_trackPageview']);

(function() {
    var ga = document.createElement('script');
    ga.type = 'text/javascript';
    ga.async = true;
    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
    var s = document.getElementsByTagName('script')[0];
    s.parentNode.insertBefore(ga, s);
})();

function load(href) {
    $('#login-component').hide();
    href = href.replace(/^!/, '');

    $.ajax({
        url: href,
        success: setContentPage,
        complete: function(){
            if (typeof(_gat) == 'object') {
                var pageTracker = _gat._getTracker("UA-20073483-1");
                pageTracker._trackPageview(href);
            }
        }
    });

}

var headerWithConnectedUser = function (username) {
    $('.not-connected').hide();
    $('.connected').fadeIn("fast");
    $('#username-welcome').html(username);
};

var headerWithoutConnectedUser = function () {
    $('#login-component').hide();
    $('.connected').hide();
    $('.not-connected').fadeIn("fast");
};

var initPage = function() {
    $.ajax({
        url: '/user/name',
        success: headerWithConnectedUser,
        error: headerWithoutConnectedUser
    });
};

var setContentPage = function(html) {
    $('#page_content').html(html);
    initPage();
};

$(document).ready(function() {
    initPage();

    $.history.init(function(url) {
        load(url == "" ? "/home" : url);
    },
    { unescape: "/,&!" });

    $('a#link-login').click(function() {
        $('#login-component').slideToggle(1);
        $('#login-input').focus();
    });

    $('div#header #menu_nav a').live('click', function(link) {
        var url = $(this).attr('href');
        url = url.replace(/^.*#/, '');
        $.history.load(url);
        return false;
    });
});