@file:Suppress("IllegalIdentifier")

package pl.elpassion.elspace.hub.report.list

import com.nhaarman.mockito_kotlin.*
import io.kotlintest.matchers.shouldBe
import io.reactivex.subjects.PublishSubject
import pl.elpassion.elspace.common.TreeSpec
import pl.elpassion.elspace.common.TreeTestSuiteBuilder
import pl.elpassion.elspace.common.assertOnFirstElement
import pl.elpassion.elspace.common.extensions.getTimeFrom
import pl.elpassion.elspace.hub.project.dto.newDayWithoutReports
import pl.elpassion.elspace.hub.project.dto.newPaidVacationHourlyReport
import pl.elpassion.elspace.hub.project.dto.newRegularHourlyReport
import pl.elpassion.elspace.hub.report.list.adapter.Empty
import pl.elpassion.elspace.hub.report.list.service.ReportsListAdaptersService
import java.util.*

class ReportListModelTest : TreeSpec() {

    private val reportListAdaptersSubject = PublishSubject.create<List<AdapterItem>>()
    private val service = mock<ReportsListAdaptersService>().apply {
        whenever(createReportsListAdapters(any())).thenReturn(reportListAdaptersSubject)
    }
    private var currentDay: Calendar = getTimeFrom(2016, Calendar.OCTOBER, 4)
    private val currentDayProvider = { currentDay }
    private val model = ReportListModel(service, currentDayProvider)
    private val states = model.states
    private val events = model.events

    init {
        "Model should " {
            "start with predefined ui state" > {
                states
                        .test()
                        .assertOnFirstElement { it shouldBe ReportListModel.getGetStartState(currentDay) }
            }
            "on create " {
                before { events.accept(ReportList.Event.OnCreate) }
                "propagate list of adapters returned from service" > {
                    val reportListAdapters = listOf(Empty, Empty)
                    reportListAdaptersSubject.onNext(reportListAdapters)
                    states.test().assertOnFirstElement {
                        it.adapterItemsToShow shouldBe reportListAdapters
                    }
                }
                `call service for report list adapters with correct yearMonth`(2016, Calendar.OCTOBER)
                `show loader`()
                "return filtered adapter items list when filter is enabled" > {
                    events.accept(ReportList.Event.OnFilter)
                    val reportListAdapters = listOf(Empty, Empty, newPaidVacationHourlyReport())
                    reportListAdaptersSubject.onNext(reportListAdapters)
                    states.test().assertOnFirstElement {
                        it.adapterItemsToShow shouldBe listOf(Empty, Empty)
                    }
                }
            }
            "on change to " {
                "next month " {
                    before { events.accept(ReportList.Event.OnNextMonth) }
                    `change yearMonth to correct one`(2016, Calendar.NOVEMBER)
                    `call service for report list adapters with correct yearMonth`(2016, Calendar.NOVEMBER)
                    `show loader`()
                }
                "previous month " {
                    before { events.accept(ReportList.Event.OnPreviousMonth) }
                    `change yearMonth to correct one`(2016, Calendar.SEPTEMBER)
                    `show loader`()
                    `call service for report list adapters with correct yearMonth`(2016, Calendar.SEPTEMBER)
                }
                "current day "{
                    "with changed current day " {
                        before {
                            currentDay = getTimeFrom(2014, Calendar.JANUARY, 1)
                            events.accept(ReportList.Event.OnChangeToCurrentDay)
                        }
                        `change yearMonth to correct one`(2014, Calendar.JANUARY)
                        "change scrollToCurrentDayAction field to PENDING" > {
                            states.test().assertOnFirstElement {
                                it.scrollToCurrentDayAction shouldBe ReportList.ScrollToCurrentDayAction.PENDING
                            }
                        }
                        `show loader`()
                        `call service for report list adapters with correct yearMonth`(2014, Calendar.JANUARY)
                    }
                    "without changed current day " {
                        before { events.accept(ReportList.Event.OnChangeToCurrentDay) }
                        "not call service" > {
                            verify(service, never()).createReportsListAdapters(any())
                        }
                        "not show loader" > {
                            states.test().assertOnFirstElement {
                                it.isLoaderVisible shouldBe false
                            }
                        }
                        "change scrollToCurrentDayAction field to SCROLL and loader should not be displayed" > {
                            states.test().assertOnFirstElement {
                                it.scrollToCurrentDayAction shouldBe ReportList.ScrollToCurrentDayAction.SCROLL
                            }
                        }
                    }
                }
            }
            "unsubscribe previous calls for report list adapters on next event" > {
                val testObserver = states
                        .filter { it.adapterItems.isNotEmpty() }
                        .test()

                events.accept(ReportList.Event.OnCreate)
                events.accept(ReportList.Event.OnNextMonth)
                reportListAdaptersSubject.onNext(listOf(Empty))

                testObserver.assertValueCount(1)
            }
            "on filter " {
                val dayWithoutReports = newDayWithoutReports()
                val originalListOfAdapterItems = listOf(Empty, newRegularHourlyReport(), dayWithoutReports)
                before {
                    events.accept(ReportList.Event.OnCreate)
                    reportListAdaptersSubject.onNext(originalListOfAdapterItems)
                    events.accept(ReportList.Event.OnFilter)
                }
                "change filter flag" > {
                    states.test().assertOnFirstElement {
                        it.isFilterEnabled shouldBe true
                    }
                }
                "filter previous itemAdapters" > {
                    states.test().assertOnFirstElement {
                        it.adapterItemsToShow shouldBe listOf(Empty, dayWithoutReports)
                    }
                }
                "return original list on second filter event" > {
                    events.accept(ReportList.Event.OnFilter)
                    states.test().assertOnFirstElement {
                        it.adapterItemsToShow shouldBe originalListOfAdapterItems
                    }
                }
            }
            "on scroll ended change state" > {
                events.accept(ReportList.Event.OnChangeToCurrentDay)
                events.accept(ReportList.Event.OnScrollEnded)
                states.test().assertOnFirstElement {
                    it.scrollToCurrentDayAction shouldBe ReportList.ScrollToCurrentDayAction.NOT_SCROLL
                }
            }
        }
    }

    private fun TreeTestSuiteBuilder.`show loader`() = "show loader" > {
        states.test().assertOnFirstElement {
            it.isLoaderVisible shouldBe true
        }
    }

    private fun TreeTestSuiteBuilder.`change yearMonth to correct one`(year: Int, month: Int) = "change yearMonth to correct one" > {
        states.test().assertOnFirstElement {
            it.yearMonth shouldBe yearMonthFrom(year, month)
        }
    }

    private fun TreeTestSuiteBuilder.`call service for report list adapters with correct yearMonth`(year: Int, month: Int) =
            "call service for report list adapters with correct yearMonth" > {
                verify(service).createReportsListAdapters(yearMonth = yearMonthFrom(year, month))
            }

    private fun yearMonthFrom(year: Int, month: Int) = getTimeFrom(year, month, 1).toYearMonth()
}
