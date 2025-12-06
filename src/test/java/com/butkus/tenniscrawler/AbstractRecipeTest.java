package com.butkus.tenniscrawler;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

public abstract class AbstractRecipeTest {

    abstract Recipe getRecipe();

    private Recipe recipe;

    @BeforeEach
    void setUp() {
        this.recipe = getRecipe();
    }

    abstract List<CourtTypeAtHour> expectedFirst();
    @Test
    void getFirst_returnsBestCourts() {
        List<CourtTypeAtHour> actual = recipe.nextCourtTypeAtHour();
        List<CourtTypeAtHour> expected = expectedFirst();
        assertEquals(expected, actual);
    }

    abstract List<CourtTypeAtHour> expectedSecond();
    @Test
    void getSecond() {
        recipe.nextCourtTypeAtHour(); // skip first

        List<CourtTypeAtHour> actual = recipe.nextCourtTypeAtHour();
        List<CourtTypeAtHour> expected = expectedSecond();
        assertEquals(expected, actual);
    }

    abstract List<CourtTypeAtHour> expectedLast();
    @Test
    void getLast() {
        int skipAllButOne = getWeightsInOrder().size() - 1;
        IntStream.rangeClosed(1, skipAllButOne).forEach(i -> recipe.nextCourtTypeAtHour());     // skip to the last

        List<CourtTypeAtHour> actual = recipe.nextCourtTypeAtHour();
        List<CourtTypeAtHour> expected = expectedLast();
        assertEquals(expected, actual);
    }


    abstract List<Integer> getWeightsInOrder();
    @Test
    void getAllWeights() {
        for (Integer weight : getWeightsInOrder()) {
            assertEquals(weight, assertGetNextWeight());
        }
    }

    private Integer assertGetNextWeight() {
        assertTrue(recipe.hasNext());
        return recipe.nextWeight();
    }

    @Test
    void getAllWeightsPlusOneMore_throws() {
        getAllWeights();
        assertFalse(recipe.hasNext());
        assertThrows(NoSuchElementException.class, () -> recipe.next());
    }

    // how many rows there are, e.g. 3 for indoor, outdoor shaded, outdoor sunny
    abstract Integer getCourtCategoryCount();

    abstract List<Long> getAllRelevantCourtIds();

    @Nested
    class RecipeIntegrity {

        @Test
        void mapPreservesOrder() {
            assertInstanceOf(LinkedHashMap.class, recipe.getMap());
        }

        // each table column (time) has all 3 court types represented
        @Test
        void allTableCellsFilledOut() {
            var timeToCourtTypes = new HashMap<LocalTime, Set<CourtTypeCustom>>();
            for (var listOfCourtTypeAtHour : recipe.getMap().values()) {
                for (var courtTypeAtHour : listOfCourtTypeAtHour) {
                    var time = courtTypeAtHour.getTime();
                    var type = courtTypeAtHour.getCourtType();
                    Set<CourtTypeCustom> courtTypeCustomSet = timeToCourtTypes.computeIfAbsent(time, k -> new HashSet<>());
                    courtTypeCustomSet.add(type);
                }
            }
            for (var entry : timeToCourtTypes.entrySet()) {
                assertEquals(getCourtCategoryCount(), entry.getValue().size(), "Time " + entry.getKey() + " does not have 3 unique court types");
            }
        }

        @Test
        void allCourtsCovered() {
            Set<CourtTypeCustom> allCourtTypeCustom = recipe.getMap().values().stream()
                    .flatMap(Collection::stream)
                    .map(CourtTypeAtHour::getCourtType)
                    .collect(Collectors.toSet());

            List<Long> actualCourtIds = new ArrayList<>();
            for (CourtTypeCustom courtTypeCustom : allCourtTypeCustom) {
                actualCourtIds.addAll(courtTypeCustom.getIds().stream().map(Court::getCourtId).toList());
            }

            Assertions.assertThat(getAllRelevantCourtIds()).containsExactlyInAnyOrderElementsOf(actualCourtIds);
        }

    }

}
