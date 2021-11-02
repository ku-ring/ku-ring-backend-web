
let feedbackDatatable, noticeDatatable, userDatatable;
let datatableCnt = 0;

const paths = document.location.href.split("/");
const datatableType = paths[paths.length - 1];

window.addEventListener('DOMContentLoaded', event => {
    // Simple-DataTables
    // https://github.com/fiduswriter/Simple-DataTables/wiki

    if(datatableType === "dashboard" || datatableType === "feedback") {
        const feedbackDatatableElement = document.getElementById('feedbackDatatable');
        if (feedbackDatatableElement) {
            feedbackDatatable = new CustomDatatable(feedbackDatatableElement);
            feedbackDatatable.on("datatable.init", () => onDatatableInit("feedbackDatatableWrap"));
            datatableCnt += 1;
        }
    }

    if(datatableType === "dashboard" || datatableType === "notice") {
        const noticeDatatableElement = document.getElementById('noticeDatatable');
        if (noticeDatatableElement) {
            noticeDatatable = new CustomDatatable(noticeDatatableElement);
            noticeDatatable.on("datatable.init", () => onDatatableInit("noticeDatatableWrap"));
            datatableCnt += 1;
        }
    }

    if(datatableType === "dashboard" || datatableType === "user") {
        const userDatatableElement = document.getElementById('userDatatable');
        if (userDatatableElement) {
            userDatatable = new CustomDatatable(userDatatableElement);
            userDatatable.on("datatable.init", () => onDatatableInit("userDatatableWrap"));
            datatableCnt += 1;
        }
    }
});

function addSelectorToUserDatatable() {

    const selectorDiv = document.createElement("div");
    selectorDiv.classList.add("dataTable-dropdown");
    selectorDiv.innerText = "카테고리 : "

    const label = document.createElement("label");
    selectorDiv.append(label);

    const selector = document.createElement("select");
    selector.classList.add("dataTable-selector");
    label.append(selector);

    // 전체 카테고리 option 추가
    const allOption = document.createElement("option");
    allOption.value = "all"; allOption.innerText = "all";
    selector.add(allOption);
    
    // 서버 지원 카테고리 option들 추가
    categories.forEach(category => {
        const option = document.createElement("option");
        option.value = category.name;
        option.innerText = category.name;
        selector.add(option);
    });

    const idx = datatableType === "dashboard" ? 2 : 0; // TODO: dirty code..
    const searchElement = document.querySelectorAll(".dataTable-top .dataTable-search")[idx];
    searchElement.insertBefore(selectorDiv, searchElement.firstChild);

    const br = document.createElement("br");
    br.setAttribute("style", "display: block; content: \"\"; font-size: 4px; height: 4px;");
    searchElement.insertBefore(br, searchElement.lastChild);

    selector.addEventListener("change", onSelectorChange);
}

function onSelectorChange(event) {

    const selectCategory = event.target.value;
    if(selectCategory === "all") {
        noticeDatatable.setFilter(false);
    } else {
        noticeDatatable.setFilter(true, "카테고리", selectCategory);
    }
    noticeDatatable.search("");
}

function onDatatableInit(datatableName) {
    const datatable = document.querySelector(`#${datatableName}`);

    if (datatable === null) {
        console.error("datatable is null");
        return;
    }

    if (datatableName === "noticeDatatableWrap") {
        addSelectorToUserDatatable();
    }

    if (--datatableCnt === 0) {
        finishRender();
    }
}
