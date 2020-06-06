package view.fxview.component.HumanResources.subcomponent

import java.net.URL
import java.util.ResourceBundle

import caseclass.CaseClassDB.Persona
import javafx.fxml.FXML
import javafx.scene.control.{Button, TableView, TextField}
import view.fxview.component.HumanResources.subcomponent.parent.IllBoxParent
import view.fxview.component.HumanResources.subcomponent.util.{CreateTable, PersonaTable}
import view.fxview.component.{AbstractComponent, Component}


//metodi controller -> view
trait IllBox extends Component[IllBoxParent]{

}

object IllBox{

  //button che chiama openModal setOnAction
  def apply(persona:List[Persona]): IllBox = new IllBoxFX(persona)

  private class IllBoxFX(employees: List[Persona]) extends AbstractComponent[IllBoxParent]("humanresources/subcomponent/AbsenceBox") with IllBox {

    @FXML
    var employeeTable: TableView[PersonaTable] = _
    @FXML
    var searchBox: TextField = _

    override def initialize(location: URL, resources: ResourceBundle): Unit = {
      val columnFields = List("id", "name", "surname")
      CreateTable.createColumns[PersonaTable](employeeTable, columnFields)
      CreateTable.fillTable[PersonaTable](employeeTable, employees)

      initializeSearch(resources)
      CreateTable.clickListener[PersonaTable](
        employeeTable,
        item => parent.openModal(item.id.get().toInt,item.name.get(),item.surname.get()))
    }

    private def initializeSearch(resourceBundle: ResourceBundle): Unit = {
      searchBox.setPromptText(resourceBundle.getString("search"))

      searchBox.textProperty().addListener((_, _, word) => {
        CreateTable.fillTable[PersonaTable](
          employeeTable, employees.filter(person => person.cognome.contains(word) ||
            person.nome.contains(word) ||
            person.matricola.head.toString.contains(word)))
      })
    }
  }
}