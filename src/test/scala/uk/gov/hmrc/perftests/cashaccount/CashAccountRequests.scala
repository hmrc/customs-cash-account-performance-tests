/*
 * Copyright 2023 HM Revenue & Customs
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

package uk.gov.hmrc.perftests.cashaccount

import uk.gov.hmrc.performance.conf.ServicesConfiguration
import uk.gov.hmrc.performance.simulation.PerformanceTestRunner
import uk.gov.hmrc.perftests.cashaccount.Requests._

trait CashAccountRequests {
  self: PerformanceTestRunner with ServicesConfiguration =>

  private val baseUrl: String = baseUrlFor("customs-cash-account-frontend")

  val searchPayload = Map(
    "start.month" -> "2",
    "start.year" -> "2021",
    "end.month" -> "3",
    "end.year" -> "2021"
  )

  setup("view-cash-account", "View cash account details") withRequests(
    getPage("Cash account page", s"$baseUrl/customs/cash-account")
    )

  val searchAndDownloadSetup = setup("search-and-download-cash-transactions", "search and download cash transactions")
  searchAndDownloadSetup.withRequests(
    getPage("search page", saveToken = true, s"$baseUrl/customs/cash-account/request-cash-transactions"),
    postPage("search page", s"$baseUrl/customs/cash-account/request-cash-transactions", s"$baseUrl/customs/cash-account/requested-cash-transactions", searchPayload),
    getPage("download cash transactions", s"$baseUrl/customs/cash-account/download-requested-csv?from=2021-02-01&to=2021-03-31&disposition=inline")
  )
}
