package model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ExerciseTest {
    private Exercise exercise;
    
    @BeforeEach
    void beforeTest() {
        exercise = new Exercise("Leg extension", Muscles.LEGS);
    }

    @Test
    void testConstrutorSimple() {
        // tests the simple constructor and the capitalization
        assertEquals("Leg extension", exercise.getExerciseName());
        assertEquals(Muscles.LEGS, exercise.getMuscleType());
        assertEquals(0, exercise.getWeightLifted());
        assertEquals(0, exercise.getNumSets());
        assertEquals(0, exercise.getNumReps());
    }

    @Test
    void testConstrutorFull() {
        // tests the full constructor and capitalization
        exercise = new Exercise("Incline bench press", Muscles.CHEST, 60, 4, 10);
        assertEquals(Muscles.CHEST, exercise.getMuscleType());
        assertEquals(60, exercise.getWeightLifted());
        assertEquals(4, exercise.getNumSets());
        assertEquals(10, exercise.getNumReps());
    }

    @Test
    void testSetmethods() {
        // tests capitalization in setExerciseName
        exercise.setExerciseName("shoulder press");
        assertEquals("Shoulder press", exercise.getExerciseName());
        
        exercise.setMuscleType(Muscles.SHOULDERS);
        assertEquals(Muscles.SHOULDERS, exercise.getMuscleType());
        
        exercise.setNumReps(10);
        assertEquals(10, exercise.getNumReps());
        
        exercise.setNumSets(3);
        assertEquals(3, exercise.getNumSets());
        
        exercise.setWeightLifted(40);
        assertEquals(40, exercise.getWeightLifted());
    }

    @Test
    void testGetTotalVolumeNormal() {
        // Test with full explicit constructor values (60kg * 4 sets * 10 reps = 2400)
        exercise = new Exercise("Incline bench press", Muscles.CHEST, 60, 4, 10);
        assertEquals(2400, exercise.getTotalVolume());
    }

    @Test
    void testGetTotalVolumeWithZeroWeight() {
        // Bodyweight exercises like pull-ups often log 0 kg weight
        // 0kg * 5 sets * 8 reps = 0
        exercise = new Exercise("Pull up", Muscles.BACK, 0, 5, 8);
        assertEquals(0, exercise.getTotalVolume());
    }

    @Test
    void testGetTotalVolumeAfterSetMethods() {
        // Start simple (0 volume initially)
        assertEquals(0, exercise.getTotalVolume());
        
        // Dynamically shift numbers via mutator setters
        exercise.setWeightLifted(50);
        exercise.setNumSets(3);
        exercise.setNumReps(12);
        
        // 50kg * 3 sets * 12 reps = 1800
        assertEquals(1800, exercise.getTotalVolume());
    }

}