<!--
  To change this license header, choose License Headers in Project Properties.
  To change this template file, choose Tools | Templates
  and open the template in the editor.
-->

<%@ page contentType="text/html;charset=UTF-8" %>

<html>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="shortcut icon" type="image/png" href="${assetPath(src: 'favicon.png')}"/>
    <asset:stylesheet src="login/login.css"/>
    <asset:stylesheet src="login/bootstrap.css"/>
    <asset:javascript src="login/index.js"/>

    <meta name="google-signin-client_id"
          content="98624763155-ig63kk95v6jfs3803m7o53qpgbaqb1nm.apps.googleusercontent.com">
    <script src="https://apis.google.com/js/platform.js?onload=loadedGoogle" async defer></script>

    <title>Login</title>
</head>

<body>
<g:form useToken="true" action="login" controller="login">
    <div class="root-div">
        <div class="content-div">
            <label class="bloomberg-headline">Bloomberg</label>

            <label class="logo-div"></label>

            <g:if test="${flash.message}">
                <label class="flash-message">${flash.message}</label>
            </g:if>
            <label id="otherMessage" style="display: none" class="flash-message"></label>


            <label class="input-label">Login or Create Account:</label>
            <div id="withGoogle" class="login-provider-button login-provider-button-google"></div>


            <label class="input-label">Username:</label>
            <input type="text" name="username" placeholder="USERNAME" class="login-entry" value=""/>

            <label class="input-label">Password:</label>
            <input type="password" name="password" placeholder="PASSWORD" class="login-entry"/>
            <input type="submit" value="LOGIN" class="submit-button"/>


        </div>
    </div>
</g:form>

<g:form style="display: none" action="googleAuth" class="googleAuth">
    <g:textField name="idtoken" id="googleIdToken"/>
</g:form>

</body>

</html>