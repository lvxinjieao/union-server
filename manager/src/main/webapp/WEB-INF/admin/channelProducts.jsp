<%--
  Created by IntelliJ IDEA.
  User: ant
  Date: 2018/5/22
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
<div id="products">

</div>

<div id="easyui_toolbar" region="north" border="false"
>

    <div class="toolbar">

        <div style="float: left;margin-right: 3px;padding-top: 3px;">
            <label>游戏：</label><input id="games" type="text" name="games" maxlength="255"/>
        </div>

        <div style="float: left;margin-right: 3px;padding-top: 3px;">
            <label>渠道：</label><input id="channels" type="text" name="channels" maxlength="255"/>
        </div>

        <div class="datagrid-btn-separator"></div>

        <div style="float: left;">
            <a class="easyui-linkbutton" plain="true" icon="icon-add" onclick="javascript:showAddDialog();">新增</a>
        </div>

        <div class="datagrid-btn-separator"></div>

        <div style="float: left;">
            <a class="easyui-linkbutton" plain="true" icon="icon-edit" onclick="javascript:showEditDialog();">编辑</a>
        </div>

        <div class="datagrid-btn-separator"></div>

        <div style="float: left;">
            <a class="easyui-linkbutton" plain="true"
               icon="icon-remove" onclick="javascript:deleteProduct();">删除</a>
        </div>

    </div>

</div>

<div id="dialog_add" class="easyui-dialog u8_form"
     closed="true" buttons="#dlg-buttons" style="height: 280px;">
    <div class="ftitle">游戏和渠道商品映射配置</div>
    <form id="fm" method="post" novalidate>
        <input type="hidden" id="channelID" name="channelID" value="0"/>
        <input type="hidden" id="id" name="id" value="0"/>


        <div class="u8_form_row">
            <label>游戏商品ID：</label>
            <input id="gameProductID" type="text" class="easyui-textbox" style="height: 25px;" name="gameProductID"
                   maxlength="255" required="false"/>
        </div>
        <div class="u8_form_row">
            <label>渠道商品ID：</label>
            <input type="text" class="easyui-textbox" style="height: 25px;" name="channelProductID" maxlength="255"
                   required="false"/>
        </div>

    </form>
</div>
<div id="dlg-buttons">
    <a href="javascript:void(0)" class="easyui-linkbutton c6" onclick="saveProduct()" style="width:90px">保 存</a>
    <a href="javascript:void(0)" class="easyui-linkbutton" onclick="javascript:$('#dialog_add').dialog('close')"
       style="width:90px">取 消</a>
</div>


<script type="text/javascript">

    var url;

    function showAddDialog() {

        var channelID = $('#channels').combobox('getValue');

        $('#gameProductID').textbox('textbox').attr('readonly', false);
        //$("#gameProductID").textbox('enable');

        if (channelID == null || channelID == undefined || channelID.length == 0) {
            $.messager.show({
                title: '操作提示',
                msg: '请选择游戏，然后选择一个渠道'
            });
            return;
        }

        $("#dialog_add").window({
            top: ($(window).height() - 280) * 0.5,
            left: ($(window).width() - 420) * 0.5
        });

        $("#dialog_add").dialog('open').dialog('setTitle', '游戏和渠道商品映射配置');

        $('#fm').form('clear');


        $("#channelID").val(channelID);

        url = '<%=basePath%>/admin/channelProduct/saveProduct';

    }

    function showEditDialog() {

        $('#gameProductID').textbox('textbox').attr('readonly', true);
        //$("#gameProductID").textbox('disable');

        $("#dialog_add").window({
            top: ($(window).height() - 280) * 0.5,
            left: ($(window).width() - 420) * 0.5
        });


        var row = $('#products').datagrid('getSelected');
        if (row) {

            $("#dialog_add").dialog('open').dialog('setTitle', '游戏和渠道商品映射配置');
            $('#fm').form('load', row);
            url = '<%=basePath%>/admin/channelProduct/saveProduct';


        } else {
            $.messager.show({
                title: '操作提示',
                msg: '请选择一条记录'
            })
        }
    }

    function deleteProduct() {
        var row = $('#products').datagrid('getSelected');
        if (row) {
            $.messager.confirm(
                '操作确认',
                '确定要删除该参数配置吗？(操作不可恢复)',
                function (r) {
                    if (r) {
                        $.post('<%=basePath%>/admin/channelProduct/deleteProduct', {currProductID: row.id}, function (result) {
                            if (result.state == 1) {
                                $('#dialog_add').dialog('close');
                                refreshList();
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

    function saveProduct() {

        $('#fm').form('submit', {
            url: url,
            onSubmit: function () {
                return $(this).form('validate');
            },
            success: function (result) {
                var result = eval('(' + result + ')');

                if (result.state == 1) {
                    $('#dialog_add').dialog('close');
                    refreshList();
                }

                $.messager.show({
                    title: '操作提示',
                    msg: result.msg
                })
            }
        });

    }


    function refreshList() {
        $('#products').datagrid('unselectAll');
        var channelID = $('#channels').combobox('getValue');

        $("#products").datagrid({
            queryParams: {
                currChannelID: channelID
            }
        });


    }

    $("#products").datagrid({
        height: '100%',
        url: '<%=basePath%>/admin/channelProduct/getProductList',
        method: 'POST',
        idField: 'id',
        striped: true,
        fitColumns: true,
        singleSelect: true,
        rownumbers: true,
        pagination: true,
        nowrap: true,
        loadMsg: '数据加载中...',
        pageSize: 15,
        pageList: [15, 30, 45, 60],
        showFooter: true,
        columns: [[
            {field: 'id', title: 'ID', width: 40, sortable: true},
            {field: 'gameProductID', title: '游戏商品ID', width: 120, sortable: true},
            {field: 'channelProductID', title: '渠道后台商品ID', width: 120, sortable: true}
        ]],
        toolbar: '#easyui_toolbar'
    });


    $("#games").combobox({
        url: '<%=basePath%>/admin/games/getAllGamesSimple',
        valueField: 'appID',
        textField: 'name',
        onSelect: function (rec) {
            //$('#appID').val(rec.appID);

            $("#channels").combobox({
                url: '<%=basePath%>/admin/channels/getAllChannelsSimple?currAppID=' + rec.appID,
                valueField: 'channelID',
                textField: 'showName',
                onSelect: function (rec) {
                    refreshList();
                }
            });

        }
    });

</script>

</body>
</html>
