package com.butkus.tenniscrawler;

import com.butkus.tenniscrawler.rest.placeinfobatch.PlaceInfoBatchRspDto;

public class SebStubs {

    public static PlaceInfoBatchRspDto stubPlaceInfoFull() throws Exception {
        String json = """
                {
                  "status": "success",
                  "data": [
                    {
                      "place": 5,
                      "data": [
                        [
                          {
                            "courtID": 44,
                            "date": "2023-10-01",
                            "timetable": {
                              "08:00:00": {
                                "from": "08:00:00",
                                "to": "08:30:00",
                                "status": "full"
                              },
                              "08:30:00": {
                                "from": "08:30:00",
                                "to": "09:00:00",
                                "status": "full"
                              },
                              "09:00:00": {
                                "from": "09:00:00",
                                "to": "09:30:00",
                                "status": "full"
                              }
                
                            }
                          }
                        ]
                
                      ]
                    }
                  ]
                }
                """;

        return PlaceInfoBatchRspDto.fromJson(json);
    }

    public static PlaceInfoBatchRspDto stubPlaceInfo7Days() throws Exception {
        String json = """
                {
                  "status": "success",
                  "data": [
                    {
                      "place": 5,
                      "data": [
                        [
                          {
                            "courtID": 44,
                            "date": "2023-10-01",
                            "timetable": {
                              "18:00:00": {
                                "from": "18:00:00",
                                "to": "18:30:00",
                                "status": "free"
                              },
                              "18:30:00": {
                                "from": "18:30:00",
                                "to": "19:00:00",
                                "status": "free"
                              }
                            }
                          },
                          {
                            "courtID": 44,
                            "date": "2023-10-02",
                            "timetable": {
                              "18:00:00": {
                                "from": "18:00:00",
                                "to": "18:30:00",
                                "status": "free"
                              },
                              "18:30:00": {
                                "from": "18:30:00",
                                "to": "19:00:00",
                                "status": "free"
                              }
                            }
                          },
                          {
                            "courtID": 44,
                            "date": "2023-10-03",
                            "timetable": {
                              "18:00:00": {
                                "from": "18:00:00",
                                "to": "18:30:00",
                                "status": "free"
                              },
                              "18:30:00": {
                                "from": "18:30:00",
                                "to": "19:00:00",
                                "status": "free"
                              }
                            }
                          },
                          {
                            "courtID": 44,
                            "date": "2023-10-04",
                            "timetable": {
                              "18:00:00": {
                                "from": "18:00:00",
                                "to": "18:30:00",
                                "status": "free"
                              },
                              "18:30:00": {
                                "from": "18:30:00",
                                "to": "19:00:00",
                                "status": "free"
                              }
                            }
                          },
                          {
                            "courtID": 44,
                            "date": "2023-10-05",
                            "timetable": {
                              "18:00:00": {
                                "from": "18:00:00",
                                "to": "18:30:00",
                                "status": "free"
                              },
                              "18:30:00": {
                                "from": "18:30:00",
                                "to": "19:00:00",
                                "status": "free"
                              }
                            }
                          },
                          {
                            "courtID": 44,
                            "date": "2023-10-06",
                            "timetable": {
                              "18:00:00": {
                                "from": "18:00:00",
                                "to": "18:30:00",
                                "status": "free"
                              },
                              "18:30:00": {
                                "from": "18:30:00",
                                "to": "19:00:00",
                                "status": "free"
                              }
                            }
                          },
                          {
                            "courtID": 44,
                            "date": "2023-10-07",
                            "timetable": {
                              "18:00:00": {
                                "from": "18:00:00",
                                "to": "18:30:00",
                                "status": "free"
                              },
                              "18:30:00": {
                                "from": "18:30:00",
                                "to": "19:00:00",
                                "status": "free"
                              }
                            }
                          },
                          {
                            "courtID": 44,
                            "date": "2023-10-08",
                            "timetable": {
                              "18:00:00": {
                                "from": "18:00:00",
                                "to": "18:30:00",
                                "status": "free"
                              },
                              "18:30:00": {
                                "from": "18:30:00",
                                "to": "19:00:00",
                                "status": "free"
                              }
                            }
                          }
                          
                        ]
                
                      ]
                    }
                  ]
                }
                """;

        return PlaceInfoBatchRspDto.fromJson(json);
    }

