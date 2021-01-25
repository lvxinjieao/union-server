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

<div id="channels"></div>

<div id="easyui_toolbar" region="north" border="false">
    <div class="toolbar">
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
               icon="icon-remove" onclick="javascript:deleteChannel();">删除</a>
        </div>

        <div class="datagrid-btn-separator"></div>

        <div style="float: left;">
            <a class="easyui-linkbutton" plain="true"
               icon="icon-remove" onclick="javascript:showConfigChannel();">配置渠道参数</a>
        </div>

        <div id="tb" style="float: right;">
            <input id="search_box" class="easyui-searchbox" style="width: 250px;height: 25px;"
                   data-options="searcher:doSearch,prompt:'请输入查询词',menu:'#search_menu'"/>
            <div id="search_menu" style="width:100px">
                <div data-options="name:'channel_name'">渠道名称</div>
                <div data-options="name:'channel_id'">渠道号</div>
                <div data-options="name:'game_name'">游戏名称</div>
            </div>
        </div>
    </div>
</div>

<div id="dialog_add" class="easyui-dialog u8_form"
     closed="true" buttons="#dlg-buttons" style="height: 450px;width: 520px;">
    <div class="ftitle">渠道信息</div>
    <form id="fm" method="post" novalidate>
        <input id="id" type="hidden" name="id"/>
        <input id="appID" type="hidden" name="appID"/>
        <input id="appName" type="hidden" name="appName"/>
        <input id="masterID" type="hidden" name="masterID"/>
        <input id="masterName" type="hidden" name="masterName"/>
        <div class="u8_form_row">
            <label>所属游戏：</label>
            <input id="games" type="text" style="height: 25px;" name="allgames" maxlength="255" required="true"/>
        </div>

        <div class="u8_form_row">
            <label>渠道商：</label>
            <input id="masters" type="text" style="height: 25px;" name="allmasters" maxlength="255" required="true"/>
        </div>
        <div class="u8_form_row">
            <label>渠道号：</label>
            <input id="channel" type="text" value="" class="easyui-textbox" style="height: 25px;" name="channelID"
                   maxlength="255" required="true"/>
            <a href="javascript:void(0)" class="easyui-linkbutton c6" onclick="recommendChannelID()" style="width:70px">推荐</a>
        </div>

        <div class="u8_form_row">
            <label>互通渠道号：</label>
            <input id="uniChannel" type="text" value="0" class="easyui-textbox" style="height: 25px;" name="uniChannel"
                   maxlength="255"/>
        </div>

        <div class="u8_form_row">
            <label>充值状态(0:开放/1:关闭)：</label>
            <input type="text" class="easyui-textbox" style="height: 25px;" name="openPayFlag" prompt="关闭后，下单会返回错误"
                   maxlength="255" novalidate/>
        </div>
        <div class="u8_form_row">
            <label>CPID：</label>
            <input type="text" class="easyui-textbox" style="height: 25px;" name="cpID" maxlength="255" novalidate/>
        </div>

        <div class="u8_form_row">
            <label>AppID：</label>
            <input type="text" class="easyui-textbox" style="height: 25px;" name="cpAppID" maxlength="255" novalidate/>
        </div>
        <div class="u8_form_row">
            <label>AppKey：</label>
            <input type="text" class="easyui-textbox" style="height: 25px;" name="cpAppKey" maxlength="255" novalidate/>
        </div>

        <div class="u8_form_row">
            <label>AppSecret：</label>
            <input type="text" class="easyui-textbox" style="height: 25px;" name="cpAppSecret" maxlength="1024"
                   novalidate/>
        </div>

        <div class="u8_form_row">
            <label>PayID：</label>
            <input type="text" class="easyui-textbox" style="height: 25px;" name="cpPayID" maxlength="255" novalidate/>
        </div>

        <div class="u8_form_row">
            <label>PayPublicKey：</label>
            <input type="text" class="easyui-textbox" style="height: 25px;" name="cpPayKey" maxlength="1024"
                   novalidate/>
        </div>

        <div class="u8_form_row">
            <label>PayPrivateKey：</label>
            <input type="text" class="easyui-textbox" style="height: 25px;" name="cpPayPriKey" maxlength="1024"
                   novalidate/>
        </div>

        <div class="u8_form_row">
            <label>特殊配置：</label>
            <input type="text" class="easyui-textbox" style="height: 25px;" name="cpConfig" maxlength="1024"
                   novalidate/>
        </div>

        <div class="u8_form_row">
            <label>登录认证地址：</label>
            <input type="text" class="easyui-textbox" style="height: 25px;" name="authUrl" prompt="这里会覆盖渠道商配置中的"
                   maxlength="1024" novalidate/>
        </div>

        <div class="u8_form_row">
            <label>支付回调地址：</label>
            <input type="text" class="easyui-textbox" style="height: 25px;" name="payCallbackUrl" prompt="这里会覆盖渠道商配置中的"
                   maxlength="1024" novalidate/>
        </div>

        <div class="u8_form_row">
            <label>渠道下单地址：</label>
            <input type="text" class="easyui-textbox" style="height: 25px;" name="orderUrl" prompt="这里会覆盖渠道商配置中的"
                   maxlength="1024" novalidate/>
        </div>

        <div class="u8_form_row">
            <label>脚本类路径：</label>
            <input type="text" class="easyui-textbox" style="height: 25px;" name="verifyClass" prompt="这里会覆盖渠道商配置中的"
                   maxlength="1024" novalidate/>
        </div>

    </form>
