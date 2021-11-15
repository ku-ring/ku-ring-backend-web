
const alerts = new Alerts();
const contentLoader = document.querySelector("#contentLoader");
const contentWrap = document.querySelector("#contentWrap");

function checkStatusCode(statusCode, serviceName, alertIds) {

    if(statusCode >= 200 && statusCode < 300) {
        renderAlert(serviceName, alertIds[0]);
    } else if(statusCode >= 300 && statusCode < 400) {
        renderAlert(serviceName, alertIds[1]);
    } else if(statusCode >= 400 && statusCode < 500) {
        renderAlert(serviceName, alertIds[2]);
    } else {
        renderAlert(serviceName, alertIds[3]);
    }
}

function renderAlert(serviceName, alertId) {
    const result = alerts.create(serviceName, alertId);
    if(result === false) {
        console.error(`${alertId} 렌더링 오류`);
    }
}

function initRender() {
    contentLoader.setAttribute("style", "display: block");
    contentWrap.setAttribute("style", "display: none");
}

/**
 *  content visible 하게 바꾸고, loader 숨기기
 */
function finishRender() {
    contentLoader.setAttribute("style", "display: none");
    contentWrap.setAttribute("style", "display: block");
}