    public static PlaceInfoBatchRspDto stubPlaceInfoClay01at1900free() throws Exception {
        String json = """
                {
                  "status": "success",
                  "data": [
                    {
                      "place": 5,
                      "data": [
                        [
                          {
                            "courtID": 44,
                            "date": "2023-10-11",
                            "timetable": {
                              "19:00:00": {
                                "from": "19:00:00",
                                "to": "19:30:00",
                                "status": "free"
                              },
                              "19:30:00": {
                                "from": "19:30:00",
                                "to": "20:00:00",
                                "status": "free"
                              },
                              "20:00:00": {
                                "from": "20:00:00",
                                "to": "20:30:00",
                                "status": "full"
                              }
                
                            }
                          }
                        ]
                
                      ]
                    }
                  ]
                }
                """;

        return PlaceInfoBatchRspDto.fromJson(json);
    }

    public static PlaceInfoBatchRspDto stubPlaceInfoClay01at1930free() throws Exception {
        String json = """
                {
                  "status": "success",
                  "data": [
                    {
                      "place": 5,
                      "data": [
                        [
                          {
                            "courtID": 44,
                            "date": "2023-10-11",
                            "timetable": {
                              "19:30:00": {
                                "from": "19:30:00",
                                "to": "20:00:00",
                                "status": "free"
                              },
                              "20:00:00": {
                                "from": "20:00:00",
                                "to": "20:30:00",
                                "status": "free"
                              },
                              "20:30:00": {
                                "from": "20:30:00",
                                "to": "21:00:00",
                                "status": "full"
                              }
                
                            }
                          }
                        ]
                
                      ]
                    }
                  ]
                }
                """;

        return PlaceInfoBatchRspDto.fromJson(json);
    }

    public static PlaceInfoBatchRspDto stubPlaceInfoHard01at1930fullsell90Mins() throws Exception {
        String json = """
                {
                  "status": "success",
                  "data": [
                    {
                      "place": 5,
                      "data": [
                        [
                          {
                            "courtID": 1,
                            "date": "2023-10-01",
                            "timetable": {
                              "19:30:00": {
                                "from": "19:30:00",
                                "to": "20:00:00",
                                "status": "fullsell"
                              },
                              "20:00:00": {
                                "from": "20:00:00",
                                "to": "20:30:00",
                                "status": "fullsell"
                              },
                              "20:30:00": {
                                "from": "20:30:00",
                                "to": "21:00:00",
                                "status": "fullsell"
                              }
                
                            }
                          }
                        ]
                
                      ]
                    }
                  ]
                }
                """;

        return PlaceInfoBatchRspDto.fromJson(json);
    }

    public static PlaceInfoBatchRspDto stubPlaceInfoClay01at1900free_then_Clay02at1930free() throws Exception {
        String json = """
                {
                  "status": "success",
                  "data": [
                    {
                      "place": 5,
                      "data": [
                        [
                          {
                            "courtID": 44,
                            "date": "2023-10-11",
                            "timetable": {
                              "19:00:00": {
                                "from": "19:00:00",
                                "to": "19:30:00",
                                "status": "free"
                              },
                              "19:30:00": {
                                "from": "19:30:00",
                                "to": "20:00:00",
                                "status": "free"
                              },
                              "20:00:00": {
                                "from": "20:00:00",
                                "to": "20:30:00",
                                "status": "full"
                              }
                
                            }
                          },
                          {
                            "courtID": 45,
                            "date": "2023-10-11",
                            "timetable": {
                              "19:00:00": {
                                "from": "19:00:00",
                                "to": "19:30:00",
                                "status": "full"
                              },
                              "19:30:00": {
                                "from": "19:30:00",
                                "to": "20:00:00",
                                "status": "free"
                              },
                              "20:00:00": {
                                "from": "20:00:00",
                                "to": "20:30:00",
                                "status": "free"
                              }
                
                            }
                          }
                        ]
                
                      ]
                    }
                  ]
                }
                """;

        return PlaceInfoBatchRspDto.fromJson(json);
    }

