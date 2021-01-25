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
<div id="cps">

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
         icon="icon-remove" onclick="javascript:deleteCP();">删除</a>
    </div>

    <div id="tb" style="float: right;margin-right: 0px;">
      <input id="search_box" class="easyui-searchbox" style="width: 250px;height: 25px;"  data-options="searcher:doSearch,prompt:'请输入查询词',menu:'#search_menu'" />
      <div id="search_menu" style="width:100px;">
        <div data-options="name:'cp_id'">CPID</div>
        <div data-options="name:'cp_name'">名称</div>
      </div>
    </div>
  </div>

</div>

<div id="dialog_add" class="easyui-dialog u8_form"
     closed="true" buttons="#dlg-buttons" style="height: 400px">
  <div class="ftitle">CP信息</div>
  <form id="fm" method="post" novalidate>
    <input id="id" type="hidden" name="id" />
    <div class="u8_form_row">
      <label >CP名称：</label>
      <input type="text" class="easyui-textbox" style="height: 25px;" name="name" maxlength="255" required="false" />
    </div>

    <div class="u8_form_row">
      <label >法人姓名：</label>
      <input type="text" class="easyui-textbox" style="height: 25px;" name="owner" maxlength="30" novalidate />
    </div>

    <div class="u8_form_row">
      <label >地址：</label>
      <input type="text" class="easyui-textbox" style="height: 25px;" name="addr" maxlength="1024" novalidate />
    </div>

    <div class="u8_form_row">
      <label >联系电话：</label>
      <input type="text" class="easyui-textbox" style="height: 25px;" name="phoneNum" maxlength="30" novalidate />
    </div>

    <div class="u8_form_row">
      <label>联系人：</label>
      <input type="text" class="easyui-textbox" style="height: 25px;" name="contactName" maxlength="30" novalidate />
    </div>

    <div class="u8_form_row">
      <label>联系人QQ：</label>
      <input type="text" class="easyui-textbox" style="height: 25px;" name="contactQQ" maxlength="30" novalidate />
    </div>

    <div class="u8_form_row">
      <label>联系人微信：</label>
      <input type="text" class="easyui-textbox" style="height: 25px;" name="contactWX" maxlength="255" novalidate />
    </div>

    <div class="u8_form_row">
      <label>联系人电话：</label>
      <input type="text" class="easyui-textbox" style="height: 25px;" name="contactPN" maxlength="30" novalidate />
    </div>

  </form>
</div>
<div id="dlg-buttons">
  <a href="javascript:void(0)" class="easyui-linkbutton c6" onclick="saveCP()" style="width:90px">保 存</a>
  <a href="javascript:void(0)" class="easyui-linkbutton" onclick="javascript:$('#dialog_add').dialog('close')" style="width:90px">取 消</a>
</div>


<script type="text/javascript">

  var url;
  function showAddDialog(){
    $("#dialog_add").window({
      top:($(window).height() - 400) * 0.5,
      left:($(window).width() - 400) * 0.5
    });

    $("#dialog_add").dialog('open').dialog('setTitle', '添加CP');
    $('#fm').form('clear');
    $("#id").val("");


    url = '<%=basePath%>/admin/cps/saveCP';

  }

  function showEditDialog(){

    $("#dialog_add").window({
      top:($(window).height() - 400) * 0.5,
      left:($(window).width() - 400) * 0.5
    });


    var row = $('#cps').datagrid('getSelected');
    if(row){

      $("#dialog_add").dialog('open').dialog('setTitle', '编辑CP');
      $('#fm').form('clear');
      $('#fm').form('load', row);
      url = '<%=basePath%>/admin/cps/saveCP';

    }else{
      $.messager.show({
        title:'操作提示',
        msg:'请选择一条记录'
      })
    }
  }

  function deleteCP(){
    var row = $('#cps').datagrid('getSelected');
    if(row){
      $.messager.confirm(
              '操作确认',
              '确定要删除该CP吗？(操作不可恢复)',
              function(r){
                if(r){
                  $.post('<%=basePath%>/admin/cps/removeCP', {cpID:row.id}, function(result){
                    if (result.state == 1) {
                      $("#cps").datagrid('reload');
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

  function saveCP(){

    $('#fm').form('submit', {
      url:url,
      onSubmit:function(){
        return $(this).form('validate');
      },
      success:function(result){
        var result = eval('('+result+')');

        if (result.state == 1) {
          $('#dialog_add').dialog('close');
          $("#cps").datagrid('reload');
        }

        $.messager.show({
          title:'操作提示',
          msg:result.msg
        })
      }
    })

  }


  function doSearch(value, name){

    if(name == "cp_id"){
      $("#cps").datagrid({
        queryParams:{
          id:value
        }
      });
    }else if(name == "cp_name"){
      $("#cps").datagrid({
        queryParams:{
          name:value
        }
      });
    }

  }

  $("#cps").datagrid({
    height:'100%',
    url:'<%=basePath%>/admin/cps/getAllCPs',
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
      {field:'id', title:'CPID', width:40, sortable:true},
      {field:'name', title:'名称', width:40, sortable:true},
      {field:'owner', title:'法人', width:60, sortable:true},
      {field:'phoneNum', title:'联系电话', width:60, sortable:true},
      {field:'contactName', title:'联系人', width:100, sortable:true},
      {field:'contactQQ', title:'联系QQ', width:100, sortable:true},
      {field:'contactWX', title:'联系微信', width:80, sortable:true},
      {field:'contactPN', title:'商务电话', width:80, sortable:true},
      {field:'createTime', title:'创建时间', width:80, sortable:true}
    ]],
    toolbar:'#easyui_toolbar'
  });

</script>

</body>
</html>
