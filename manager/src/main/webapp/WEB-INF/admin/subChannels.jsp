<%--
  Created by IntelliJ IDEA.
  User: xiaohei
  Date: 2017/2/8
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
<div style="height: 20px;">
  <div style="font-size: 12px;color: #ffffff;">注意：所属游戏，父渠道，子渠道号填写之后，不能再进行更改。只能删除，重新添加</div>
</div>
<div id="channels">

</div>

<div id="easyui_toolbar" region="north" border="false"
        >

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
         icon="icon-remove" onclick="javascript:deleteMaster();">删除</a>
    </div>

    <div id="tb" style="float: right;">
      <input id="search_box" class="easyui-searchbox" style="width: 250px;height: 25px;"  data-options="searcher:doSearch,prompt:'请输入查询词',menu:'#search_menu'" />
      <div id="search_menu" style="width:100px">
        <div data-options="name:'channel_id'">渠道号</div>
        <div data-options="name:'sub_channel_id'">子渠道号</div>
        <div data-options="name:'app_id'">游戏ID</div>
      </div>
    </div>
  </div>

</div>

<div id="dialog_add" class="easyui-dialog u8_form"
     closed="true" buttons="#dlg-buttons">
  <div class="ftitle">子渠道信息</div>
  <form id="fm" method="post" novalidate>
    <input id="id" type="hidden" name="id" />
    <input id="appID" type="hidden" name="appID" />
    <input id="channelID" type="hidden" name="channelID" />
    <div class="u8_form_row">
      <label >所属游戏：</label>
      <span id="gamesCombobox"><input id="games" type="text" style="height: 25px;" name="allgames" maxlength="255" required="true"/></span>
      <span id="gamesTextbox"></span>
    </div>

    <div class="u8_form_row">
      <label >父渠道：</label>
      <span id="channelsCombobox"><input id="parents" type="text" style="height: 25px;" name="allchannels" maxlength="255" required="true"/></span>
      <span id="channelsTextbox"></span>
    </div>
    <div class="u8_form_row">
      <label >子渠道号：</label>
      <input id="subChannelID" type="text" class="easyui-textbox" style="height: 25px;" name="subChannelID" maxlength="10" required="false" />
    </div>

    <div class="u8_form_row">
      <label >子渠道名：</label>
      <input id="subChannelName" type="text" class="easyui-textbox" style="height: 25px;" name="subChannelName" maxlength="64" />
    </div>

    <div class="u8_form_row">
      <label >充值状态(0:开放/1:关闭)：：</label>
      <input type="text" class="easyui-textbox" style="height: 25px;" name="openPayFlag" maxlength="1024" required="false" />
    </div>
  </form>
</div>
<div id="dlg-buttons">
  <a href="javascript:void(0)" class="easyui-linkbutton c6" onclick="javascript:saveSubChannel();" style="width:90px">保 存</a>
  <a href="javascript:void(0)" class="easyui-linkbutton" onclick="javascript:$('#dialog_add').dialog('close')" style="width:90px">取 消</a>
</div>


<div id="dialog_edit" class="easyui-dialog u8_form"
     closed="true" buttons="#dlg-buttons2">
  <div class="ftitle">子渠道信息</div>
  <form id="fm2" method="post" novalidate>
    <input type="hidden" name="id" />
    <input type="hidden" name="appID" />
    <input type="hidden" name="channelID" />
    <input type="hidden" name="subChannelID" />
    <div class="u8_form_row">
      <label >所属游戏：</label>
      <span id="gamesTextbox2"></span>
    </div>

    <div class="u8_form_row">
      <label >父渠道：</label>
      <span id="channelsTextbox2"></span>
    </div>
    <div class="u8_form_row">
      <label >子渠道号：</label>
      <span id="subChannelID2"></span>
    </div>

    <div class="u8_form_row">
      <label >子渠道名：</label>
      <input id="subChannelName2" type="text" class="easyui-textbox" style="height: 25px;" name="subChannelName" maxlength="255" />
    </div>

    <div class="u8_form_row">
      <label >充值状态(0:开放/1:关闭)：：</label>
      <input type="text" class="easyui-textbox" style="height: 25px;" name="openPayFlag" maxlength="1024" required="false" />
    </div>
  </form>
</div>
<div id="dlg-buttons2">
  <a href="javascript:void(0)" class="easyui-linkbutton c6" onclick="javascript:editSubChannel();" style="width:90px">保 存</a>
  <a href="javascript:void(0)" class="easyui-linkbutton" onclick="javascript:$('#dialog_edit').dialog('close')" style="width:90px">取 消</a>
</div>


