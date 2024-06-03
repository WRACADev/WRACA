
$(document).ready(function () {  
    $("#entryLogo, #entryBG, #entryText, #main").show()
    $("#scrollingBG").fadeIn(1500)
    $("#bg1").delay(2000).fadeIn(2000)
    $("#scrollingBG2").delay(4000).animate({
        left: '-1500px',
    });
    $("#scrollingBG3").delay(1500).fadeIn(2000)
    $("#scrollingBG4").delay(3000).animate({
        left: '600px',
    });
    $("#entryLogo").delay(6000).animate({
        left: '780px',
    });
    $("#entryText").delay(6000).animate({
        left: '25px',
    });

});

$(document).ready(function() {
    $(".dropbtn").click(function() {
        $(".dropdown-content").toggle();
    });

    $(document).click(function(event) {
        if (!$(event.target).closest('.dropdown').length) {
            $(".dropdown-content").hide();
        }
    });
});
