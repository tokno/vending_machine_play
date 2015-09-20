const socketUrl = "ws://" + location.host + "/vending-machine";
const socket = new WebSocket(socketUrl);

// WebSocketでサーバにメッセージ送信
function sendMessage(message) {
    socket.send(JSON.stringify(message));
}

// 自販機Viewの表示更新
function updateView(vendingMachine) {
    $("#payment").text(vendingMachine.payment);
    $("#change").text(vendingMachine.change);
    $("#lamp").text(vendingMachine.lampLighted ? "Yes" : "No");

    var juice = vendingMachine.juice;
    $("#juice-name").text(juice.name);
    $("#juice-price").text(juice.price);
    $("#juice-stock").text(juice.count);
}

socket.onopen = function() {
    console.log("socket opened.");
    sendMessage({ operation: "show" });
};

socket.onmessage = function(message) {
    var vendingMachine = JSON.parse(message.data);
    console.log(vendingMachine);
    updateView(vendingMachine);
};

socket.onerror = function() {
    alert("error.");
};

socket.onclose = function() {
    alert("socket closed.");
};

$(function() {
    // お金を入れるボタン
    $(".insert-yen").on("click", function() {
        sendMessage({
            operation: "insert-money",
            value: $(this).data("value")
        });
    });

    // {購入,払い戻し,リセット}ボタン
    $(".operation-button").on("click", function() {
        sendMessage({
            operation: $(this).data("operation")
        });
    });
});
