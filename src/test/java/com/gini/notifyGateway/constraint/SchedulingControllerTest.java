///*
// * Copyright 2020 Red Hat, Inc. and/or its affiliates.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package com.gini.scheduling.solver;
//
//import static org.junit.jupiter.api.Assertions.assertFalse;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.Timeout;
//import org.optaplanner.core.api.solver.SolverStatus;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import com.gini.scheduling.domain.Sgruser;
//import com.gini.scheduling.domain.Scheduling;
//
//@SpringBootTest(properties = {
//        "optaplanner.solver.termination.spent-limit=1h", // Effectively disable this termination in favor of the best-score-limit
//        "optaplanner.solver.termination.best-score-limit=0hard/*soft"})
//public class SchedulingControllerTest {
//
//    @Autowired
//    private SchedulingController timeTableController;
//
//    @Test
//    @Timeout(600_000)
//    public void solveDemoDataUntilFeasible() throws InterruptedException {
//        timeTableController.solve();
//        Scheduling timeTable;
//        do { // Use do-while to give the solver some time and avoid retrieving an early infeasible solution.
//            // Quick polling (not a Test Thread Sleep anti-pattern)
//            // Test is still fast on fast machines and doesn't randomly fail on slow machines.
//            Thread.sleep(20L);
//            timeTable = timeTableController.getScheduling();
//        } while (timeTable.getSolverStatus() != SolverStatus.NOT_SOLVING || !timeTable.getScore().isFeasible());
//        assertFalse(timeTable.getSgruserList().size() == 0);
//        for (Sgruser lesson : timeTable.getSgruserList()) {
//            assertNotNull(lesson.getSchedule());
//            assertNotNull(lesson.getSgsch());
//        }
//        assertTrue(timeTable.getScore().isFeasible());
//    }
//
//}
