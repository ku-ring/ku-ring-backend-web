
function addListeners() {
    const loginBtn = document.querySelector("#loginBtn");
    loginBtn.addEventListener("click", onLoginBtnClick);

    const tokenInput = document.querySelector("#tokenInput");
    tokenInput.addEventListener("focus", onLoginInputFocus);
}

function onLoginBtnClick(event) {
    event.preventDefault();

    const tokenInput = document.querySelector("#tokenInput");
    const token = tokenInput.value;

    if(token === null || token === "") {
        tokenInput.classList.add("has-error");
        return;
    }

    tryLogin(token);
}

function onLoginInputFocus(event) {
    event.preventDefault();
    if(event.target.classList.contains("has-error")) {
        event.target.classList.remove("has-error");
    }

}

function tryLogin(token) {

    const url = "/admin/login";
    const body = {
        token,
    };
    const headers = {
        "Content-Type": "application/json",
    };

    axios.post(url, body, {
        headers,
    }).then(response => {
        if(response.data) {
            const { resultCode } = response.data;
            if(resultCode >= 200 && resultCode < 300) {
                console.log(`dashboardUrl = ${response.data.dashboardUrl}`);
                document.location.href = response.data.dashboardUrl;
            } else if(resultCode >= 400 && resultCode < 500) {
                alert("유효하지 않은 토큰입니다.");
            } else {
                alert("서버 오류입니다. 지속될 시 관리자에게 문의해주세요.");
            }
        } else {
            alert("서버 오류입니다. 지속될 시 관리자에게 문의해주세요.");
        }
    }).catch(response => {
        alert("서버 오류입니다. 지속될 시 관리자에게 문의해주세요.");
    });
}

function init() {
    addListeners();
}

init();