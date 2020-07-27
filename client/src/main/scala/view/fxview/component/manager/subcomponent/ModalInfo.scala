package view.fxview.component.manager.subcomponent

import java.net.URL
import java.util.ResourceBundle

import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.layout.VBox
import javafx.stage.Stage
import view.fxview.AbstractFXModalView
import view.fxview.component.AbstractComponent
import view.fxview.component.manager.subcomponent.parent.ModalRunParent
import view.fxview.util.ResourceBundleUtil._


case class ModalInfo(stage:Stage) extends AbstractFXModalView(stage){
  private var modal:ModalInfoA = _
  def start():Unit = {
    pane.setStyle("-fx-fill-height: true;")
    modal = ModalInfo()
    pane.getChildren.add(modal.pane)
    show()
  }
  def message(message:String): Unit = modal.printMessage(message)
  def isShow:Boolean = myStage.isShowing
  override def show():Unit={
    myStage.show()
  }
  override def close(): Unit = {

  }
}
/**
 * Companion object of [[ModalInfo]]
 */
object ModalInfo{
  def apply(): ModalInfoA  = new ModalInfoFX()
  private class ModalInfoFX()
    extends AbstractComponent[ModalRunParent]("manager/subcomponent/InfoAlgorithmBox")
      with ModalInfoA {
    @FXML
    var messagesHeader: VBox = _
    @FXML
    var close: Button = _

    override def initialize(location: URL, resources: ResourceBundle): Unit = {
      super.initialize(location, resources)
      intiButton()
    }

    private def intiButton(): Unit = {
      close.setText(resources.getResource(key = "close"))
    }

    override def printMessage(information: String): Unit = {
      val info = InfoLabel(information)
      messagesHeader.getChildren.add(info.setParent(parent).pane)
    }
  }

}
