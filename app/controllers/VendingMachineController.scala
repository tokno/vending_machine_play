package controllers

import jp.tokno.{Yen, VendingMachine}
import play.api.libs.iteratee.{Concurrent, Iteratee}
import play.api.libs.json._
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits.defaultContext

object VendingMachineController extends Controller {

  // 自販機
  var vendingMachine = VendingMachine()

  // WebSocket出力
  val (out, channel) = Concurrent.broadcast[JsValue]

  // WebSocket入力
  val in = Iteratee.foreach[JsValue] { input =>
    val operation = parseMessage(input)

    println(operation)

    operation match {
      case InsertMoney(yen) =>
        yen match {
          case Yen(1000) | Yen(2000) | Yen(5000) | Yen(10000) =>
            vendingMachine.insertBill(yen)
          case _ =>
            vendingMachine.insertCoin(yen)
        }

      case Reset =>
        vendingMachine = VendingMachine()

      case Refund =>
        vendingMachine.refund()

      case BuyJuice =>
        vendingMachine.buy()

      case Show | InvalidOperation =>
        // Do nothing.
    }

    channel.push(vendingMachineJson(vendingMachine))
  }

  // WebSocket通信
  def socket = WebSocket.using[JsValue] { request =>
    (in, out)
  }

  // 受信したメッセージを自販機操作に変換
  private def parseMessage(input: JsValue) = {
    val operationName = (input \ "operation").asInstanceOf[JsString].value

    operationName match {
      case "show" =>
        Show

      case "insert-money" =>
        val value = (input \ "value").asInstanceOf[JsNumber].value.toInt
        convertToYen(value) match {
          case Some(yen) => InsertMoney(yen)
          case _ => InvalidOperation
        }

      case "reset" =>
        Reset

      case "refund" =>
        Refund

      case "buy-juice" =>
        BuyJuice

      case _ =>
        InvalidOperation
    }
  }

  // 数値をYenに変換
  private def convertToYen(value: Int): Option[Yen] = {
    Map(
      1 -> Yen._1,
      5 -> Yen._5,
      10 -> Yen._10,
      50 -> Yen._50,
      100 -> Yen._100,
      500 -> Yen._500,
      1000 -> Yen._1000,
      2000 -> Yen._2000,
      5000 -> Yen._5000,
      10000 -> Yen._10000
    ).get(value)
  }

  // 自販機をJSONに変換
  private def vendingMachineJson(vm: VendingMachine): JsObject = {
    Json.obj(
      // 自販機の状態
      "payment" -> vm.amount,
      "change" -> vm.changeBox.amount,
      "lampLighted" -> vm.purchaseLampLighted,

      // ジュース
      "juice" -> Json.obj(
        "name" -> vm.juiceName,
        "price" -> vm.juicePrice,
        "count" -> vm.juiceCount
      )
    )
  }

}

trait VendingMachineOperation
case object Show extends VendingMachineOperation
case class InsertMoney(value: Yen) extends VendingMachineOperation
case object BuyJuice extends VendingMachineOperation
case object Reset extends VendingMachineOperation
case object Refund extends VendingMachineOperation
case object InvalidOperation extends VendingMachineOperation