</div>
<div id="dlg-buttons">
    <a href="javascript:void(0)" class="easyui-linkbutton c6" onclick="saveUser()" style="width:90px">保 存</a>
    <a href="javascript:void(0)" class="easyui-linkbutton" onclick="javascript:$('#dialog_add').dialog('close')"
       style="width:90px">取 消</a>
</div>


<div id="dialog_params" class="easyui-dialog u8_form_no_height"
     closed="true" buttons="#dlg-buttons_params" style="width: 450px;">
    <div class="ftitle">请按照说明配置渠道参数</div>
    <form id="fm_params" method="post" novalidate>
    </form>
</div>
<div id="dlg-buttons_params">
    <a href="javascript:void(0)" class="easyui-linkbutton c6" onclick="saveChannelParams();" style="width:90px">保 存</a>
    <a href="javascript:void(0)" class="easyui-linkbutton" onclick="javascript:$('#dialog_params').dialog('close')"
       style="width:90px">取 消</a>
</div>


<script type="text/javascript">

    var url;
    var currChannel;

    function showAddDialog() {
        $("#dialog_add").window({
            top: ($(window).height() - 450) * 0.5,
            left: ($(window).width() - 520) * 0.5
        });

        $("#dialog_add").dialog('open').dialog('setTitle', '添加渠道');

        $('#fm').form('clear');

        url = '<%=basePath%>/admin/channels/addChannel';

    }

    function showEditDialog() {

        $("#dialog_add").window({
            top: ($(window).height() - 450) * 0.5,
            left: ($(window).width() - 520) * 0.5
        });


        var row = $('#channels').datagrid('getSelected');
        if (row) {

            $("#dialog_add").dialog('open').dialog('setTitle', '编辑渠道');
            $('#fm').form('load', row);
            $('#games').combobox('select', row.appID);
            $('#masters').combobox('select', row.masterID);
            url = '<%=basePath%>/admin/channels/saveChannel';

        } else {
            $.messager.show({
                title: '操作提示',
                msg: '请选择一条记录'
            })
        }
    }

    function showConfigChannel() {

        var row = $('#channels').datagrid('getSelected');
        if (row) {

            $("#fm_params").html("");
            $.post('<%=basePath%>/admin/channels/getChannelParams', {currChannelID: row.channelID}, function (result) {
                if (result.state == 1) {
                    currChannel = row.channelID;
                    var htmlContent = "";
                    var baseH = 20;
                    $.each(result.data, function (index, jsonData) {

                        htmlContent += "<div class=\"u8_form_row\"><label >" + jsonData.showName + "：</label><input id=\"" + jsonData.paramKey + "\" type=\"text\" class=\"easyui-textbox\" style=\"height: 25px;width:230px;\" prompt=\"" + jsonData.metaDesc + "\" name=\"" + jsonData.paramKey + "\" ";

                        if (jsonData.defaultVal != null) {
                            htmlContent += " value=\"" + jsonData.defaultVal + "\"";
                        }

                        htmlContent += " maxlength=\"4096\"/></div>";
                        baseH += 30;

                    });

                    $("#fm_params").append(htmlContent);

                    $.parser.parse($("#fm_params"));

                    $("#fm_params").css({"height": baseH + "px"});

                    $("#dialog_params").window({
                        top: ($(window).height() - baseH) * 0.5,
                        left: ($(window).width() - 450) * 0.5
                    });

                    $("#dialog_params").dialog('open').dialog('setTitle', '配置渠道参数');

                } else {
                    $.messager.show({
                        title: '操作提示',
                        msg: result.msg
                    })
                }


            }, 'json');


        } else {
            $.messager.show({
                title: '操作提示',
                msg: '请选择一条记录'
            })
        }

    }

    function deleteChannel() {
        var row = $('#channels').datagrid('getSelected');
        if (row) {
            $.messager.confirm(
                '操作确认',
                '确定要删除该渠道吗？(操作不可恢复)',
                function (r) {
                    if (r) {
                        $.post('<%=basePath%>/admin/channels/removeChannel', {currChannelID: row.channelID}, function (result) {
                            if (result.state == 1) {
                                $('#dialog_add').dialog('close');
                                $("#channels").datagrid('reload');
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

    function saveUser() {

        $('#fm').form('submit', {
            url: url,
            onSubmit: function () {
                return $(this).form('validate');
            },
            success: function (result) {
                var result = eval('(' + result + ')');

                if (result.state == 1) {
                    $('#dialog_add').dialog('close');
                    $("#channels").datagrid('reload');
                }

                $.messager.show({
                    title: '操作提示',
                    msg: result.msg
                })
            }
        })

    }

    function saveChannelParams() {
        var d = {};
        var t = $('#fm_params').serializeArray();
        $.each(t, function () {
            d[this.name] = this.value;
        });

        var strParams = JSON.stringify(d);

        if (currChannel != null && currChannel != undefined) {
            $.post('<%=basePath%>/admin/channels/saveChannelParams', {
                currChannelID: currChannel,
                currChannelParams: strParams
            }, function (result) {
                if (result.state == 1) {
                    $('#dialog_params').dialog('close');
                }

                $.messager.show({
                    title: '操作提示',
                    msg: result.msg
                })

            });
        }

    }


    function recommendChannelID() {

        $.post('<%=basePath%>/admin/channels/recommendChannelID', {}, function (result) {
            if (result.state == 1) {
                $("#channel").textbox('setValue', result.data);
            } else {
                alert(result.msg);
            }
        });

    }

    function doSearch(value, name) {

        if (name == "channel_name") {
            $("#channels").datagrid({
                queryParams: {
                    searchMaserName: value
                }
            });
        } else if (name == "channel_id") {
            $("#channels").datagrid({
                queryParams: {
                    channelID: value
                }
            });
        } else if (name == "game_name") {
            $("#channels").datagrid({
                queryParams: {
                    searchGameName: value
                }
            });
        }

    }

    $("#channels").datagrid({
        height: '100%',
        url: '<%=basePath%>/admin/channels/getAllChannels',
        method: 'POST',
        idField: 'channelID',
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
            {field: 'id', title: 'ID', sortable: true},
            {field: 'channelID', title: '渠道号', sortable: true},
            {field: 'appName', title: '所属游戏', sortable: true},
            {field: 'masterName', title: '渠道名称', sortable: true},
            {field: 'cpID', title: 'CPID', width: 40, sortable: true},
            {field: 'cpAppID', title: 'CPAppID', width: 40, sortable: true},
            {field: 'cpAppKey', title: 'CPAppKey', width: 40, sortable: true},
            {field: 'cpAppSecret', title: 'CPAppSecret', width: 40, sortable: true},
            {field: 'cpPayID', title: 'CPPayID', width: 40, sortable: true},
            {field: 'cpPayKey', title: 'CPPayPublicKey', width: 40, sortable: true},
            {field: 'cpPayPriKey', title: 'CPPayPrivateKey', width: 40, sortable: true},
            {field: 'cpConfig', title: '特殊配置', width: 40, sortable: true},
        ]],
        toolbar: '#easyui_toolbar'
    });

    $("#games").combobox({
        url: '<%=basePath%>/admin/games/getAllGamesSimple',
        valueField: 'appID',
        textField: 'name',
        onSelect: function (rec) {
            $('#appID').val(rec.appID);
            $('#appName').val(rec.appName);
        }
    });


    $("#masters").combobox({
        url: '<%=basePath%>/admin/channelMaster/getAllMastersSimple',
        valueField: 'masterID',
        textField: 'masterName',
        onSelect: function (rec) {
            $('#masterID').val(rec.masterID);
            $('#masterName').val(rec.masterName);
        }
    });

</script>

</body>
</html>
