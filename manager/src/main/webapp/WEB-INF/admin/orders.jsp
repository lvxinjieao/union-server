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
//  String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path;

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

<div style="width:90%;height: 100px;">

    <table>
        <tr style="height: 30px;">
            <td><input id="games" type="text" style="height: 25px" prompt="选择游戏" name="allgames" maxlength="255"/></td>
            <td class="u8_search_item"><input id="channelID" type="text" class="easyui-textbox" style="height: 25px"
                                              prompt="渠道号" name="channelID" maxlength="255"/></td>
            <td class="u8_search_item"><input id="subChannelID" type="text" class="easyui-textbox" style="height: 25px"
                                              prompt="子渠道号" name="subChannelID" maxlength="255"/></td>
            <td class="u8_search_item"><input id="orderID" type="text" class="easyui-textbox" style="height: 25px"
                                              prompt="订单号" name="orderID" maxlength="255"/></td>
            <td class="u8_search_item"><input id="channelOrderID" type="text" class="easyui-textbox"
                                              style="height: 25px" prompt="渠道订单号" name="channelOrderID"
                                              maxlength="255"/></td>
            <td class="u8_search_item"><input id="beginCreateTime" class="easyui-datetimebox" style="height: 25px"
                                              name="beginCreateTime" prompt="订单创建开始时间" data-options="showSeconds:true"
                                              style="width:140px"></td>
            <td class="u8_search_item"><input id="endCreateTime" class="easyui-datetimebox" style="height: 25px"
                                              name="endCreateTime" prompt="订单创建结束时间" data-options="showSeconds:true"
                                              style="width:140px"></td>
            <%--<td class="u8_search_item"><a href="javascript:void(0)" class="easyui-linkbutton c6" onclick="doSearch();" style="width:80px">查  询</a></td>--%>
        </tr>

        <tr>
            <td><input id="minMoney" name="minMoney" prompt="最小金额(分)" type="text" class="easyui-numberbox"
                       style="height: 25px" data-options="min:0,precision:0"/></td>
            <td class="u8_search_item"><input id="maxMoney" name="maxMoney" prompt="最大金额(分)" type="text"
                                              class="easyui-numberbox" style="height: 25px"
                                              data-options="min:0,precision:0"/></td>
            <td class="u8_search_item"><input id="minRealMoney" name="minRealMoney" prompt="最小实际金额(分)" type="text"
                                              class="easyui-numberbox" style="height: 25px"
                                              data-options="min:0,precision:0"/></td>
            <td class="u8_search_item"><input id="maxRealMoney" name="maxRealMoney" prompt="最大实际金额(分)" type="text"
                                              class="easyui-numberbox" style="height: 25px"
                                              data-options="min:0,precision:0"/></td>
            <td class="u8_search_item">
                <input id="state" type="text" class="easyui-combobox" style="height: 25px" prompt="订单状态" name="state"
                       maxlength="255"
                       data-options="
                  valueField:'value',
                  textField:'label',
                  data:[
                    {label:'等待支付',value:'1'},
                    {label:'支付成功',value:'2'},
                    {label:'交易完成',value:'3'},
                    {label:'交易失败',value:'0'}
                  ]
                "/>
                <%--<select id="state" class="easyui-combobox" prompt="订单状态" name="state" style="width: 140px;height: 25px">--%>
                <%--<option value="3">完成</option>--%>
                <%--<option value="1">支付中</option>--%>
                <%--<option value="2">处理成功</option>--%>
                <%--<option value="4">失败</option>--%>
                <%--</select>--%>

            </td>
            <td class="u8_search_item"><input id="serverID" type="text" class="easyui-textbox" style="height: 25px"
                                              prompt="服务器ID" name="serverID" maxlength="255"/></td>
            <td class="u8_search_item">
                <a href="javascript:void(0)" class="easyui-linkbutton c6" onclick="doSearch(1);" style="width:80px">查询</a>&nbsp;&nbsp;
                <a href="javascript:void(0)" class="easyui-linkbutton c4" onclick="doSearch(2);" style="width:80px">导出</a>
            </td>
        </tr>

        <tr>
            <td><input id="userID" type="text" class="easyui-textbox" style="height: 25px" prompt="用户ID" name="userID"
                       maxlength="255"/></td>
            <td class="u8_search_item"><input id="username" type="text" class="easyui-textbox" style="height: 25px"
                                              prompt="用户名" name="username" maxlength="255"/></td>
            <td class="u8_search_item"><input id="productID" type="text" class="easyui-textbox" style="height: 25px"
                                              prompt="商品ID" name="productID" maxlength="255"/></td>
            <td class="u8_search_item"><input id="productName" type="text" class="easyui-textbox" style="height: 25px"
                                              prompt="商品名" name="productName" maxlength="255"/></td>
            <td class="u8_search_item"><input id="roleID" type="text" class="easyui-textbox" style="height: 25px"
                                              prompt="角色ID" name="roleID" maxlength="255"/></td>
            <td class="u8_search_item"><input id="roleName" type="text" class="easyui-textbox" style="height: 25px"
                                              prompt="角色名称" name="roleName" maxlength="255"/></td>
            <td class="u8_search_item"><a href="javascript:void(0)" class="easyui-linkbutton" onclick="doClear();"
                                          style="width:80px">清 空</a></td>
        </tr>

        <%--<tr style="height: 30px;">--%>
        <%--<td><input id="channelUserID" type="text" class="easyui-textbox" prompt="渠道用户ID" name="channelUserID" maxlength="255" /></td>--%>
        <%--<td class="u8_search_item"><input id="channelUserName" type="text" class="easyui-textbox" prompt="渠道用户名" name="channelUserName" maxlength="255" /></td>--%>
        <%--<td class="u8_search_item"><input id="channelNickName" type="text" class="easyui-textbox" prompt="渠道昵称" name="channelNickName" maxlength="255" /></td>--%>
        <%--<td class="u8_search_item"><input id="beginLoginTime" class="easyui-datetimebox" name="loginCreateTime" prompt="登录开始时间" data-options="showSeconds:true" style="width:150px"></td>--%>
        <%--<td class="u8_search_item"><input id="endLoginTime" class="easyui-datetimebox" name="loginCreateTime" prompt="登录结束时间" data-options="showSeconds:true" style="width:150px"></td>--%>
        <%--<td class="u8_search_item"><a href="javascript:void(0)" class="easyui-linkbutton" onclick="doClear();" style="width:80px">清  空</a></td>--%>
        <%--</tr>--%>
    </table>

