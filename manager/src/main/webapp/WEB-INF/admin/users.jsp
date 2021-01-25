<%--
  Created by IntelliJ IDEA.
  User: xiaohei
  Date: 2015/8/22
  Time: 14:01
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String basePath = request.getContextPath();
%>

<html>
<head>
    <title></title>

    <link rel="stylesheet" type="text/css" href="<%=basePath%>/js/plugins/easyui/themes/black/easyui.css">
    <link rel="stylesheet" type="text/css" href="<%=basePath%>/js/plugins/easyui/themes/icon.css">
    <link rel="stylesheet" type="text/css" href="<%=basePath%>/js/plugins/easyui/themes/color.css">
    <link rel="stylesheet" type="text/css" href="<%=basePath%>/css/u8server.css">

    <script type="text/javascript" src="<%=basePath%>/js/plugins/easyui/jquery.min.js"></script>
    <script type="text/javascript" src="<%=basePath%>/js/plugins/easyui/jquery.easyui.min.js"></script>
    <script type="text/javascript" src="<%=basePath%>/js/plugins/easyui/locale/easyui-lang-zh_CN.js"></script>

</head>
<body>

<div style="width:90%;height: 80px;">

    <table>
        <tr style="height: 30px;">
            <td><input id="games" type="text" style="height: 25px" prompt="选择游戏" name="allgames" maxlength="255"/></td>
            <td class="u8_search_item"><input id="channelID" type="text" class="easyui-textbox" style="height: 25px"
                                              prompt="渠道号" name="channelID" maxlength="255"/></td>
            <td class="u8_search_item"><input id="subChannelID" type="text" class="easyui-textbox" style="height: 25px"
                                              prompt="子渠道号" name="subChannelID" maxlength="255"/></td>
            <td class="u8_search_item"><input id="id" type="text" class="easyui-textbox" style="height: 25px"
                                              prompt="用户ID" name="name" maxlength="255"/></td>
            <td class="u8_search_item"><input id="beginRegTime" class="easyui-datetimebox" style="height: 25px"
                                              name="beginCreateTime" prompt="注册开始时间" data-options="showSeconds:true"
                                              style="width:150px"></td>
            <td class="u8_search_item"><input id="endRegTime" class="easyui-datetimebox" style="height: 25px"
                                              name="endCreateTime" prompt="注册结束时间" data-options="showSeconds:true"
                                              style="width:150px"></td>
            <td class="u8_search_item"><a href="javascript:void(0)" class="easyui-linkbutton c6" onclick="doSearch();"
                                          style="width:80px">查 询</a></td>
        </tr>
        <tr style="height: 30px;">
            <td><input id="name" type="text" class="easyui-textbox" style="height: 25px" prompt="用户名" name="name"
                       maxlength="255"/></td>
            <td class="u8_search_item"><input id="channelUserID" type="text" class="easyui-textbox" style="height: 25px"
                                              prompt="渠道用户ID" name="channelUserID" maxlength="255"/></td>
            <td class="u8_search_item"><input id="channelUserName" type="text" class="easyui-textbox"
                                              style="height: 25px" prompt="渠道用户名" name="channelUserName"
                                              maxlength="255"/></td>
            <td class="u8_search_item"><input id="channelNickName" type="text" class="easyui-textbox"
                                              style="height: 25px" prompt="渠道昵称" name="channelNickName"
                                              maxlength="255"/></td>
            <td class="u8_search_item"><input id="beginLoginTime" class="easyui-datetimebox" style="height: 25px"
                                              name="loginCreateTime" prompt="登录开始时间" data-options="showSeconds:true"
                                              style="width:150px"></td>
            <td class="u8_search_item"><input id="endLoginTime" class="easyui-datetimebox" style="height: 25px"
                                              name="loginCreateTime" prompt="登录结束时间" data-options="showSeconds:true"
                                              style="width:150px"></td>
            <td class="u8_search_item"><a href="javascript:void(0)" class="easyui-linkbutton" onclick="doClear();"
                                          style="width:80px">清 空</a></td>
        </tr>
    </table>

</div>

<div class="easyui-panel" style="margin-top: 5px; height: 620px;border:hidden">
    <div id="users" style="width: 100%;"></div>
