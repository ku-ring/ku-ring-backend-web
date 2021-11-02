
const fakeUpdateLoader = document.querySelector("#fakeUpdateLoader");

function createCategorySelector() {
    const selector = document.querySelector("#noticeCategorySelector");

    categories.forEach(category => {
        const option = document.createElement("option");
        option.value = category.name;
        option.innerText = category.name;
        selector.append(option);
    });
}

function addBtnListeners() {
    const fakeNoticeBtn = document.querySelector("#fakeNoticeBtn");
    fakeNoticeBtn.addEventListener("click", onFakeNoticeBtnClick);
}

function onFakeNoticeBtnClick(event) {
    event.preventDefault();

    const noticeSubjectInput = document.querySelector("#noticeSubjectInput");
    const subject = noticeSubjectInput.value;

    if(subject.length === 0 || subject.length > 128) {
        alerts.create("fakeUpdate", "noSubjectAlert");
        return;
    }

    const noticeCategorySelector = document.querySelector("#noticeCategorySelector");
    const selectedCategory = noticeCategorySelector.value;

    sendNotice(selectedCategory, subject);
}

function sendNotice(category, subject) {

    fakeUpdateLoader.setAttribute("style", "display: inline-block");

    const url = "/admin/service/fake-update";
    const body = {
        category,
        subject,
    };
    const headers = {
        "Content-Type": "application/json",
        "Accept": "application/json",
    }

    axios.post(url, body, {
        headers
    }).then(response => {
        if(response.data) {
            const { resultCode } = response.data;
            const alertIds = ["fakeUpdateSuccessAlert", "", "noSubjectAlert", "serverErrorAlert"];
            checkStatusCode(resultCode, "fakeUpdate", alertIds);
        } else {
            renderAlert("fakeUpdate", "serverErrorAlert");
        }
    }).catch(error => {
        if(error.response) {
            const { status } = error.response;
            const alertIds = ["", "", "noSubjectAlert", "serverErrorAlert"];
            checkStatusCode(status, "fakeUpdate", alertIds);
        } else {
            renderAlert("fakeUpdate", "serverErrorAlert");
        }
    }).finally(_ => {
        fakeUpdateLoader.setAttribute("style", "display: none;");
    });
}

function init() {
    createCategorySelector();
    addBtnListeners();
}

init();