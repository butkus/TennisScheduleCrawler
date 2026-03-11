package com.butkus.tenniscrawler;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.*;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

public abstract class AbstractRecipeTest {

    abstract Recipe getRecipe();

    private Recipe recipe;

    @BeforeEach
    void setUp() {
        this.recipe = getRecipe();
    }

    abstract List<CourtGroupAtHour> expectedFirst();
    @Test
    void getFirst_returnsBestCourts() {
        List<CourtGroupAtHour> actual = recipe.nextCourtTypeAtHour();
        List<CourtGroupAtHour> expected = expectedFirst();
        assertEquals(expected, actual);
    }

    abstract List<CourtGroupAtHour> expectedSecond();
    @Test
    void getSecond() {
        recipe.nextCourtTypeAtHour(); // skip first

        List<CourtGroupAtHour> actual = recipe.nextCourtTypeAtHour();
        List<CourtGroupAtHour> expected = expectedSecond();
        assertEquals(expected, actual);
    }

    abstract List<CourtGroupAtHour> expectedLast();
    @Test
    void getLast() {
        int skipAllButOne = getWeightsInOrder().size() - 1;
        IntStream.rangeClosed(1, skipAllButOne).forEach(i -> recipe.nextCourtTypeAtHour());     // skip to the last

        List<CourtGroupAtHour> actual = recipe.nextCourtTypeAtHour();
        List<CourtGroupAtHour> expected = expectedLast();
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
            var timeToCourtTypes = new HashMap<LocalTime, Set<CourtGroup>>();
            for (var listOfCourtTypeAtHour : recipe.getMap().values()) {
                for (var courtTypeAtHour : listOfCourtTypeAtHour) {
                    var time = courtTypeAtHour.getTime();
                    var type = courtTypeAtHour.getCourtType();
                    Set<CourtGroup> courtGroupSet = timeToCourtTypes.computeIfAbsent(time, k -> new HashSet<>());
                    courtGroupSet.add(type);
                }
            }
            for (var entry : timeToCourtTypes.entrySet()) {
                assertEquals(getCourtCategoryCount(), entry.getValue().size(), "Time " + entry.getKey() + " does not have 3 unique court types");
            }
        }

        @Test
        void allCourtsCovered() {
            Assertions.assertThat(recipe.getCourtIds()).containsExactlyInAnyOrderElementsOf(getAllRelevantCourtIds());
        }

    }

}
