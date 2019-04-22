layui.use(['table', 'form'], function () {
    var table = layui.table;
    var form = layui.form;

    // 初始化数据库连接下拉框
    $.get("/connection/list", function (res) {
        if (res.code) {
            var data = res.data;
            layui.each(data, function (index, item) {
                $("#connectionSelect").append("<option value='" + item.id + "'>" + item.name + "</option>");
            });
        } else {
            layer.msg("加载数据库连接失败! ", {icon: 2});
        }
    });

    // 监听数据库连接select选择, 渲染表名下拉框
    form.on('select(connectionSelect)', function (data) {
        var connectionId = data.value; //得到被选中的值
        setTableNames(connectionId);
    });

    // 加载表格
    table.render({
        elem: '#webSocket'
        , url: '/websocket/list' // 数据接口
        , title: 'webSocket'
        , page: true // 开启分页
        , cols: [[ // 表头
            {type: 'checkbox'} // 开启复选框
            , {field: 'connectionName', title: '数据源名称'}
            , {field: 'clientGroupId', title: '客户端组ID'}
            , {field: 'type', title: '类型'}
            , {field: 'tableNameOrSql', title: '表名/SQL'}
            , {field: 'updateDate', title: '最后更新时间', sort: true}
            , {title: '操作', toolbar: '#webSocketBar'}
        ]]
        , response: {
            statusCode: 1 //规定成功的状态码，默认：0
            , msgName: 'data' //规定状态信息的字段名称，默认：msg
        }
        , even: true //开启隔行背景
        , size: 'lg' //小尺寸的表格
        , toolbar: 'default' //开启工具栏，此处显示默认图标，可以自定义模板
    });

    //监听头部工具事件
    table.on('toolbar(webSocket)', function (obj) {
        var checkStatus = table.checkStatus(obj.config.id);
        var data = checkStatus.data, length = data.length;
        switch (obj.event) {
            case 'add':
                showQuerySqlDiv();
                // 手动清空id
                $("#id").val("");
                // 模拟点击清空按钮
                $("#restButton").click();
                // 弹出页面层
                layer.open({
                    type: 1, // 1表示页面层
                    title: 'webSocket', //标题
                    // closeBtn: 0, // 去掉关闭按钮
                    shade: 0.6, // 遮罩层透明度
                    shadeClose: true, // 点击遮罩关闭弹出层
                    skin: 'layui-layer-lan', // 为弹出层增加样式
                    area: ['1000px', '618px'], // 宽高
                    anim: 4, // 动画: 从左翻滚
                    content: $("#addOrEdit") // 页面层内容
                });
                break;
            case 'update':
                if (length === 0) {
                    layer.msg('请选择一行! ', {icon: 0});
                } else if (length > 1) {
                    layer.msg('只能同时编辑一个! ', {icon: 0});
                } else {
                    preEdit(data[0].id, data[0].type);
                }
                break;
            case 'delete':
                if (length === 0) {
                    layer.msg('请选择一行! ', {icon: 0});
                } else {
                    var ids0 = "";
                    var ids1 = "";
                    layui.each(data, function (index, item) {
                        if (item.type === '入库触发') {
                            ids0 += item.id + ',';
                        } else if (item.type === '定时查询') {
                            ids1 += item.id + ',';
                        }
                    });
                    layer.confirm('确定删除? ', function (index) {
                        layer.close(index);
                        if (ids0 !== "") {
                            ids0 = ids0.substring(0, ids0.length - 1);
                        }
                        if (ids1 !== "") {
                            ids1 = ids1.substring(0, ids1.length - 1);
                        }
                        deleteByIds(ids0, ids1);
                    });
                }
                break;
        }
    });

    //监听行工具事件
    table.on('tool(webSocket)', function (obj) {
        var data = obj.data;
        var id = data.id;
        var typeString = data.type;
        switch (obj.event) {
            case 'del':
                layer.confirm('确定删除? ', function (index) {
                    layer.close(index);
                    if (typeString === '入库触发') {
                        deleteByIds(id, "");
                    } else if (typeString === '定时查询') {
                        deleteByIds("", id);
                    }
                });
                break;
            case 'edit':
                preEdit(id, typeString);
                break;
        }
    });

    // 监听radio单选
    form.on('radio(typeRadio)', function(data){
        var type = data.value;
        if (type === '1') {
            $("#querySqlTextarea").val("");
            showQuerySqlDiv();
        } else if (type === '0') {
            showTableNameDiv();
        }
    });

    // 监听提交按钮
    form.on('submit(submitButton)', function (data) {
        layer.closeAll('page');
        var submitData = data.field;
        var id = submitData.id;
        if (id) {
            // 如果id有值, 则更新
            update(submitData);
        } else {
            // id无值, 则新增
            delete submitData.id;
            insert(submitData);
        }
        return false; //阻止表单跳转。如果需要表单跳转，去掉这段即可。
    });

    // 弹出页面层并回显
    function preEdit(id, typeString) {
        var type = '';
        if (typeString === '入库触发') {
            type = '0';
        } else if (typeString === '定时查询') {
            type = '1';
        }
        // 查询详情
        $.get("/websocket/info", {id: id, type: type}, function (res) {
            if (res.code) {
                var data = res.data;
                // 渲染表名下拉框
                $.get("/connection/getTables", {connectionId: data.connectionId}, function (res) {
                    if (res.code) {
                        // 删除#tableNameSelect下除了第一个option其他所有option
                        $("#tableNameSelect option:gt(0)").remove();
                        var tableData = res.data;
                        layui.each(tableData, function (index, item) {
                            $("#tableNameSelect").append("<option value='" + item.name + "'>" + item.name +
                                "(表备注: " + item.comments + ")</option>");
                        });
                        // 刷新select选择框渲染
                        form.render('select');
                        // 数据回显
                        form.val("formWebSocket", {
                            "id": data.id
                            , "type": data.type
                            , "clientGroupId": data.clientGroupId
                            , "connectionId": data.connectionId
                            , "tableName": data.tableNameOrSql
                            , "querySql": data.tableNameOrSql
                        });
                        if (type === '1') {
                            showQuerySqlDiv();
                        } else if (type === '0') {
                            showTableNameDiv();
                        }
                        // 弹出页面层
                        layer.open({
                            type: 1, // 1表示页面层
                            title: 'webSocket', //标题
                            // closeBtn: 0, // 去掉关闭按钮
                            shade: 0.6, // 遮罩层透明度
                            shadeClose: true, // 点击遮罩关闭弹出层
                            skin: 'layui-layer-lan', // 为弹出层增加样式
                            area: ['1000px', '618px'], // 宽高, 这里用黄金比例
                            anim: 4, // 动画: 从左翻滚
                            content: $("#addOrEdit") // 页面层内容
                        });
                    } else {
                        layer.msg("加载表信息失败! ", {icon: 2});
                    }
                });
            } else {
                layer.msg("获取详情失败! ", {icon: 2});
            }
        });
    }

    // 渲染表名下拉框
    function setTableNames(connectionId) {
        $.get("/connection/getTables", {connectionId: connectionId}, function (res) {
            if (res.code) {
                // 删除#tableNameSelect下除了第一个option其他所有option
                $("#tableNameSelect option:gt(0)").remove();
                var data = res.data;
                layui.each(data, function (index, item) {
                    $("#tableNameSelect").append("<option value='" + item.name + "'>" + item.name +
                        "(表备注: " + item.comments + ")</option>");
                });
                // 刷新select选择框渲染
                form.render('select');
            } else {
                layer.msg("加载表信息失败! ", {icon: 2});
            }
        });
    }

    // 显示查询SQL的div
    function showQuerySqlDiv() {
        $("#tableNameDiv").hide();
        $("#querySqlDiv").show();
        $("#submitDiv").css("margin-top", "0");
    }

    // 显示表名的div
    function showTableNameDiv() {
        $("#querySqlDiv").hide();
        $("#tableNameDiv").show();
        $("#submitDiv").css("margin-top", "29%");
    }

    // 删除数据连接
    function deleteByIds(ids0, ids1) {
        $.get("/websocket/delete", {ids0: ids0, ids1: ids1}, function (res) {
            if (res.code === 1) {
                layer.msg("删除成功! ", {icon: 1});
                table.reload('webSocket');
            } else {
                layer.msg("删除失败! ", {icon: 2});
            }
        });
    }

    // 新增数据连接
    function insert(data) {
        $.post("/websocket/insert", data, function (res) {
            if (res.code === 1) {
                layer.msg("新增成功! ", {icon: 1});
                table.reload('webSocket');
            } else {
                layer.msg("新增失败, 可能SQL有问题! ", {icon: 2});
            }
        });
    }

    // 更新数据连接
    function update(data) {
        $.post("/websocket/update", data, function (res) {
            if (res.code === 1) {
                layer.msg("更新成功! ", {icon: 1});
                table.reload('webSocket');
            } else {
                layer.msg("更新失败, 可能SQL有问题! ", {icon: 2});
            }
        });
    }
});