    public static PlaceInfoBatchRspDto stubPlaceInfoClay02at1930free_then_Clay01at1900free() throws Exception {
        String json = """
                {
                  "status": "success",
                  "data": [
                    {
                      "place": 5,
                      "data": [
                        [
                          {
                            "courtID": 45,
                            "date": "2023-10-11",
                            "timetable": {
                              "19:00:00": {
                                "from": "19:00:00",
                                "to": "19:30:00",
                                "status": "full"
                              },
                              "19:30:00": {
                                "from": "19:30:00",
                                "to": "20:00:00",
                                "status": "free"
                              },
                              "20:00:00": {
                                "from": "20:00:00",
                                "to": "20:30:00",
                                "status": "free"
                              }
                            }
                          },
                          {
                            "courtID": 44,
                            "date": "2023-10-11",
                            "timetable": {
                              "19:00:00": {
                                "from": "19:00:00",
                                "to": "19:30:00",
                                "status": "free"
                              },
                              "19:30:00": {
                                "from": "19:30:00",
                                "to": "20:00:00",
                                "status": "free"
                              },
                              "20:00:00": {
                                "from": "20:00:00",
                                "to": "20:30:00",
                                "status": "full"
                              }
                
                            }
                          }

                        ]
                
                      ]
                    }
                  ]
                }
                """;

        return PlaceInfoBatchRspDto.fromJson(json);
    }

// todo remove first timetable? it is here just to fulfill all weight 5 cases
    public static PlaceInfoBatchRspDto stubWeight5_60min90min60min() throws Exception {
        String json = """
                {
                  "status": "success",
                  "data": [
                    {
                      "place": 5,
                      "data": [
                        [
                          {
                            "courtID": 44,
                            "date": "2023-10-11",
                            "timetable": {
                              "18:00:00": {
                                "from": "18:00:00",
                                "to": "18:30:00",
                                "status": "free"
                              },
                              "18:30:00": {
                                "from": "18:30:00",
                                "to": "19:00:00",
                                "status": "free"
                              }
                            }
                          },
                          {
                            "courtID": 44,
                            "date": "2023-10-11",
                            "timetable": {
                              "20:00:00": {
                                "from": "20:00:00",
                                "to": "20:30:00",
                                "status": "free"
                              },
                              "20:30:00": {
                                "from": "20:30:00",
                                "to": "21:00:00",
                                "status": "free"
                              },
                              "21:00:00": {
                                "from": "21:00:00",
                                "to": "21:30:00",
                                "status": "free"
                              }
                            }
                          },
                          {
                            "courtID": 52,
                            "date": "2023-10-11",
                            "timetable": {
                              "19:30:00": {
                                "from": "19:30:00",
                                "to": "20:00:00",
                                "status": "free"
                              },
                              "20:00:00": {
                                "from": "20:00:00",
                                "to": "20:30:00",
                                "status": "free"
                              }
                
                            }
                          }

                        ]
                
                      ]
                    }
                  ]
                }
                """;

        return PlaceInfoBatchRspDto.fromJson(json);
    }