<script type="text/javascript">

  var url;
  function showAddDialog(){
    $("#dialog_add").window({
      top:($(window).height() - 300) * 0.5,
      left:($(window).width() - 350) * 0.5
    });

    url = '<%=basePath%>/admin/subChannels/saveSubChannel';

    $("#gamesTextbox").hide();
    $("#channelsTextbox").hide();
    $('#gamesCombobox').show();
    $('#channelsCombobox').show();
    $("#dialog_add").dialog('open').dialog('setTitle', '添加子渠道');

    $('#fm').form('clear');

  }

  function showEditDialog(){

    $("#dialog_edit").window({
      top:($(window).height() - 300) * 0.5,
      left:($(window).width() - 350) * 0.5
    });

//    $('#games').hide();
//    $('#parents').hide();
    $('#gamesCombobox2').hide();
    $('#channelsCombobox2').hide();
    $("#gamesTextbox2").show();
    $("#channelsTextbox2").show();


    var row = $('#channels').datagrid('getSelected');
    if(row){

      $("#dialog_edit").dialog('open').dialog('setTitle', '编辑子渠道');
      $('#fm2').form('clear');
      $('#fm2').form('load', row);

      $("#gamesTextbox2").html(row.appName);
      $("#channelsTextbox2").html(row.masterName+"("+row.channelID+")");
      $("#subChannelID2").html(row.subChannelID);

      url = '<%=basePath%>/admin/subChannels/saveSubChannel';

    }else{
      $.messager.show({
        title:'操作提示',
        msg:'请选择一条记录'
      })
    }
  }

  function deleteMaster(){
    var row = $('#channels').datagrid('getSelected');
    if(row){
      $.messager.confirm(
              '操作确认',
              '确定要删除该子渠道吗？(操作不可恢复)',
              function(r){
                if(r){
                  $.post('<%=basePath%>/admin/subChannels/removeChannel', {channelID:row.channelID, subChannelID:row.subChannelID}, function(result){
                    if (result.state == 1) {
                      $('#dialog_add').dialog('close');
                      $("#channels").datagrid('reload');
                    }

                    $.messager.show({
                      title:'操作提示',
                      msg:result.msg
                    })

                  }, 'json');
                }
              }
      );
    }else{
      $.messager.show({
        title:'操作提示',
        msg:'请选择一条记录'
      })
    }
  }

  function saveSubChannel(){

    $('#fm').form('submit', {
      url:url,
      onSubmit:function(){
        return $(this).form('validate');
      },
      success:function(result){
        var result = eval('('+result+')');

        if (result.state == 1) {
          $('#dialog_add').dialog('close');
          $("#channels").datagrid('reload');
        }

        $.messager.show({
          title:'操作提示',
          msg:result.msg
        })
      }
    })

  }

  function editSubChannel(){

    $('#fm2').form('submit', {
      url:url,
      onSubmit:function(){
        return $(this).form('validate');
      },
      success:function(result){
        var result = eval('('+result+')');

        if (result.state == 1) {
          $('#dialog_edit').dialog('close');
          $("#channels").datagrid('reload');
        }

        $.messager.show({
          title:'操作提示',
          msg:result.msg
        })
      }
    })

  }

  function doSearch(value, name){

    if(name == "channel_id"){
      $("#channels").datagrid({
        queryParams:{
          channelID:value
        }
      });
    }else if(name == "sub_channel_id"){
      $("#channels").datagrid({
        queryParams:{
          subChannelID:value
        }
      });
    }else if(name == "app_id"){
      $("#channels").datagrid({
        queryParams:{
          appID:value
        }
      });
    }

  }


  $("#channels").datagrid({
    height:'100%',
    url:'<%=basePath%>/admin/subChannels/getAllSubChannels',
    method:'POST',
    idField:'id',
    striped:true,
    fitColumns:true,
    singleSelect:true,
    rownumbers:true,
    pagination:true,
    nowrap:true,
    loadMsg:'数据加载中...',
    pageSize: 15,
    pageList: [15, 30, 45, 60],
    showFooter:true,
    columns:[[
      {field:'id', title:'ID', width:40, sortable:true},
      {field:'appID', title:'游戏ID', width:60, sortable:true},
      {field:'appName', title:'游戏名称', width:60, sortable:true},
      {field:'channelID', title:'父渠道', width:60, sortable:true},
      {field:'subChannelID', title:'子渠道号', width:60, sortable:true},
      {field:'subChannelName', title:'子渠道名称', width:60, sortable:true},
      {field:'masterName', title:'父渠道名称', width:60, sortable:true}
    ]],
    toolbar:'#easyui_toolbar'
  });

  $("#games").combobox({
    url:'<%=basePath%>/admin/games/getAllGamesSimple',
    valueField:'appID',
    textField:'name',
    onSelect:function(rec){
      $('#appID').val(rec.appID);

      $("#parents").combobox({
        url:'<%=basePath%>/admin/channels/getAllChannelsSimple?currAppID='+rec.appID,
        valueField:'channelID',
        textField:'showName',
        onSelect:function(rec){
          $('#channelID').val(rec.channelID);
        }
      });

    }
  });

  $(function(){

      $("#subChannelID").textbox('textbox').bind('keyup', function(e){
          $(this).textbox('setValue', $(this).val().replace(/\D/g,''));
          var val = $(this).val();
          if(val.length > 10)
          {
              $(this).textbox('setValue', val.substring(0, 10));
          }
      });

  });



</script>

</body>
</html>
