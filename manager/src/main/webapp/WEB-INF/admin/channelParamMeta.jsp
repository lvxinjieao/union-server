<%--
  Created by IntelliJ IDEA.
  User: ant
  Date: 2018/2/22
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
<div id="metas">

</div>

<div id="easyui_toolbar" region="north" border="false"
>

    <div class="toolbar">

        <div style="float: left;margin-right: 3px;padding-top: 3px;">
            <label>渠道商：</label><input id="masters" type="text" name="masters" maxlength="255"/>
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
               icon="icon-remove" onclick="javascript:deleteMaster();">删除</a>
        </div>

    </div>

</div>

<div id="dialog_add" class="easyui-dialog u8_form"
     closed="true" buttons="#dlg-buttons" style="height: 300px;">
    <div class="ftitle">渠道参数配置信息</div>
    <form id="fm" method="post" novalidate>
        <input type="hidden" id="masterID" name="masterID" value="0"/>
        <input type="hidden" id="id" name="id" value="0"/>
        <%--<div class="u8_form_row">--%>
        <%--<label >渠道商：</label>--%>
        <%--<span id="masterName"></span>--%>
        <%--&lt;%&ndash;<input type="text" class="easyui-textbox" style="height: 25px;" name="masterName" maxlength="255" required="false" readonly="readonly"/>&ndash;%&gt;--%>
        <%--</div>--%>

        <div class="u8_form_row">
            <label>唯一Key：</label>
            <input type="text" class="easyui-textbox" style="height: 25px;" name="paramKey" maxlength="255"
                   required="false"/>
        </div>
        <div class="u8_form_row">
            <label>参数名称：</label>
            <input type="text" class="easyui-textbox" style="height: 25px;" name="showName" maxlength="255"
                   required="false"/>
        </div>

        <div class="u8_form_row">
            <label>默认值：</label>
            <input type="text" class="easyui-textbox" style="height: 25px;" name="defaultVal" maxlength="255"/>
        </div>

        <div class="u8_form_row">
            <label>配置说明：</label>
            <input type="text" class="easyui-textbox" style="height: 25px;" name="metaDesc" maxlength="1024"/>
        </div>

        <div class="u8_form_row">
            <label>参数类型：</label>
            <select class="easyui-combobox" name="paramType" style="height: 25px;width: 59%;">
                <option value="0">前后端通用</option>
                <option value="1">客户端专用</option>
                <option value="2">服务端专用</option>
            </select>
        </div>

    </form>
</div>
<div id="dlg-buttons">
    <a href="javascript:void(0)" class="easyui-linkbutton c6" onclick="saveMeta()" style="width:90px">保 存</a>
    <a href="javascript:void(0)" class="easyui-linkbutton" onclick="javascript:$('#dialog_add').dialog('close')"
       style="width:90px">取 消</a>
</div>


<script type="text/javascript">

    var url;

    function showAddDialog() {

        var masterID = $('#masters').combobox('getValue');
        var masterName = $("#masters").combobox('getText');

        if (masterID == null || masterID == undefined || masterID.length == 0) {
            $.messager.show({
                title: '操作提示',
                msg: '请在渠道商下拉框中选择一个渠道商'
            });
            return;
        }

        $("#dialog_add").window({
            top: ($(window).height() - 300) * 0.5,
            left: ($(window).width() - 420) * 0.5
        });

        $("#dialog_add").dialog('open').dialog('setTitle', '添加渠道参数配置项');

        $('#fm').form('clear');


        $("#masterID").val(masterID);
        $("#masterName").html(masterName);

        url = '<%=basePath%>/admin/channelParamMeta/saveMeta';

    }

    function showEditDialog() {


        $("#dialog_add").window({
            top: ($(window).height() - 300) * 0.5,
            left: ($(window).width() - 420) * 0.5
        });


        var row = $('#metas').datagrid('getSelected');
        if (row) {

            $("#dialog_add").dialog('open').dialog('setTitle', '编辑渠道参数配置项');
            $('#fm').form('load', row);
            url = '<%=basePath%>/admin/channelParamMeta/saveMeta';


        } else {
            $.messager.show({
                title: '操作提示',
                msg: '请选择一条记录'
            })
        }
    }

    function deleteMaster() {
        var row = $('#metas').datagrid('getSelected');
        if (row) {
            $.messager.confirm(
                '操作确认',
                '确定要删除该参数配置吗？(操作不可恢复)',
                function (r) {
                    if (r) {
                        $.post('<%=basePath%>/admin/channelParamMeta/removeMeta', {currMetaID: row.id}, function (result) {
                            if (result.state == 1) {
                                $('#dialog_add').dialog('close');
                                $("#metas").datagrid('reload');
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

    function saveMeta() {

        $('#fm').form('submit', {
            url: url,
            onSubmit: function () {
                return $(this).form('validate');
            },
            success: function (result) {
                var result = eval('(' + result + ')');

                if (result.state == 1) {
                    $('#dialog_add').dialog('close');

                    $("#metas").datagrid('reload');
                }

                $.messager.show({
                    title: '操作提示',
                    msg: result.msg
                })
            }
        });

    }


    function doSearch(value) {

        $("#metas").datagrid({
            queryParams: {
                currMasterID: value
            }
        });

    }

    $("#masters").combobox({
        url: '<%=basePath%>/admin/channelMaster/getAllMastersSimple',
        valueField: 'masterID',
        textField: 'masterName',
        onSelect: function (rec) {
            doSearch(rec.masterID);
        }
    });


    $("#metas").datagrid({
        height: '100%',
        url: '<%=basePath%>/admin/channelParamMeta/getMetaList',
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
        pageList: [15, 30, 45],
        showFooter: true,
        columns: [[
            {field: 'id', title: 'ID', width: 40, sortable: true},
            {field: 'paramKey', title: 'Key', width: 60, sortable: true},
            {field: 'showName', title: '名称', width: 80, sortable: true},
            {field: 'defaultVal', title: '默认值', width: 60, sortable: true},
            {field: 'paramTypeName', title: '参数类型', width: 60, sortable: true},
            {field: 'metaDesc', title: '配置说明', width: 230, sortable: true}
        ]],
        toolbar: '#easyui_toolbar'
    });

</script>

</body>
</html>