    // todo remove first timetable? it is here just to fulfill all weight 5 cases
    public static PlaceInfoBatchRspDto stubWeight5_60min60min90min() throws Exception {
        String json = """
                {
                  "status": "success",
                  "data": [
                    {
                      "place": 5,
                      "data": [
                        [
                          {
                            "courtID": 44,
                            "date": "2023-10-11",
                            "timetable": {
                              "18:00:00": {
                                "from": "18:00:00",
                                "to": "18:30:00",
                                "status": "free"
                              },
                              "18:30:00": {
                                "from": "18:30:00",
                                "to": "19:00:00",
                                "status": "free"
                              }
                            }
                          },
                          {
                            "courtID": 44,
                            "date": "2023-10-11",
                            "timetable": {
                              "20:00:00": {
                                "from": "20:00:00",
                                "to": "20:30:00",
                                "status": "free"
                              },
                              "20:30:00": {
                                "from": "20:30:00",
                                "to": "21:00:00",
                                "status": "free"
                              }
                            }
                          },
                          {
                            "courtID": 52,
                            "date": "2023-10-11",
                            "timetable": {
                              "19:30:00": {
                                "from": "19:30:00",
                                "to": "20:00:00",
                                "status": "free"
                              },
                              "20:00:00": {
                                "from": "20:00:00",
                                "to": "20:30:00",
                                "status": "free"
                              },
                              "20:30:00": {
                                "from": "20:30:00",
                                "to": "21:00:00",
                                "status": "free"
                              }
                
                            }
                          }

                        ]
                
                      ]
                    }
                  ]
                }
                """;

        return PlaceInfoBatchRspDto.fromJson(json);
    }



    public static PlaceInfoBatchRspDto stubPlaceInfoClay09at1830free_then_Grass01at1900free() throws Exception {
        String json = """
                {
                  "status": "success",
                  "data": [
                    {
                       "place": 5,
                       "data": [
                         [
                           {
                             "courtID": 52,
                             "date": "2023-10-11",
                             "timetable": {
                               "18:30:00": {
                                 "from": "18:30:00",
                                 "to": "19:00:00",
                                 "status": "free"
                               },
                               "19:00:00": {
                                 "from": "19:00:00",
                                 "to": "19:30:00",
                                 "status": "free"
                               }
                             }
                           }
 
                         ]
                       ]
                    },
                    {
                       "place": 20,
                       "data": [
                         [
                           {
                             "courtID": 54,
                             "date": "2023-10-11",
                             "timetable": {
                               "19:00:00": {
                                 "from": "19:00:00",
                                 "to": "19:30:00",
                                 "status": "free"
                               },
                               "19:30:00": {
                                 "from": "19:30:00",
                                 "to": "20:00:00",
                                 "status": "free"
                               }
                             }
                           }
 
                         ]
                       ]
                    }
                  ]
                }
                """;

        return PlaceInfoBatchRspDto.fromJson(json);
    }

    public static PlaceInfoBatchRspDto stubPlaceInfoGrass01at1900free_then_Clay09at1830free() throws Exception {
        String json = """
                {
                  "status": "success",
                  "data": [
                    {
                       "place": 20,
                       "data": [
                         [
                           {
                             "courtID": 54,
                             "date": "2023-10-11",
                             "timetable": {
                               "19:00:00": {
                                 "from": "19:00:00",
                                 "to": "19:30:00",
                                 "status": "free"
                               },
                               "19:30:00": {
                                 "from": "19:30:00",
                                 "to": "20:00:00",
                                 "status": "free"
                               }
                             }
                           }
                         ]
                       ]
                    },
                    {
                       "place": 5,
                       "data": [
                         [
                           {
                             "courtID": 52,
                             "date": "2023-10-11",
                             "timetable": {
                               "18:30:00": {
                                 "from": "18:30:00",
                                 "to": "19:00:00",
                                 "status": "free"
                               },
                               "19:00:00": {
                                 "from": "19:00:00",
                                 "to": "19:30:00",
                                 "status": "free"
                               }
                             }
                           }
                         ]
                       ]
                    }
                
                  ]
                }
                """;

        return PlaceInfoBatchRspDto.fromJson(json);
    }








    public static PlaceInfoBatchRspDto stubPlaceInfoClay01at1830free() throws Exception {
        String json = """
                {
                  "status": "success",
                  "data": [
                    {
                      "place": 5,
                      "data": [
                        [
                          {
                            "courtID": 44,
                            "date": "2023-10-11",
                            "timetable": {
                              "18:30:00": {
                                "from": "18:30:00",
                                "to": "19:00:00",
                                "status": "free"
                              },
                              "19:00:00": {
                                "from": "19:00:00",
                                "to": "19:30:00",
                                "status": "free"
                              }
                
                            }
                          }
                        ]
                
                      ]
                    }
                  ]
                }
                """;

        return PlaceInfoBatchRspDto.fromJson(json);
    }

