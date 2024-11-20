/*
 * Copyright 2024 HM Revenue & Customs
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
    "start.day" -> "1",
    "start.month" -> "2",
    "start.year" -> "2021",
    "end.day" -> "1",
    "end.month" -> "3",
    "end.year" -> "2021"
  )

  val searchByMrnPayload = Map(
    "value" -> "GHRT122910DC537220"
  )

  val requestDatesPayload = Map(
    "start.month" -> "01",
    "start.year" -> "2023",
    "end.month" -> "04",
    "end.year" -> "2023"
  )

  setup("view-cash-account", "View cash account details") withRequests
    getPage("Cash account page",
      s"$baseUrl/customs/cash-account"
    )

  val searchAndDownloadSetup =
    setup("search-and-download-cash-transactions", "search and download cash transactions")

  searchAndDownloadSetup.withRequests(
    getPage("search page",
      saveToken = true,
      s"$baseUrl/customs/cash-account/request-cash-transactions"
    ),

    postPage("search page",
      s"$baseUrl/customs/cash-account/request-cash-transactions",
      s"$baseUrl/customs/cash-account/requested-cash-transactions",
      searchPayload
    ),

    getPage("download cash transactions",
      s"$baseUrl/customs/cash-account/download-requested-csv?from=2021-02-01&to=2021-03-31&disposition=inline"
    )
  )

  setup("view-cash-account-v2", "View cash account details v2") withRequests
    getPage("Cash account page v2",
      saveToken = true,
      s"$baseUrl/customs/cash-account/v2"
    )


  setup("search-transactions-by-mrn", "View declaration details") withRequests(
    postPage("search by mrn",
      s"$baseUrl/customs/cash-account/v2",
      nextPage = s"$baseUrl/customs/cash-account/transaction-search/GHRT122910DC537220",
      searchByMrnPayload
    ),

    getPage("declaration details page",
      s"$baseUrl/customs/cash-account/transaction-search/GHRT122910DC537220"
    )
  )

  setup("request-cash-account-statement", "Request cash account statements") withRequests(
    getPage("request dates page",
      saveToken = true,
      s"$baseUrl/customs/cash-account/request-cash-transactions/v2"
    ),

    postPage("request dates page",
      s"$baseUrl/customs/cash-account/request-cash-transactions/v2",
      s"$baseUrl/customs/cash-account/selected-cash-transactions",
      requestDatesPayload
    ),

    getPage("selected dates page",
      saveToken = true,
      s"$baseUrl/customs/cash-account/selected-cash-transactions"
    ),

    postPage("selected dates page",
      postToken = false,
      s"$baseUrl/customs/cash-account/selected-cash-transactions",
      s"$baseUrl/customs/cash-account/selected-confirmation",
      Map("csrfToken" -> f"$${csrfToken}")
    ),

    getPage("confirmation page",
      s"$baseUrl/customs/cash-account/selected-confirmation"
    )
  )
}
