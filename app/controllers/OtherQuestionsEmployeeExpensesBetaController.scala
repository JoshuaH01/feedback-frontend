/*
 * Copyright 2019 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers

import config.FrontendAppConfig
import forms.{OtherQuestionsEmployeeExpensesBetaFormProvider, OtherQuestionsFormProvider}
import javax.inject.Inject
import models.{OtherQuestions, OtherQuestionsEmployeeExpensesBeta}
import navigation.Navigator
import pages.GenericQuestionsPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.Action
import services.AuditService
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import views.html.otherQuestionsEmployeeExpensesBeta

import scala.concurrent.ExecutionContext

class OtherQuestionsEmployeeExpensesBetaController @Inject()(appConfig: FrontendAppConfig,
                                                             override val messagesApi: MessagesApi,
                                                             navigator: Navigator,
                                                             formProvider: OtherQuestionsEmployeeExpensesBetaFormProvider,
                                                             auditService: AuditService
                                        )(implicit ec: ExecutionContext) extends FrontendController with I18nSupport {

  val form: Form[OtherQuestionsEmployeeExpensesBeta] = formProvider()
  lazy val successPage = navigator.nextPage(GenericQuestionsPage)(())
  def submitCall = routes.OtherQuestionsEmployeeExpensesBetaController.onSubmit()

  def onPageLoad = Action {
    implicit request =>
      Ok(otherQuestionsEmployeeExpensesBeta(appConfig, form, submitCall))
  }

  def onSubmit = Action {
    implicit request =>
      val origin="employee-expenses"
      form.bindFromRequest().fold(
        formWithErrors =>
          BadRequest(otherQuestionsEmployeeExpensesBeta(appConfig, formWithErrors, submitCall)),
        value => {
          println("here!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! value " + value)
          auditService.otherEmployeeExpensesBetaAudit(origin, request.session.get("feedbackId").getOrElse("-"), value)
          Redirect(successPage)
        }
      )
  }
}
