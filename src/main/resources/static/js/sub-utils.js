
function createCategoryCheckBoxes() {

    const checkboxes = document.querySelector("#categoryCheckboxes");

    if(checkboxes === null) {
        console.error("Cannot find Checkbox top div");
        return;
    }

    categories.forEach(category => {

        const id = `${category.name}Checkbox`;

        const wrap = document.createElement("div");
        wrap.classList.add("form-check");
        wrap.classList.add("form-check-inline");

        const input = document.createElement("input");
        input.classList.add("form-check-input");
        input.setAttribute("id", id);
        input.setAttribute("type", "checkbox");
        input.setAttribute("value", category.name);

        const label = document.createElement("label");
        label.classList.add("form-check-label");
        label.setAttribute("for", id);
        label.innerText = category.name;

        wrap.append(input);
        wrap.append(label);
        checkboxes.append(wrap);
    });
}

function addBtnListeners() {

    const subBtn = document.querySelector("#subBtn");
    subBtn.addEventListener("click", onSubBtnClick);

    const subfindBtn = document.querySelector("#subFindBtn");
    subfindBtn.addEventListener("click", event => updateSubCategoryList({ event }));
}

function onSubBtnClick(event) {
    event.preventDefault();

    const subCategoryListElement = document.querySelector("#subCategoryList");
    if(subCategoryListElement === null) {
        console.error("Cannot find subCategoryListElement");
        return;
    }
    subCategoryListElement.innerHTML = "";

    const tokenInput = document.querySelector("#fcmTokenInput");
    const categoryCheckboxesParent = document.querySelector("#categoryCheckboxes");
    const categoryCheckboxDivs = Array.from(categoryCheckboxesParent.children);

    const token = tokenInput.value;
    const categories = [];
    for(let i=0; i<categoryCheckboxDivs.length; ++i) {
        const checkboxElement = categoryCheckboxDivs[i].getElementsByTagName("input")[0];
        if(checkboxElement.checked) {
            console.log(checkboxElement.value);
            categories.push(checkboxElement.value);
        }
    }

    const url = "/api/v1/notice/subscribe";
    const body = {
        id: token,
        categories,
    };
    const headers = {
        "Content-Type": "application/json",
        "Accept": "application/json",
    };


    const serviceName = "subUnsub";
    axios.post(url, body, { headers })
        .then(response => {
            if(response.data) {
                const { resultCode } = response.data;
                const alertIds = ["subUnsubSuccessAlert", "", "invalidTokenAlert", "serverErrorAlert"];

                checkStatusCode(resultCode, serviceName, alertIds);

                if(resultCode >= 200 && resultCode < 300) {
                    updateSubCategoryList({ token });
                }
            } else {
                renderAlert("subUnsub", "serverErrorAlert");
            }
        })
        .catch(error => {
            if(error.response) {
                const { status } = error.response;
                const alertIds = ["", "", "serverErrorAlert", "serverErrorAlert"];
                checkStatusCode(status, serviceName, alertIds);
            } else {
                renderAlert(serviceName, "serverErrorAlert");
            }
        });
}

function updateSubCategoryList({ event, token }) {

    const categoryList = document.querySelector("#subCategoryList");
    if(categoryList === null) {
        console.error("Cannot find categoryList element");
        return;
    }

    const tokenElement = document.querySelector("#subCategoryListFCMTokenInput");
    if(tokenElement === null) {
        console.error("Cannot find fcm token input element");
        return;
    }

    // loader on
    const checkSubLoader = document.querySelector(".loader-dot");
    checkSubLoader.setAttribute("style", "display: block;");

    const url = "/api/v1/notice/subscribe";
    if(token === undefined || token === null) {
        console.log("hi");
        token = tokenElement.value;
    }
    const params = {
        id: token,
    }

    const serviceName = "checkSub";
    axios.get(url, { params })
        .then(response => {
            if(response.data) {
                const { resultCode } = response.data;
                const alertIds = ["checkSubSuccessAlert", "", "invalidTokenAlert", "serverErrorAlert"];
                checkStatusCode(resultCode, serviceName, alertIds);

                if(resultCode >= 200 && resultCode < 300) {
                    createSubCategoryList(response.data.categories);
                }
            } else {
                renderAlert(serviceName, "serverErrorAlert");
            }
        })
        .catch(error => {
            if(error.response) {
                const { status } = error.response;
                const alertIds = ["", "", "serverErrorAlert", "serverErrorAlert"];
                checkStatusCode(status, serviceName, alertIds);
            } else {
                renderAlert(serviceName, "serverErrorAlert");
            }
        })
        .finally(_ => {
            checkSubLoader.setAttribute("style", "display: none;");
        });
}

function createSubCategoryList(categories) {

    const subCategoryListElement = document.querySelector("#subCategoryList");
    if(subCategoryListElement === null) {
        console.error("Cannot find subCategoryListElement");
        return;
    }
    subCategoryListElement.innerHTML = "";

    categories.forEach(category => {
        const li = document.createElement("li");
        li.classList.add("list-group-item");
        li.innerText = category;
        subCategoryListElement.append(li);
    });
}

function init() {
    createCategoryCheckBoxes();
    addBtnListeners();
}

init();

