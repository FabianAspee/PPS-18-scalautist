package view.fxview.component.manager.subcomponent

import java.net.URL
import java.util.ResourceBundle

import caseclass.CaseClassDB.Zona
import javafx.fxml.FXML
import javafx.scene.control.{Button, TextField}
import regularexpressionutilities.ZonaChecker
import view.fxview.component.HumanResources.subcomponent.util.TextFieldControl
import view.fxview.component.manager.subcomponent.parent.ModalZoneParent
import view.fxview.component.{AbstractComponent, Component}
import view.fxview.util.ResourceBundleUtil._
/**
 * @author Francesco Cassano
 *
 * Box that is drawn inside modal to manage information about a [[caseclass.CaseClassDB.Zona]] instance.
 * It extends [[view.fxview.component.Component]]
 * of [[ModalZoneParent]]
 */
trait ModalZone extends Component[ModalZoneParent] {

}

/**
 * Companion object of [[ModalZone]]
 */
object ModalZone {

  def apply(zona: Zona): ModalZone = new ModalZoneFX(zona)

  /**
   * JavaFX implementation of [[ModalZone]]
   * @param zona
   */
  private class ModalZoneFX(zona: Zona)
    extends AbstractComponent[ModalZoneParent]("manager/subcomponent/ModalZone") with ModalZone {

    @FXML
    var id: TextField = _
    @FXML
    var namez: TextField = _
    @FXML
    var delete: Button = _
    @FXML
    var update: Button = _

    override def initialize(location: URL, resources: ResourceBundle): Unit = {
      id.setText(zona.idZone.head.toString)
      id.setEditable(false)

      manageZonaText()

      delete.setText(resources.getResource("delete"))
      delete.setOnAction(_ => parent.deleteZona(zona))

      update.setText(resources.getResource("update"))
      update.setOnAction(_ => parent.updateZona(Zona(namez.getText, zona.idZone)))

      ableToChange
    }

    private def manageZonaText(): Unit = {
      namez.setText(zona.zones)
      namez.setEditable(true)
      namez.textProperty().addListener((_, oldS, word) => {
        TextFieldControl.controlNewChar(namez, ZonaChecker, word, oldS)
        ableToChange()
      })
    }

    private def ableToChange(): Unit =
      update.setDisable(namez.getText().equals(""))

  }
}
