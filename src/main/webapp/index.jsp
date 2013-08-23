<%@ include file="/fragments/top.jspf" %>

<%@ include file="/fragments/templates-index.jspf" %>

<!-- Facebook API -->
<div id="fb-root"></div>
<script>
    window.fbAsyncInit = function() {
        // init the FB JS SDK
        FB.init({
            appId      : '1411379809075114',                   // App ID from the app dashboard
            channelUrl : '//WWW.YOUR_DOMAIN.COM/channel.html', // Channel file for x-domain comms
            status     : true,                                 // Check Facebook Login status
            xfbml      : true                                  // Look for social plugins on the page
        });

        // Additional initialization code such as adding Event Listeners goes here
    };

    // Load the SDK asynchronously
    (function(d, s, id){
        var js, fjs = d.getElementsByTagName(s)[0];
        if (d.getElementById(id)) {return;}
        js = d.createElement(s); js.id = id;
        js.src = "//connect.facebook.net/en_US/all.js";
        fjs.parentNode.insertBefore(js, fjs);
    }(document, 'script', 'facebook-jssdk'));
</script>

<div class="template">
    <h2>Checking code</h2>
</div>

<%@ include file="/fragments/bottom.jspf" %>

<!-- Additional Libraries -->

<!-- App Code -->
<script src="js/app/eotw.js"></script>

</body>

</html>
