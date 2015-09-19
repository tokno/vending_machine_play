package controllers

import play.api.mvc._

object VendingMachineController extends Controller {

  // 自販機の現在の状態を返す
  def show = Action {
    Ok
  }

  def reset = Action {
    Ok
  }

  // 貨幣の投入
  def payment = Action {
    Ok
  }

  // ジュースの購入
  def buy = Action {
    Ok
  }

  // 払い戻し
  def refund = Action {
    Ok
  }

}
