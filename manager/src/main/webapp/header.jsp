<%--
  Created by IntelliJ IDEA.
  User: xiaohei
  Date: 2015/8/22
  Time: 10:58
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  String basePath = request.getContextPath();
//  String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path;

%>

<html>
<head>
    <title></title>
    <link rel="icon" href="<%=basePath%>/images/favicon.ico">
    <link rel="stylesheet" type="text/css" href="<%=basePath%>/css/u8server.css">

    <link rel="stylesheet" type="text/css" href="<%=basePath%>/js/plugins/easyui/themes/black/easyui.css">
    <link rel="stylesheet" type="text/css" href="<%=basePath%>/js/plugins/easyui/themes/icon.css">
    <link rel="stylesheet" type="text/css" href="<%=basePath%>/js/plugins/easyui/themes/color.css">
    <link rel="stylesheet" type="text/css" href="<%=basePath%>/css/u8server.css">

    <script type="text/javascript" src="<%=basePath%>/js/plugins/easyui/jquery.min.js"></script>
    <script type="text/javascript" src="<%=basePath%>/js/plugins/easyui/jquery.easyui.min.js"></script>
    <script type="text/javascript" src="<%=basePath%>/js/plugins/easyui/locale/easyui-lang-zh_CN.js"></script>
    <script type="text/javascript" src="<%=basePath%>/js/jquery.md5.js"></script>

</head>
<body>

<div id="dialog_add" class="easyui-dialog u8_form"
     closed="true" buttons="#dlg-buttons" style="height: 240px;">
    <div class="ftitle">修改密码</div>
    <form id="fm" method="post" novalidate>

        <div class="u8_form_row">
            <label >旧密码：</label>
            <input id="password" type="password" class="easyui-textbox" style="height: 25px;" name="password" maxlength="20"/>
        </div>

        <div class="u8_form_row">
            <label >新密码：</label>
            <input id="password2" type="password" class="easyui-textbox" placeholder="新密码(字母和数字,8-20位)" style="height: 25px;" name="password" maxlength="20" />
        </div>

    </form>
</div>

<div id="dlg-buttons">
    <a href="javascript:void(0)" class="easyui-linkbutton c6"  onclick="javascript:doChange();" style="width:90px">确 定</a>
    <a href="javascript:void(0)" class="easyui-linkbutton" onclick="javascript:$('#dialog_add').dialog('close')" style="width:90px">取 消</a>
</div>

  <div class="top-bar">
      <div class="u8_logo">

      </div>
      <div class="u8_title">
          U8Server后台管理系统
      </div>

      <div style="float:right;color: #ffffff;font-size: 14px;margin-top: 5px;margin-right: 10px;">
          <span><a href="javascript:void(0)" onclick="exit();" style="width:90px;color:#ffffff">[退出]</a></span>
      </div>
      <div class="datagrid-btn-separator" style="float:right;height:14px;margin-top: 9px;margin-right: 2px;"></div>
      <div style="float:right;color: #ffffff;font-size: 14px;margin-top: 5px;margin-right: 2px;">
          <span>当前登录用户：</span><span>${sessionScope.adminName}</span>
          <span><a href="javascript:void(0)" onclick="changePWD();" style="width:90px;color:#ffffff">[修改密码]</a></span>
      </div>
  </div>

  <script type="text/javascript">

      function exit(){

          $.post('<%=basePath%>/admin/exit', {}, function(result){
              if (result.state == 1) {

                  location.href="<%=basePath%>/admin/login"

              }

          }, 'json');
      }

      function changePWD(){
          $("#dialog_add").window({
              top:($(window).height() - 300) * 0.5,
              left:($(window).width() - 420) * 0.5
          });

          $("#dialog_add").dialog('open').dialog('setTitle', '修改密码');

      }

      function doChange(){

          var p = $("#password").val();
          var p2 = $("#password2").val();

          if(p != null && p2 != null){

              var reg = /^(?=.*[a-zA-Z])(?=.*\d).{8,20}$/;
              if(!reg.test(p2)){
                  $.messager.show({
                      title:'操作提示',
                      msg:"请输入合法的密码(字母和数字,8-20位)"
                  });
                  return;
              }

              var md5Val = $.md5(p);
              var md5NewVal = $.md5(p2);

              $.post('<%=basePath%>/admin/changePassword', {password:md5Val,newPwd:md5NewVal}, function(result){

                  if (result.state == 1) {

                      $('#dialog_add').dialog('close');

                  }

                  $.messager.show({
                      title:'操作提示',
                      msg:result.msg
                  })

              }, 'json');

          }else{
              $.messager.show({
                  title:'操作提示',
                  msg:"请输入密码"
              })
          }
      }


  </script>

</body>
</html>
