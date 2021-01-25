<%--
  Created by IntelliJ IDEA.
  User: chenjie.chen
  Date: 2016/8/9
  Time: 18:51
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String basePath = request.getContextPath();
//  String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path;

%>
<html>
<head>
    <%--<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />--%>
    <%--<link rel="stylesheet" type="text/css" href="<%=basePath%>/js/plugins/easyui/themes/default/easyui.css">--%>
    <%--<link rel="stylesheet" type="text/css" href="<%=basePath%>/js/plugins/easyui/themes/icon.css">--%>
    <%--<link rel="stylesheet" type="text/css" href="<%=basePath%>/js/plugins/easyui/themes/color.css">--%>

    <link rel="icon" href="<%=basePath%>/images/favicon.ico">
    <link rel="stylesheet" type="text/css" href="<%=basePath%>/css/login.css">

    <script type="text/javascript" src="<%=basePath%>/js/plugins/easyui/jquery.min.js"></script>
    <script type="text/javascript" src="<%=basePath%>/js/plugins/easyui/jquery.easyui.min.js"></script>
    <script type="text/javascript" src="<%=basePath%>/js/plugins/easyui/locale/easyui-lang-zh_CN.js"></script>
    <script type="text/javascript" src="<%=basePath%>/js/jquery.md5.js"></script>
</head>
<body>
<div class="login_wrap">
    <div style="overflow: hidden;height:100%;display: block;">
        <div style="width: 680px; height: 100px; margin-left:auto;margin-right: auto; margin-top: 180px;background:url('<%=basePath%>/images/login_logo.png') top center no-repeat;">
        </div>
        <div style="margin-left:auto;margin-right: auto;margin-top: 30px;">
            <form id="login2" method="post">
                <label>用户名：<input id="username" type="text" name="userName"/></label>
                <label style="margin-top: 20px;">密　码：<input id="pwd" type="password" name="pass"/></label>
                <div style="position: relative;display: block;">
                    <label>验证码：<input id="vcode" type="text" name="yzm" maxlength="6"/></label>
                    <img id="vcodeImg" name="changeCaptcha" title="点击切换" alt="验证码" src="<%=basePath%>/admin/getVCode"
                         onclick="changeVCode();" style="position: absolute;right:0;top:0;z-index: 100">
                </div>

                <button id="btnLogin" type="button" onclick="javascript:login();">登　录</button>
            </form>
        </div>
    </div>


</div>


<script type="text/javascript">

    function chgUrl(url) {
        var timestamp = (new Date()).valueOf();
        //url = url.substring(0, 20);
        if ((url.indexOf("&amp;") >= 0)) {
            url = url + "&amp;timestamp=" + timestamp;
        } else {
            url = url + "?timestamp=" + timestamp;
        }
        return url;
    }

    function changeVCode() {
        var imgSrc = $("#vcodeImg");
        var src = imgSrc.attr("src");
        var tsrc = chgUrl(src);
        imgSrc.attr("src", tsrc);
    }

    function login() {

        var username = $("#username").val();
        var pwd = $("#pwd").val();
        pwd = $.md5(pwd);
        var vcode = $("#vcode").val();

        $.post('<%=basePath%>/admin/doLogin', {username: username, password: pwd, vcode: vcode}, function (result) {
            if (result.state == 1) {
                location.href = "<%=basePath%>/admin/index"
            } else {
                alert(result.msg);
                changeVCode();
            }
        }, 'json');


    }


    $("#pwd").keyup(function (event) {
        if (event.keyCode == 13) {
            $("#btnLogin").click();
        }
    });

</script>

</body>
</html>
