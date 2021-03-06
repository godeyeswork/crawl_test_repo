function adjustScale() {
    var e = 1,
        t = $(window).width() - 20;
    ew > t && (e = t / ew),
    setScale($("body"), e),
    setScale($("#header .logoWrapper,input[type=text],select"), 1 / e),
    $("#header .logoWrapper,input[type=text],select").css("transform-origin", "center"),
    e < 1
        ? $("body").css("width", $(window).width() / e)
        : $("body").css("width", "auto"),
    $("body").css("height", "auto");
    var i = $(window).height();
    i > $("body").height() * e && $("body").css("height", i / e)
}
function setScale(e, t) {
    $(e).transform("scaleX", t),
    $(e).transform("scaleY", t),
    $(e).css("transform-origin", "0 0")
}
var ww,
    ew;
$(function () {
    $("body").hasClass("index")
        ? ew = 744
        : $("body").hasClass("main")
            ? ew = 513
            : $("body").hasClass("inquiry")
                ? ew = 497
                : $("body").hasClass("policy")
                    ? ew = 600
                    : $("body").hasClass("edit") && (ew = 520);
    var e = !1;
    navigator.userAgent.indexOf("iPhone") > 0 && -1 === navigator.userAgent.indexOf("iPad") || navigator.userAgent.indexOf("iPod") > 0 || navigator.userAgent.indexOf("Android") > 0
        ? eventName = "orientationchange"
        : eventName = "resize",
    $(window).on(eventName, function () {
        !1 !== e && clearTimeout(e),
        e = setTimeout(function () {
            adjustScale()
        }, 200)
    }),
    adjustScale()
}),
jQuery.fn.scrollHeight = function () {
    var e = this.prop("clientHeight"),
        t = this.prop("scrollHeight");
    return e > t
        ? e
        : t
};