<!--
  To change this license header, choose License Headers in Project Properties.
  To change this template file, choose Tools | Templates
  and open the template in the editor.
-->

<%@ page contentType="text/html;charset=UTF-8" %>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <asset:stylesheet src="login/login.css"/>
    <title>Login</title>
</head>
<body style="background-color: black">
    <g:form useToken="true" action="login">
        <div style="margin: auto; width: 500px; padding-top: 100px; padding-bottom: 90px; left: 0px; right: 0px; font-size: 24px; ">
            <div style="position: relative">
                <label class="bloomberg-headline">Bloomberg</label><br/>

                <label style="color:white; font-size: 44px">Mystery Hunt</label>
                <label style="color: red; position: absolute; transform: rotate(-45deg); top: -5px; left: -20px; font-size: 48px; font-family: Segoe Script">Hack</label>

                <div style="height: 40px"></div>
                <g:if test="${flash.message}">
                    <label style="color: red; font-size: 18px">${flash.message}</label>
                    <div style="height: 30px" ></div>
                </g:if>

                <label style="color: #979797">Email:</label><br/>
                <input type="text" name="username" placeholder="USERNAME" class="login-entry" value="" /><br/>

                <label style="color: #979797">Password:</label><br/>
                <input type="password" name="password" placeholder="PASSWORD" class="login-entry"/><br/>
                <input type="submit" value="LOGIN" class="submit-button"/>
            </div>
        </div>

    </g:form>
</body>
</html>
