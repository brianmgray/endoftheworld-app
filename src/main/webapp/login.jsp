<!DOCTYPE html>
<html lang="en">
<head>
    <title>The End of the World Show | Login</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <!-- see https://developers.google.com/identity-toolkit/v2/acguide#intro -->
    <script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.4.2/jquery.min.js"></script>
    <script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jqueryui/1.8.2/jquery-ui.min.js"></script>
    <script type="text/javascript" src="https://ajax.googleapis.com/jsapi"></script>
    <script type="text/javascript" src="https://www.accountchooser.com/client.js"></script>
    <script type="text/javascript">
        function load() {
            google.load("identitytoolkit", "2", {packages: ["ac"], callback: callback});
        }
        function callback() {
            window.google.identitytoolkit.setConfig({
                developerKey: "{your developer key}",
                callbackUrl: "https://yoursite.com/callback", // must be a full URL
                userStatusUrl: "/userStatus", // these can just be partial paths
                loginUrl: "/login",
                signupUrl: "/signup",
                homeUrl: "/home",
                logoutUrl: "/logout",
                realm: "",  // optional
                language: "en",
                idps: ["Gmail", "AOL", "Hotmail", "Yahoo"],
                tryFederatedFirst: true,
                useContextParam: true
            });
            window.google.identitytoolkit.init();
        }
    </script>
    <script type="text/javascript" src="https://apis.google.com/js/client.js?onload=load"></script>
</head>

<body>
<div class="header"></div>

<div class="content hcenter">
    Login
</div>
</body>

</html>
