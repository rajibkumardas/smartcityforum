<%@ page import="smartcity.LoadProperties" %><%
    String oauthClientID;
    if (request.getRequestURL().toString().contains(LoadProperties.properties.getString("WebsiteName"))) {
        oauthClientID = LoadProperties.properties.getString("OAuthClientIDforWeb");
    } else {
        oauthClientID = LoadProperties.properties.getString("OAuthClientIDforLocalHost");
    }
%>
<html lang="en">
<head>
    <meta name="google-signin-scope" content="profile email">
    <meta name="google-signin-client_id" content="<%=oauthClientID%>">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.2/jquery.min.js"></script>
    <script src="https://apis.google.com/js/platform.js" async defer></script>
</head>
<body>
<div class="g-signin2" data-onsuccess="onSignIn" data-theme="dark"></div>
<script>
    function onSignIn(googleUser) {
        // Useful data for your client-side scripts:
        var profile = googleUser.getBasicProfile();
        console.log("ID: " + profile.getId()); // Don't send this directly to your server!
        console.log('Full Name: ' + profile.getName());
        console.log('Given Name: ' + profile.getGivenName());
        console.log('Family Name: ' + profile.getFamilyName());
        console.log("Image URL: " + profile.getImageUrl());
        console.log("Email: " + profile.getEmail());

        // The ID token you need to pass to your backend:
        var id_token = googleUser.getAuthResponse().id_token;
        console.log("ID Token: " + id_token);
        var url = "ajax/checkAuth.jsp";

        $.ajax({
            type: 'POST',
            url: url,
            contentType: "application/x-www-form-urlencoded; charset=UTF-8",
            dataType: "json",
            data: {id: googleUser.getAuthResponse().id_token},
            success: function (response) {
                $('#info-modal .modal-title #spinner').remove();
                if (response && response.status == 0) {
                    //location.pathname = location.pathname.replace(/(.*)\/[^/]*/, "$1/"+ 'dashboard');
                    alert("logged in as " + response.email + " name = " + response.name);
                }
                else {
                    //LOG("Showing error");
                    alert('Error: ' + response.error);
                }
            },
            error: function (jqXHR, textStatus, errorThrown) {
                alert('There was an unexpected error in connecting to the server');
            },
        });
    }
</script>
</body>
</html>