package view.fxview.component.HumanResources.subcomponent.util

import java.util.stream.Collectors

import com.sun.javafx.scene.control.skin.TableViewSkin
import javafx.scene.control.cell.{PropertyValueFactory, TextFieldTableCell}
import javafx.scene.control.{TableColumn, TableRow, TableView}
import regularexpressionutilities.NumbersChecker
import view.fxview.component.manager.subcomponent.util.ShiftTable

import scala.jdk.CollectionConverters

object CreateTable {

  val DEFAULT_DIM: Int = 30

  def createColumns[A <: TableArgument](table: TableView[A], columns: List[String], dim: Int = DEFAULT_DIM): Unit = {

    columns.foreach(name => {
      val column: TableColumn[A, String] = createTableColumn(name.toUpperCase())
      column.setResizable(false)
      table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY)
      setFactoryAndWidth(column, name, dim)
      table.getColumns.add(column)
    })
  }

  def createEditableColumns[A<: TableArgument](table: TableView[A], columns: List[(String, (A, String) => A)]): Unit = {
    columns.foreach(col => {
      val column: TableColumn[A, String] = createEditableColumn(col._1.toUpperCase, col._2)
      setFactoryAndWidth(column, col._1, 80)
      table.getColumns.add(column)
    })
    table.setEditable(true)
  }

  def createNestedColumns[A <: TableArgument](table: TableView[A], columns: Map[String,List[String]]): Unit = {

    columns.foreach(name => {
      val column: TableColumn[A, String] = createTableColumn(name._1.toUpperCase)
      setFactoryAndWidth(column, name._1,177)
      name._2.foreach(nestedColumn=>{
        val columnNested: TableColumn[A, String] = createTableColumn(nestedColumn.toUpperCase)
        setFactoryAndWidth(columnNested, nestedColumn,177)
        column.getColumns.add(columnNested)
      })
      table.setMaxSize(430,200)
      table.getColumns.add(column)
    })
  }
  private def createTableColumn[A](name: String)=new TableColumn[A, String](name)

  private def createEditableColumn[A](name: String, action: (A, String) => A): TableColumn[A, String] = {
    val col = new TableColumn[A, String](name)
    col.setCellFactory(TextFieldTableCell.forTableColumn())
    col.setOnEditCommit((event: TableColumn.CellEditEvent[A, String]) => {
      val newVal = if (event.getNewValue.isEmpty || notNumber(event.getNewValue)) "0"
      else event.getNewValue
      event.getTableView.getItems.set(event.getTablePosition.getRow,
        action(event.getTableView.getItems.get(event.getTablePosition.getRow),
          newVal))
    })
    col
  }

  private def notNumber(str: String): Boolean =
    TextFieldControl.controlString(str, NumbersChecker)

  private def setFactoryAndWidth[A](column: TableColumn[A, String], name: String,dim:Int=DEFAULT_DIM): Unit ={
    column.setId(name.toLowerCase())
    column.setMinWidth(dim)
    column.setCellValueFactory(new PropertyValueFactory[A, String](name))
  }

  def fillTable[A <: TableArgument](table: TableView[A], elements: List[A]): Unit = {
    table.getItems.clear()
    elements.foreach(element => table.getItems.add(element))
  }

  def refillTable[A <: TableArgument](table: TableView[A], elements: List[A]): Unit = {
    table.getItems.forEach(item => table.getItems.remove(item))
    fillTable(table, elements)
  }

  def clickListener[A <: TableArgument](table: TableView[A], action: A => Unit): Unit = {
    table.setRowFactory(_ => {
      val row: TableRow[A] = new TableRow[A]()
      row.setOnMouseClicked(event => {
        if(event.getClickCount == 2 && !row.isEmpty)
          action(row.getItem)
      })
      row
    })
  }

  def createSkinTable[A](): (TableViewSkin[A], TableView[A]) = {
    val table = new TableView[A]()
    (new TableViewSkin[A](table), table)
  }

  def getElements[A](table: TableView[A]): Set[A] = {
    new CollectionConverters.ListHasAsScala[A](
      table.getItems.stream().map[A](x => x)
        .collect(Collectors.toList[A])).asScala.toSet
  }
}
