layui.use(['table', 'form'], function () {
    var table = layui.table;
    var form = layui.form;

    // 加载表格
    table.render({
        elem: '#connection'
        , url: '/connection/list' // 数据接口
        , title: '数据库连接' // 定义 table 的大标题, 在文件导出等地方会用到, layui 2.4.0 新增
        , page: true // 开启分页
        , cols: [[ // 表头
            {type: 'checkbox'} // 开启复选框
            , {field: 'name', title: '名称'}
            , {field: 'url', title: '地址'}
            , {field: 'username', title: '用户名'}
            , {field: 'updateDate', title: '最后更新时间', sort: true}
            , {title: '操作', toolbar: '#connectionBar'}
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
    table.on('toolbar(connection)', function (obj) {
        var checkStatus = table.checkStatus(obj.config.id);
        var data = checkStatus.data, length = data.length;
        switch (obj.event) {
            case 'add':
                // 手动清空id
                $("#id").val("");
                // 模拟点击清空按钮
                $("#restButton").click();
                // 弹出页面层
                layer.open({
                    type: 1, // 1表示页面层
                    title: '数据库连接', //标题
                    // closeBtn: 0, // 去掉关闭按钮
                    shade: 0.6, // 遮罩层透明度
                    shadeClose: true, // 点击遮罩关闭弹出层
                    skin: 'layui-layer-lan', // 为弹出层增加样式
                    area: ['600px', '352px'], // 宽高, 这里用黄金比例
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
                    preEdit(data[0].id);
                }
                break;
            case 'delete':
                if (length === 0) {
                    layer.msg('请选择一行! ', {icon: 0});
                } else {
                    var ids = "";
                    layui.each(data, function (index, item) {
                        ids += item.id + ',';
                    });
                    layer.confirm('确定删除? ', function (index) {
                        layer.close(index);
                        deleteByIds(ids.substring(0, ids.length - 1));
                    });
                }
                break;
        }
    });

    //监听行工具事件
    table.on('tool(connection)', function (obj) {
        var id = obj.data.id;
        switch (obj.event) {
            case 'del':
                layer.confirm('确定删除? ', function (index) {
                    layer.close(index);
                    deleteByIds(id);
                });
                break;
            case 'edit':
                preEdit(id);
                break;
        }
    });

    // 监听测试按钮
    form.on('submit(testButton)', function (data) {
        var submitData = data.field;
        $.post("/connection/test", submitData, function (res) {
            if (res.code === 1) {
                layer.msg("连接成功! ", {icon: 1});
                table.reload('connection');
            } else {
                layer.msg("连接失败! ", {icon: 2});
            }
        });
        return false; //阻止表单跳转。如果需要表单跳转，去掉这段即可。
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
    function preEdit(id) {
        // 查询详情
        $.get("/connection/info", {id: id}, function (res) {
            if (res.code) {
                var data = res.data;
                // 数据回显
                form.val("formConnection", {
                    "id": data.id
                    , "name": data.name
                    , "url": data.url
                    , "username": data.username
                    , "password": data.password
                });
                // 弹出页面层
                layer.open({
                    type: 1, // 1表示页面层
                    title: '数据库连接', //标题
                    // closeBtn: 0, // 去掉关闭按钮
                    shade: 0.6, // 遮罩层透明度
                    shadeClose: true, // 点击遮罩关闭弹出层
                    skin: 'layui-layer-lan', // 为弹出层增加样式
                    area: ['600px', '352px'], // 宽高
                    anim: 4, // 动画: 从左翻滚
                    content: $("#addOrEdit") // 页面层内容
                });
            } else {
                layer.msg("获取详情失败! ", {icon: 2});
            }
        });
    }

    // 删除数据连接
    function deleteByIds(ids) {
        $.get("/connection/delete", {ids: ids}, function (res) {
            if (res.code === 1) {
                layer.msg("删除成功! ", {icon: 1});
                table.reload('connection');
            } else {
                layer.msg("删除失败! ", {icon: 2});
            }
        });
    }

    // 新增数据连接
    function insert(data) {
        $.post("/connection/insert", data, function (res) {
            if (res.code === 1) {
                layer.msg("新增成功! ", {icon: 1});
                table.reload('connection');
            } else {
                layer.msg("新增失败! ", {icon: 2});
            }
        });
    }

    // 更新数据连接
    function update(data) {
        $.post("/connection/update", data, function (res) {
            if (res.code === 1) {
                layer.msg("更新成功! ", {icon: 1});
                table.reload('connection');
            } else {
                layer.msg("更新失败! ", {icon: 2});
            }
        });
    }
});