</div>


<div id="easyui_toolbar" region="north" border="false">
    <div class="toolbar">
        <div>
            <a class="easyui-linkbutton" plain="true"
               icon="icon-remove" onclick="javascript:deleteUser();">删除</a>
        </div>
    </div>
</div>


<script type="text/javascript">

    function deleteUser() {
        var row = $('#users').datagrid('getSelected');
        if (row) {
            $.messager.confirm(
                '操作确认',
                '确定要删除该用户吗？(操作不可恢复)',
                function (r) {
                    if (r) {
                        $.post('<%=basePath%>/admin/users/removeUser', {currUserID: row.id}, function (result) {
                            if (result.state == 1) {
                                $("#users").datagrid('reload');
                            }

                            $.messager.show({
                                title: '操作提示',
                                msg: result.msg
                            })

                        }, 'json');
                    }
                }
            );
        } else {
            $.messager.show({
                title: '操作提示',
                msg: '请选择一条记录'
            })
        }
    }


    function doClear() {

        $("#games").combobox("clear");
        $("#channelID").val("");
        $("#subChannelID").val("");
        $("#id").val("");
        $("#name").val("");
        $("#beginRegTime").datetimebox("setValue", "");
        $("#endRegTime").datetimebox("setValue", "");
        $("#channelUserID").val("");
        $("#channelUserName").val("");
        $("#channelNickName").val("");
        $("#beginLoginTime").datetimebox('setValue', "");
        $("#endLoginTime").datetimebox('setValue', "");

    }

    function doSearch() {
        var appID = $("#games").combobox("getValue");
        var channelID = $("#channelID").val();
        var subChannelID = $("#subChannelID").val();
        var id = $("#id").val();
        var name = $("#name").val();
        var beginRegTime = $("#beginRegTime").datetimebox('getValue');
        var endRegTime = $("#endRegTime").datetimebox('getValue');
        var channelUserID = $("#channelUserID").val();
        var channelUserName = $("#channelUserName").val();
        var channelNickName = $("#channelNickName").val();
        var beginLoginTime = $("#beginLoginTime").datetimebox('getValue');
        var endLoginTime = $("#endLoginTime").datetimebox('getValue');

        $("#users").datagrid({
            queryParams: {
                appID: appID,
                channelID: channelID,
                subChannelID: subChannelID,
                id: id,
                name: name,
                beginRegTime: beginRegTime,
                endRegTime: endRegTime,
                channelUserID: channelUserID,
                channelUserName: channelUserName,
                channelNickName: channelNickName,
                beginLoginTime: beginLoginTime,
                endLoginTime: endLoginTime
            }
        });
    }

    $("#users").datagrid({
        height: '98%',
        fit: true,
        url: '<%=basePath%>/admin/users/searchUsers',
        method: 'POST',
        idField: 'id',
        striped: true,
        fitColumns: true,
        singleSelect: true,
        rownumbers: true,
        pagination: true,
        nowrap: true,
        loadMsg: '数据加载中...',
        pageSize: 20,
        pageList: [20, 40, 60],
        showFooter: true,
        columns: [[
            {field: 'id', title: 'ID', sortable: true},
            {field: 'name', title: '名称', sortable: true},
            {field: 'appName', title: '游戏', sortable: true},
            {field: 'channelID', title: '渠道号', sortable: true},
            {field: 'channelName', title: '渠道名称', sortable: true},
            {field: 'subChannelID', title: '子渠道号', sortable: true},
            {field: 'channelUserID', title: '渠道userID', sortable: true},
            {field: 'channelUserName', title: '渠道用户名', sortable: true},
            {field: 'channelUserNick', title: '用户昵称', sortable: true},
            {field: 'lastLoginTime', title: '最后登录时间', sortable: true},
            {field: 'createTime', title: '注册时间', sortable: true}
        ]],
        toolbar: '#easyui_toolbar'
    });

    $("#games").combobox({
        url: '<%=basePath%>/admin/games/getAllGamesSimple',
        valueField: 'appID',
        textField: 'name',
        onSelect: function (rec) {
            //$('#appID').val(rec.appID);
        }
    });

</script>

</body>
</html>
