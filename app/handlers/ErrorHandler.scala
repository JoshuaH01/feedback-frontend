/*
 * Copyright 2021 HM Revenue & Customs
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

package handlers

import config.FrontendAppConfig
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.Request
import play.twirl.api.Html
import uk.gov.hmrc.play.bootstrap.frontend.http.FrontendErrorHandler
import views.html.{ErrorTemplateView, GlobalNotFoundView}

import javax.inject.{Inject, Singleton}

@Singleton
class ErrorHandler @Inject()(
  appConfig: FrontendAppConfig,
  val messagesApi: MessagesApi,
  errorTemplateView: ErrorTemplateView,
  globalNotFoundView: GlobalNotFoundView
) extends FrontendErrorHandler with I18nSupport {

  override def standardErrorTemplate(pageTitle: String, heading: String, message: String)(
    implicit rh: Request[_]): Html =
    errorTemplateView(pageTitle, heading, message, appConfig)

  override def notFoundTemplate(implicit request: Request[_]): Html =
    globalNotFoundView(appConfig)
}
