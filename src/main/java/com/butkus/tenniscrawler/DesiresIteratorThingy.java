package com.butkus.tenniscrawler;

import com.butkus.tenniscrawler.rest.SebFetcher;
import com.butkus.tenniscrawler.rest.SebOrderConverter;
import com.butkus.tenniscrawler.rest.orders.Order;
import com.butkus.tenniscrawler.rest.orders.OrdersRspDto;
import com.butkus.tenniscrawler.rest.placeinfobatch.DataInner;
import com.butkus.tenniscrawler.rest.placeinfobatch.PlaceInfoBatchRspDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
@Component
public class DesiresIteratorThingy {

    private final BookingConfigurator configurator;

    public void doWork(List<Desire> desires) {

        // todo refactoring
        //  - why are desires prepared outside and orders processed here?
        //  - we could
        //     - process both of them inside or both of them outside
        //     - after simplification, maybe DesiresIteratorThingy can be removed? It always had a weird place and name. Inelegant.
        //     - can we also make it so `emptyMethodToCaptureArgument()` hack is no longer necessary?
        //     - also, see this for more on same topic
        //         - DesiresIteratorThingyTest, comment // todo (after finished with postPlaceInfoBatch):

        configurator.resetAudioPlayer();
        SebFetcher fetcher = configurator.getFetcher();

        LocalDate min = desires.stream().map(Desire::getDate).min(Comparator.naturalOrder()).orElseThrow(() -> new RuntimeException("no min date found"));
        LocalDate max = desires.stream().map(Desire::getDate).max(Comparator.naturalOrder()).orElseThrow(() -> new RuntimeException("no max date found"));
        OrdersRspDto ordersDto = fetcher.getOrders(min.toString(), max.toString());
        List<Order> orders = SebOrderConverter.toOrders(ordersDto);

        // todo if "bandyti parduoti", order will have "sab_parduodamas":1

        DesireOrderPairer pairer = new DesireOrderPairer(desires, orders);
        pairer.pair();

        // todo extract some methods, this is too long
        for (LocalDate currentDate = min; !currentDate.isAfter(max); currentDate = currentDate.plusDays(8)) {
            LocalDate from = currentDate;
            LocalDate to = currentDate.plusDays(7);
            List<String> _8days = List.of(
                    currentDate.toString(),
                    currentDate.plusDays(1).toString(),
                    currentDate.plusDays(2).toString(),
                    currentDate.plusDays(3).toString(),
                    currentDate.plusDays(4).toString(),
                    currentDate.plusDays(5).toString(),
                    currentDate.plusDays(6).toString(),
                    currentDate.plusDays(7).toString()
            );

            PlaceInfoBatchRspDto placeInfoBatchRspDto = fetcher.postPlaceInfoBatch(_8days, CourtType.getIdsForPlaceFetcher());
            List<DataInner> courtDtos = placeInfoBatchRspDto.getData()
                    .stream()
                    .flatMap(placeData -> placeData.getData().stream()
                            .flatMap(List::stream))
                    .toList();

            // todo (maybe) if I pre-prepared only 1-8 day desires to be in this `desires`, wouldn't need to check `dateOk`
            for (Desire desire : desires) {
                boolean desireEqualsOrLaterThanFrom = desire.getDate().equals(from) || desire.getDate().isAfter(from);
                boolean desireBeforeOrEqualsToTo = desire.getDate().isBefore(to) || desire.getDate().equals(to);
                boolean dateOk = desireEqualsOrLaterThanFrom && desireBeforeOrEqualsToTo;
                if (dateOk) {
                    // fixme: should pass courtDtos for only for that 1 day (now 8 days)
                    // todo: similar as above "maybe" comment, move filtration elsewhere?
                    List<DataInner> todaysDtos = courtDtos.stream().filter(e -> e.getDate().equals(desire.getDate())).toList();
                    VacancyFound vacancyFound = findVacancy(desire, todaysDtos);
                    emptyMethodToCaptureArgument(vacancyFound);
                }
            }
        }


        Calendar.printCalendar(orders, desires);
    }

    public void emptyMethodToCaptureArgument(VacancyFound vacancyFound) {

    }

    public VacancyFound findVacancy(Desire desire, List<DataInner> courtDtos) {
        Vacancy vacancy = new Vacancy(desire, configurator);
        return vacancy.find(courtDtos);
    }


    public LocalDateTime getNow() {
        return LocalDateTime.now(configurator.getClock());
    }

    public LocalDateTime get48HoursAgo() {
        return LocalDateTime.now(configurator.getClock()).minusHours(48);
    }
}
