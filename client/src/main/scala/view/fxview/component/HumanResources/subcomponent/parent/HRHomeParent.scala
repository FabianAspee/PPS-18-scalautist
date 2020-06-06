package view.fxview.component.HumanResources.subcomponent.parent


/**
 * @author Francesco Cassano
 *
 * It is the interface of the methods used by views to make requests to controller.
 * It extends home view's children parent
 *
 */
trait HRHomeParent extends RecruitParent with FiresParent with ZonaParent with IllBoxParent with HolidayBoxParent {

  /**
   * It notify parent that recruitView must be shown
   */
  def drawRecruitPanel: Unit

  /**
   * It notify parent that an employees View must be shown
   *
   * @param viewToDraw
   *                   string code for view selection
   */
  def drawEmployeePanel(viewToDraw: String): Unit

  /**
   * It notify parent that the show change password view
   *
   */
  def drawChangePassword: Unit

  /**
   * It notify parent that a manage zones view must be shown
   *
   */
  def drawZonePanel: Unit

  /**
   * design view for holiday
   */
  def drawHoliday():Unit
}