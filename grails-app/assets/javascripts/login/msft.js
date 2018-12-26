var applicationConfig = {
    clientID: 'd61adc91-cc63-4c21-a830-6e39acee75b4', //This is your client ID
    authority: "https://login.microsoftonline.com/common",  //Default authority value is https://login.microsoftonline.com/common
    graphScopes: ["user.read"],
    graphEndpoint: "https://graph.microsoft.com/v1.0/me"
};

var myMSALObj = new Msal.UserAgentApplication(applicationConfig.clientID, applicationConfig.authority, acquireTokenRedirectCallBack,
    {storeAuthStateInCookie: true, cacheLocation: "localStorage"});

function signIn() {
    if (false && myMSALObj.getUser()) {// avoid duplicate code execution on page load in case of iframe and popup window.
        acquireTokenPopupAndCallMSGraph();
    } else {
        myMSALObj.loginPopup(applicationConfig.graphScopes).then(function (idToken) {
            acquireTokenPopupAndCallMSGraph();
        }, function (error) {
            console.log(error);
        });
    }
}

function acquireTokenPopupAndCallMSGraph() {
    //Call acquireTokenSilent (iframe) to obtain a token for Microsoft Graph
    myMSALObj.acquireTokenSilent(applicationConfig.graphScopes).then(function (accessToken) {
        console.log('ACCESS TOKEN', accessToken);

        registerAccessToken(applicationConfig.graphEndpoint, accessToken);
    }, function (error) {
        console.log(error);
        // Call acquireTokenPopup (popup window) in case of acquireTokenSilent failure due to consent or interaction required ONLY
        if (error.indexOf("consent_required") !== -1 || error.indexOf("interaction_required") !== -1 || error.indexOf("login_required") !== -1) {
            myMSALObj.acquireTokenPopup(applicationConfig.graphScopes).then(function (accessToken) {
                console.log('ACCESS TOKEN INNER', accessToken);

                registerAccessToken(applicationConfig.graphEndpoint, accessToken);
            }, function (error) {
                console.log(error);
            });
        }
    });
}

function registerAccessToken(theUrl, accessToken) {
    document.getElementById('microsoftIdToken').value = accessToken;
    document.querySelector('.microsoftAuth').submit();
}


function acquireTokenRedirectCallBack(errorDesc, token, error, tokenType) {
    if (tokenType === "access_token") {
        registerAccessToken(applicationConfig.graphEndpoint, token);
    } else {
        console.log("token type is:" + tokenType);
    }
}

document.addEventListener("DOMContentLoaded", function (event) {
    document.getElementById("withMicrosoft").onclick = signIn;
});



function signOut() {
    myMSALObj.logout();
}