    public static PlaceInfoBatchRspDto stubPlaceInfoClay01at1800free() throws Exception {
        String json = """
                {
                  "status": "success",
                  "data": [
                    {
                      "place": 5,
                      "data": [
                        [
                          {
                            "courtID": 44,
                            "date": "2023-10-11",
                            "timetable": {
                              "18:00:00": {
                                "from": "18:00:00",
                                "to": "18:30:00",
                                "status": "free"
                              },
                              "18:30:00": {
                                "from": "18:30:00",
                                "to": "19:00:00",
                                "status": "free"
                              }
                
                            }
                          }
                        ]
                
                      ]
                    }
                  ]
                }
                """;

        return PlaceInfoBatchRspDto.fromJson(json);
    }

    public static PlaceInfoBatchRspDto stubPlaceInfoClay01at1800has30minFree() throws Exception {
        String json = """
                {
                  "status": "success",
                  "data": [
                    {
                      "place": 5,
                      "data": [
                        [
                          {
                            "courtID": 44,
                            "date": "2023-10-01",
                            "timetable": {
                              "18:00:00": {
                                "from": "18:00:00",
                                "to": "18:30:00",
                                "status": "free"
                              }
                
                            }
                          }
                        ]
                
                      ]
                    }
                  ]
                }
                """;

        return PlaceInfoBatchRspDto.fromJson(json);
    }

    public static PlaceInfoBatchRspDto stubPlaceInfoClay01at1930has90minFree() throws Exception {
        String json = """
                {
                  "status": "success",
                  "data": [
                    {
                      "place": 5,
                      "data": [
                        [
                          {
                            "courtID": 44,
                            "date": "2023-10-11",
                            "timetable": {
                              "19:30:00": {
                                "from": "19:30:00",
                                "to": "20:00:00",
                                "status": "free"
                              },
                              "20:00:00": {
                                "from": "20:00:00",
                                "to": "20:30:00",
                                "status": "free"
                              },
                              "20:30:00": {
                                "from": "20:30:00",
                                "to": "21:00:00",
                                "status": "free"
                              }
                
                            }
                          }
                        ]
                
                      ]
                    }
                  ]
                }
                """;

        return PlaceInfoBatchRspDto.fromJson(json);
    }

    public static PlaceInfoBatchRspDto stubPlaceInfoClay09at1930has120minFree() throws Exception {
        String json = """
                {
                  "status": "success",
                  "data": [
                    {
                      "place": 5,
                      "data": [
                        [
                          {
                            "courtID": 52,
                            "date": "2023-10-11",
                            "timetable": {
                              "19:30:00": {
                                "from": "19:30:00",
                                "to": "20:00:00",
                                "status": "free"
                              },
                              "20:00:00": {
                                "from": "20:00:00",
                                "to": "20:30:00",
                                "status": "free"
                              },
                              "20:30:00": {
                                "from": "20:30:00",
                                "to": "21:00:00",
                                "status": "free"
                              },
                              "21:00:00": {
                                "from": "21:00:00",
                                "to": "21:30:00",
                                "status": "free"
                              }
                
                            }
                          }
                        ]
                
                      ]
                    }
                  ]
                }
                """;

        return PlaceInfoBatchRspDto.fromJson(json);
    }



    public static PlaceInfoBatchRspDto stubPlaceInfoClay10at1900free() throws Exception {
        String json = """
                {
                  "status": "success",
                  "data": [
                    {
                      "place": 5,
                      "data": [
                        [
                          {
                            "courtID": 53,
                            "date": "2023-10-11",
                            "timetable": {
                              "19:00:00": {
                                "from": "19:00:00",
                                "to": "19:30:00",
                                "status": "free"
                              },
                              "19:30:00": {
                                "from": "19:30:00",
                                "to": "20:00:00",
                                "status": "free"
                              }
                
                            }
                          }
                        ]
                
                      ]
                    }
                  ]
                }
                """;

        return PlaceInfoBatchRspDto.fromJson(json);
    }
}
