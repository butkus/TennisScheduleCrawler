
package com.butkus.tenniscrawler.rest.placeinfobatch;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;

@NoArgsConstructor
@Data
public class PlaceInfoBatchRspDto {

    @JsonProperty("data")
    private List<DataOuter> data;

    @JsonProperty("status")
    private String status;

    public boolean hasVacancies(String timeFrom, String timeTo) {
        LocalTime from = LocalTime.parse(timeFrom);
        LocalTime to = LocalTime.parse(timeTo);

        if (!"success".equals(status)) return false;        // todo maybe throw exception instead? or at least sout warning

        // "place" a.k.a. courtType + data[]
        for (DataOuter outer : data) {
            // list of list of data, (or a bunch of timetables for various courtIDs on various dates)
            for (List<DataInner> inner : outer.getData()) {
                // single courtID + timetable (timetable of certain courtID on certain date)
                for (DataInner dataInner : inner) {
                    boolean hasVacancies = dataInner.getTimetable().hasVacancies(from, to);
                    if (hasVacancies) return true;
                }
            }
        }

        return false;
    }

}

// FOR NICER REFERENCE SEE  com.butkus.tenniscrawler.rest.placeinfobatch.PlaceInfoBatchRspDto
// IT IS THE SAME CLASS BUT ACTUAL JSON
//
//
//{
//    "status": "success",
//    "data":
//    [
//        {
//        "place": 20,
//        "data":
//            [
//                [
//                    {
//                        "courtID": 54,
//                        "date": "2023-09-02",
//                        "timetable":
//                            {
//                                "t08_00_00": {
//                                    "from": "08:00:00",
//                                    "to": "08:30:00",
//                                    "status": "full"
//                                },
//                                "t08_30_00": {
//                                    "from": "08:30:00",
//                                    "to": "09:00:00",
//                                    "status": "full"
//                                },
//                                "t09_00_00": {
//                                    "from": "09:00:00",
//                                    "to": "09:30:00",
//                                    "status": "full"
//                                }
//                            }
//                    }
//                ]
//
//
//            ]
//        }
//    ]
//}
//
//
