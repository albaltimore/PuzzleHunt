function displayMessage(msg) {
    var elem = document.getElementById('otherMessage');
    elem.textContent = msg;
    elem.style.display = null;
}

function loadedGoogle() {
    console.log('hi');
    gapi.signin2.render('withGoogle', {
        longtitle: true,
        width: '400',
        height: '40'
    });

    console.log(gapi);
    gapi.load('auth2', () => {
        gapi.auth2.getAuthInstance().attachClickHandler('withGoogle', {}, function (googleUser) {
            console.log('google sign in', googleUser);
            document.getElementById('googleIdToken').value = googleUser.getAuthResponse().id_token;
            document.querySelector('.googleAuth').submit();
        }, () => displayMessage('Failed to login with Google'));
    });
}


window.fbAsyncInit = function () {
    FB.init({
        appId: '970763136443531',
        cookie: false,
        status: false,
        xfbml: true,
        version: 'v3.2'
    });

    FB.Event.subscribe('auth.statusChange', response => {
        console.log(response);
        if (response.status === 'connected') {
            document.getElementById('facebookIdToken').value = response.authResponse.accessToken;
            document.querySelector('.facebookAuth').submit();
        }
    });
};
(function (d, s, id) {
    var js, fjs = d.getElementsByTagName(s)[0];
    if (d.getElementById(id)) return;
    js = d.createElement(s);
    js.id = id;
    js.src = 'https://connect.facebook.net/en_US/sdk.js#xfbml=1&version=v3.2&appId=970763136443531&autoLogAppEvents=1';
    fjs.parentNode.insertBefore(js, fjs);
}(document, 'script', 'facebook-jssdk'));