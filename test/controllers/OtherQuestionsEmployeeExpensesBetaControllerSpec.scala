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

import controllers.actions._
import forms.{OtherQuestionsEmployeeExpensesBetaFormProvider, OtherQuestionsFormProvider}
import generators.ModelGenerators
import models.{OtherQuestions, OtherQuestionsEmployeeExpensesBeta}
import navigation.FakeNavigator
import org.mockito.Matchers.{eq => eqTo, _}
import org.mockito.Mockito._
import org.scalacheck.Arbitrary._
import org.scalatest.mockito.MockitoSugar
import org.scalatest.prop.PropertyChecks
import play.api.data.Form
import play.api.mvc.Call
import play.api.test.Helpers._
import services.AuditService
import views.html.{otherQuestions, otherQuestionsEmployeeExpensesBeta}

import scala.concurrent.ExecutionContext.Implicits.global

class OtherQuestionsEmployeeExpensesBetaControllerSpec extends ControllerSpecBase with PropertyChecks with ModelGenerators with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new OtherQuestionsEmployeeExpensesBetaFormProvider()
  val form = formProvider()
  lazy val mockAuditService = mock[AuditService]

  def submitCall = routes.OtherQuestionsEmployeeExpensesBetaController.onSubmit

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new OtherQuestionsEmployeeExpensesBetaController(
      frontendAppConfig,
      messagesApi,
      new FakeNavigator(onwardRoute),
      formProvider,
      mockAuditService)

  def viewAsString(form: Form[_] = form, action: Call) =
    otherQuestionsEmployeeExpensesBeta(frontendAppConfig, form, action)(fakeRequest, messages).toString

  "OtherQuestions Controller" must {

    "return OK and the correct view for a GET" in {
      forAll(arbitrary[String]) { origin =>
        val result = controller().onPageLoad(fakeRequest)

        status(result) mustBe OK
        contentAsString(result) mustBe viewAsString(action = submitCall)
      }
    }

    "redirect to the next page when valid data is submitted" in {
      forAll(arbitrary[String]) { origin =>
        val result = controller().onSubmit(fakeRequest)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(onwardRoute.url)
      }
    }

    "audit response on success" in {
      forAll(arbitrary[String], arbitrary[String], arbitrary[OtherQuestionsEmployeeExpensesBeta]) {
        (origin, feedbackId, answers) =>
          reset(mockAuditService)

          val values = Map(
            "ableToDo" -> answers.ableToDo.map(_.toString),
            "howEasyScore" -> answers.howEasyScore.map(_.toString),
            "whyGiveScore" -> answers.whyGiveScore,
            "howDoYouFeelScore" -> answers.howDoYouFeelScore.map(_.toString),
            "fullName" -> answers.fullName,
            "email" -> answers.email
          )

          val request = fakeRequest.withFormUrlEncodedBody(values.mapValues(_.getOrElse("")).toList: _*)
          controller().onSubmit(request.withSession(("feedbackId", feedbackId)))

          verify(mockAuditService, times(1))
            .otherEmployeeExpensesBetaAudit(eqTo("employee-expenses"), eqTo(feedbackId), eqTo(answers))(any())
      }
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      forAll(arbitrary[String]) { origin =>
        val postRequest = fakeRequest.withFormUrlEncodedBody(("ableToDo", "invalid value"))
        val boundForm = form.bind(Map("ableToDo" -> "invalid value"))

        val result = controller().onSubmit(postRequest)

        status(result) mustBe BAD_REQUEST
        contentAsString(result) mustBe viewAsString(form = boundForm, action = submitCall)
      }
    }
  }
}