</div>

<div class="easyui-panel" style="margin-top: 5px; height:620px;border:hidden">
    <div id="orders" style="width: 100%;"></div>
</div>


<div id="easyui_toolbar" region="north" border="false">

    <div class="toolbar">

        <div style="float: left;">
            <a class="easyui-linkbutton" plain="true" icon="icon-filter" onclick="javascript:resend();">补单</a>
            <a class="easyui-linkbutton" plain="true" icon="icon-filter" onclick="javascript:resendOneKey();">一键补单</a>
        </div>

        <div class="datagrid-btn-separator"></div>

        <div style="float: left;">
            <a class="easyui-linkbutton" plain="true"
               icon="icon-remove" onclick="javascript:deleteOrder();">删除</a>
        </div>

    </div>


    <%--<div id="tb" style="float: right;">--%>
    <%--<input id="search_box" class="easyui-searchbox" style="width: 250px"  data-options="searcher:doSearch,prompt:'请输入查询词',menu:'#search_menu'" />--%>
    <%--<div id="search_menu" style="width:120px">--%>
    <%--<div data-options="name:'order_id'">订单号</div>--%>
    <%--<div data-options="name:'order_channelID'">渠道订单号</div>--%>
    <%--<div data-options="name:'order_username'">用户名</div>--%>
    <%--<div data-options="name:'order_channel'">渠道名称</div>--%>
    <%--<div data-options="name:'order_game'">所属游戏</div>--%>
    <%--</div>--%>
    <%--</div>--%>

</div>


