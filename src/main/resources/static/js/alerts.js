class Alerts {
    constructor() {

        this.alertGroupMap = new Map();
        this.alertGroupMap.set("checkSub", "checkSubAlertGroup");
        this.alertGroupMap.set("subUnsub", "subUnsubAlertGroup");
        this.alertGroupMap.set("fakeUpdate", "fakeUpdateAlertGroup");

        this.alertHTML = {
            "subUnsubSuccessAlert": "<div id=\"subUnsubSuccessAlert\" class=\"alert alert-primary alert-dismissible fade show\" role=\"alert\">\n" +
                "            정상적으로 구독했습니다.\n" +
                "            <button type=\"button\" class=\"btn-close\" data-bs-dismiss=\"alert\" aria-label=\"Close\"></button>\n" +
                "        </div>\n",

            "checkSubSuccessAlert":  "<div id=\"checkSubSuccessAlert\" class=\"alert alert-primary alert-dismissible fade show\" role=\"alert\">\n" +
                "            구독 카테고리 목록 로딩이 완료됐습니다.\n" +
                "            <button type=\"button\" class=\"btn-close\" data-bs-dismiss=\"alert\" aria-label=\"Close\"></button>\n" +
                "        </div>\n",

            "fakeUpdateSuccessAlert":  "<div id=\"fakeUpdateSuccessAlert\" class=\"alert alert-primary alert-dismissible fade show\" role=\"alert\">\n" +
                "            가짜 공지를 성공적으로 전송했습니다.\n" +
                "            <button type=\"button\" class=\"btn-close\" data-bs-dismiss=\"alert\" aria-label=\"Close\"></button>\n" +
                "        </div>\n",


            "invalidTokenAlert": "<div id=\"invalidTokenAlert\" class=\"alert alert-danger alert-dismissible fade show\" role=\"alert\">\n" +
                "            <strong>[오류]</strong> 유효하지 않은 토큰입니다.\n" +
                "            <button type=\"button\" class=\"btn-close\" data-bs-dismiss=\"alert\" aria-label=\"Close\"></button>\n" +
                "        </div>\n",

            "serverErrorAlert": "<div id=\"serverErrorAlert\" class=\"alert alert-danger alert-dismissible fade show\" role=\"alert\">\n" +
                "            <strong>[오류]</strong> 서버에 오류가 발생했습니다. 잠시 후 시도해주세요.\n" +
                "            <p>문제가 지속될 시 관우에게 문의해 주세요.</p>\n" +
                "            <button type=\"button\" class=\"btn-close\" data-bs-dismiss=\"alert\" aria-label=\"Close\"></button>\n" +
                "        </div>",

            "noSubjectAlert": "<div id=\"noSubjectAlert\" class=\"alert alert-danger alert-dismissible fade show\" role=\"alert\">\n" +
                "            <strong>[오류]</strong> 공지 제목은 최소 1자 이상, 128자 이하 길이여야 합니다.\n" +
                "            <button type=\"button\" class=\"btn-close\" data-bs-dismiss=\"alert\" aria-label=\"Close\"></button>\n" +
                "        </div>\n",

            "noArticleIdAlert": "<div id=\"noArticleIdAlert\" class=\"alert alert-danger alert-dismissible fade show\" role=\"alert\">\n" +
                "            <strong>[오류]</strong> 공지 아이디를 입력해주세요.\n" +
                "            <button type=\"button\" class=\"btn-close\" data-bs-dismiss=\"alert\" aria-label=\"Close\"></button>\n" +
                "        </div>\n",
        }
    }

    create(serviceName, alertId) {

        const groupId = this.alertGroupMap.get(serviceName);
        const group = document.querySelector(`#${groupId}`);

        if(group === null) {
            return false;
        }

        if(this.alertHTML[alertId] === undefined) {
            return false;
        }

        group.insertAdjacentHTML("afterbegin", this.alertHTML[alertId]);
        return true;
    }
}