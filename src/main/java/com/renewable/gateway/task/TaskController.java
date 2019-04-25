package com.renewable.gateway.task;

import com.renewable.gateway.task.config.ScheduleConfig;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description：
 * @Author: jarry
 */
@RestController
@AllArgsConstructor
public class TaskController {
    private final ScheduleConfig scheduleConfig;

//    @Autowired
//    public TaskController(ScheduleConfig scheduleConfig) {
//        this.scheduleConfig = scheduleConfig;
//    }


//    @PostMapping("/task")
//    public String task(@RequestBody Task task) {
//        scheduleConfig.editTask(task);
//        return task.getState().name();
//    }
//
//    @GetMapping("/task")
//    public List<Task> doGet() {
//        return scheduleConfig.getTaskList();
//    }
}
