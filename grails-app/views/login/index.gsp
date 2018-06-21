<!--
  To change this license header, choose License Headers in Project Properties.
  To change this template file, choose Tools | Templates
  and open the template in the editor.
-->

<%@ page contentType="text/html;charset=UTF-8" %>

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="shortcut icon" type="image/png" href="${createLink(controller: "login", action: "getFavicon")}"/>
    <asset:stylesheet src="login/login.css"/>
    <asset:stylesheet src="bootstrap.css"/>
    <title>Login</title>
</head>

<body style="background-color: black">
<g:form useToken="true" action="login">
    <div style="margin: auto; width: 500px; padding-top: 100px; padding-bottom: 90px; left: 0px; right: 0px; font-size: 24px; ">
        <div style="position: relative">
            <label class="bloomberg-headline">Bloomberg</label><br/><br/><br/>

            <label style="color:white; font-size: 44px">Case System</label>
            <label style="color: red; position: absolute; transform: rotate(-5deg); top: 65px; left: 10px; font-size: 64px; font-family: Segoe Script">H.A.A.D.</label>

            <div style="height: 40px"></div>
            <g:if test="${flash.message}">
                <label style="color: red; font-size: 18px">${flash.message}</label>

                <div style="height: 30px"></div>
            </g:if>

            <label style="color: #979797">Username:</label><br/>
            <input type="text" name="username" placeholder="USERNAME" class="login-entry" value=""/><br/>

            <label style="color: #979797">Password:</label><br/>
            <input type="password" name="password" placeholder="PASSWORD" class="login-entry"/><br/>
            <input type="submit" value="LOGIN" class="submit-button"/>
        </div>
    </div>

</g:form>
</body>
</html>
