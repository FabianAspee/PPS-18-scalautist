package view.fxview.component.login

import java.net.URL
import java.util.ResourceBundle

import javafx.fxml.FXML
import javafx.scene.control.{Button, Label, PasswordField, TextField}
import javafx.scene.input.KeyCode
import view.fxview.component.login.LoginParent
import view.fxview.component.{AbstractComponent, Component}
import view.fxview.util.ResourceBundleUtil._
/**
 * @author Giovanni Mormone.
 *
 * Component for the LoginBox contained in a [[view.fxview.component.login.LoginParent]]
 *
 */
trait LoginBox extends Component[LoginParent]{
  /**
   * Shows a message error on the view. If alredy showed does nothing.
   */
  def showErrorMessage():Unit

  /**
   * Resets the message error, if showed, and cleans the password field.
   */
  def resetViewFields():Unit
}

/**
 * @author Giovanni Mormone.
 *
 * Companion object of the [[view.fxview.component.login.LoginBox]]
 *
 */
object LoginBox{

  def apply(): LoginBox = new LoginBoxImpl()

  private class LoginBoxImpl extends AbstractComponent[LoginParent]("login/LoginBox") with LoginBox{
    @FXML
    var usernameField: TextField = _
    @FXML
    var passwordField: PasswordField = _
    @FXML
    var loginButton: Button = _
    @FXML
    var error: Label = _

    override def showErrorMessage(): Unit =
      error setVisible true

    override def initialize(location: URL, resources: ResourceBundle): Unit = {
      usernameField.setPromptText(resources.getResource("user"))
      passwordField.setPromptText(resources.getResource("pass"))
      loginButton.setText(resources.getResource("login"))
      error.setText(resources.getResource("error-message"))

      error.setVisible(false)
      loginButton.setOnAction(_ => this.loginSent())
    }

    private def loginSent(): Unit =
      parent.login(usernameField.getText, passwordField.getText)

    /**
     * Resets the message error, if showed, and to clean the password field.
     */
    override def resetViewFields(): Unit = {
      error setVisible false
      passwordField setText ""
    }
  }
}