<script type="text/javascript">

    function resend() {
        var row = $('#orders').datagrid('getSelected')
        if (row) {

            $.messager.confirm(
                '操作确认',
                '确定要补单吗？',
                function (r) {
                    if (r) {

                        $.post('<%=basePath%>/admin/orders/resendOrder', {currOrderID: row.orderID}, function (result) {
                            if (result.state == 1) {
                                $("#orders").datagrid('reload');

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
                msg: '请选择一条纪录'
            })
        }
    }

    function resendOneKey() {
        $.messager.confirm(
            '操作确认',
            '一键补单将会重发所有状态为[处理成功]的订单，确定要补单吗？',
            function (r) {
                if (r) {

                    $.post('<%=basePath%>/admin/orders/resendOrders', {}, function (result) {
                        if (result.state == 1) {
                            $("#orders").datagrid('reload');
                        }

                        $.messager.show({
                            title: '操作提示',
                            msg: result.msg
                        })
                    }, 'json');
                }
            }
        );
    }

    function deleteOrder() {
        var row = $('#orders').datagrid('getSelected');
        if (row) {
            $.messager.confirm(
                '操作确认',
                '确定要删除该用户吗？(操作不可恢复)',
                function (r) {
                    if (r) {
                        $.post('<%=basePath%>/admin/orders/removeOrder', {currOrderID: row.orderID}, function (result) {
                            if (result.state == 1) {
                                $("#orders").datagrid('reload');
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
        $("#orderID").val("");
        $("#userID").val("");
        $("#username").val("");
        $("#productID").val("");
        $("#productName").val("");
        $("#minMoney").numberbox("clear");
        $("#maxMoney").numberbox("clear");
        $("#minRealMoney").numberbox("clear");
        $("#maxRealMoney").numberbox("clear");
        $("#roleID").val("");
        $("#roleName").val("");
        $("#serverID").val("");
        $("#state").combobox("clear");
        $("#channelOrderID").val();
        $("#beginCreateTime").datetimebox('setValue', "");
        $("#endCreateTime").datetimebox('setValue', "");
    }

    function doSearch(sType) {
        var appID = $("#games").combobox("getValue");
        var channelID = $("#channelID").val();
        var subChannelID = $("#subChannelID").val();
        var orderID = $("#orderID").val();
        var userID = $("#userID").val();
        var username = $("#username").val();
        var productID = $("#productID").val();
        var productName = $("#productName").val();
        var minMoney = $("#minMoney").numberbox("getValue");
        var maxMoney = $("#maxMoney").numberbox("getValue");
        var minRealMoney = $("#minRealMoney").numberbox("getValue");
        var maxRealMoney = $("#maxRealMoney").numberbox("getValue");
        var roleID = $("#roleID").val();
        var roleName = $("#roleName").val();
        var serverID = $("#serverID").val();
        var state = $("#state").combobox("getValue");
        var channelOrderID = $("#channelOrderID").val();
        var beginCreateTime = $("#beginCreateTime").datetimebox('getValue');
        var endCreateTime = $("#endCreateTime").datetimebox('getValue');

        if (sType == 1) {
            $("#orders").datagrid({
                queryParams: {
                    orderID: orderID,
                    appID: appID,
                    channelID: channelID,
                    subChannelID: subChannelID,
                    userID: userID,
                    username: username,
                    productID: productID,
                    productName: productName,
                    minMoney: minMoney,
                    maxMoney: maxMoney,
                    minRealMoney: minRealMoney,
                    maxRealMoney: maxRealMoney,
                    roleID: roleID,
                    roleName: roleName,
                    serverID: serverID,
                    state: state,
                    channelOrderID: channelOrderID,
                    beginCreateTime: beginCreateTime,
                    endCreateTime: endCreateTime
                }
            });
        } else {
            location.href = "<%=basePath%>/admin/orders/exportOrders" + "?appID=" + appID + "&channelID=" + channelID + "&subChannelID=" + subChannelID + "&orderID=" + orderID + "&userID=" + userID + "&username=" + username + "&productID=" + productID + "&productName=" + productName + "&minMoney=" + minMoney + "&maxMoney=" + maxMoney + "&minRealMoney=" + minRealMoney + "&maxRealMoney=" + maxRealMoney + "&roleID=" + roleID + "&roleName=" + roleName + "&serverID=" + serverID + "&state=" + state + "&channelOrderID=" + channelOrderID + "&beginCreateTime=" + beginCreateTime + "&endCreateTime=" + endCreateTime;
        }
    }

    $("#orders").datagrid({
        height: '100%',
        url: '<%=basePath%>/admin/orders/searchOrders',
        method: 'POST',
        idField: 'orderID',
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
            {field: 'orderID', title: '订单号', sortable: true},
            {field: 'username', title: '用户名', sortable: true},
            {field: 'money', title: '金额(分)', sortable: true},
            {field: 'stateName', title: '状态', sortable: true},
            {field: 'channelOrderID', title: '渠道订单号', sortable: true},
            {field: 'channelID', title: '渠道号', sortable: true},
            {field: 'roleName', title: '角色名', sortable: true},
            {field: 'serverID', title: '服务器ID', sortable: true},
            {field: 'channelName', title: '渠道名称', sortable: true},
            {field: 'subChannelID', title: '子渠道号', sortable: true},
            {field: 'appName', title: '所属游戏', sortable: true},
            {field: 'createdTime', title: '下单时间', sortable: true},
            {field: 'notifyUrl', title: '回调地址', width: 40, sortable: true}
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
