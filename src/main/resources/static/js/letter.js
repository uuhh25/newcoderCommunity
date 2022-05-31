$(function(){
	$("#sendBtn").click(send_letter);
	$(".close").click(delete_msg);
});

function send_letter() {
	$("#sendModal").modal("hide");

	// 获取弹出框的输入内容，根据id获取
	var toName = $("#recipient-name").val();
	var content = $("#message-text").val();

	$.post(
		CONTEXT_PATH+"/letter/send",
		{"toName":toName,"content":content},
		function (data){
			data=$.parseJSON(data);	// json数据解析
			// 请求成功与否的显示
			if (data.code==0){
				$("#hintBody").text("发送成功");
			}else {
				$("#hintBody").text(data.msg);
			}
			$("#hintModal").modal("show");
			// 页面刷新
			setTimeout(function(){
				$("#hintModal").modal("hide");
				location.reload();
			}, 2000);
		}
	)


}

function delete_msg() {
	// TODO 删除数据
	$(this).parents(".media").remove();
}