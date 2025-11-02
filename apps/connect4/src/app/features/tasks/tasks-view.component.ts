import {ChangeDetectionStrategy, Component, OnDestroy} from '@angular/core';
import {AsyncPipe} from "@angular/common";
import {FormsModule} from "@angular/forms";
import {Observable, retry, Subscription} from "rxjs";
import {Tasks} from "./models/Tasks";
import {RemainingTasks} from "./models/RemainingTasks";
import {TasksService} from "./services/jobs-service/tasks.service";
import {ActivatedRoute} from "@angular/router";
import {StatusIndicator} from "../../shared/components/status-indicator/status-indicator";
import {WinnerIndicator} from "../../shared/components/winner-indicator/winner-indicator";
import {SimpleValueInformation} from "../../shared/components/simple-value-information/simple-value-information";
import {PlotlyPieCharts} from "../../shared/components/plotly-pie-charts/plotly-pie-charts";
import {PlotlyLineCharts} from "../../shared/components/plotly-line-charts/plotly-line-charts";
import {GameViewer} from "./components/game-viewer/game-viewer";
import {GlobalStats} from "../jobs/models/GlobalStats";

@Component({
  selector: 'app-tasks-view',
  imports: [
    AsyncPipe,
    FormsModule,
    StatusIndicator,
    WinnerIndicator,
    SimpleValueInformation,
    PlotlyPieCharts,
    PlotlyLineCharts,
    GameViewer
  ],
  templateUrl: './tasks-view.component.html',
  styleUrl: './tasks-view.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class TasksView implements OnDestroy {
  tasksArray$: Observable<Tasks[]>;
  remainingTasks$: Observable<RemainingTasks>;

  tasksArraySubscription?: Subscription;
  globalStatsSubscription?: Subscription;

  winnerValues: number[] = [0, 0, 0];
  movesTimeValue: {x: number[], y: number[]} = {x: [0], y: [0]};
  averageGameTime: string = '~';

  globalStats?: GlobalStats;
  redDeepness = '~';
  yellowDeepness = '~';
  globalAverageGameTime = '~';

  selectedTask: Tasks | null = null;
  selectedTaskId: string | null = null;

  constructor(private jobsService: TasksService, private route: ActivatedRoute) {
    this.route.paramMap.subscribe(params => {
      const jobId = params.get('jobId') ?? '';
      this.jobsService.init(jobId);
    });

    this.tasksArray$ = this.jobsService.tasksArray$;
    this.remainingTasks$ = this.jobsService.remainingTasks$;

    this.tasksArraySubscription = this.tasksArray$.subscribe(jobs => {
      if (jobs.length <= 0) return;
      this.selectedTask = null;

      this.winnerValues = this.getWinnerValues(jobs);
      this.movesTimeValue = this.getMovesTimeValues(jobs);
      this.averageGameTime = this.getAverageGameTime(jobs);

      if (this.selectedTaskId) this.selectedTask = jobs.find(job => job.id === this.selectedTaskId) || null;
      else this.selectedTask = jobs[0];

      if (!this.selectedTask || !this.selectedTask.stats) return;
      this.globalStatsSubscription = this.jobsService.getGlobalStats(this.selectedTask.stats.redDeepness, this.selectedTask.stats.yellowDeepness).subscribe(globalStats => {
        this.globalStats = globalStats;

        const timeInMillis = Math.round((jobs.map(job => job.stats!.gameTime).reduce((sum, value) => sum + value) / jobs.length) - globalStats.averageGameTime);

        if (timeInMillis < 1000) this.globalAverageGameTime = `${timeInMillis} ms`;
        else this.globalAverageGameTime = `${timeInMillis / 1000} s`;
      });

      this.redDeepness = this.selectedTask.stats.redDeepness.toString();
      this.yellowDeepness = this.selectedTask.stats.yellowDeepness.toString();
    });
  }

  msToTime(ms: number): string {
    if (ms == null || ms <= 0) return '00:00:00';

    const totalSeconds = Math.floor(ms / 1000);
    const hours = Math.floor(totalSeconds / 3600);
    const minutes = Math.floor((totalSeconds % 3600) / 60);
    const seconds = totalSeconds % 60;

    return `${this.pad(hours)}:${this.pad(minutes)}:${this.pad(seconds)}`;
  }

  pad(n: number): string {
    return n < 10 ? '0' + n : n.toString();
  }

  selectTask(taskId: string) {
    this.selectedTaskId = taskId;
    this.tasksArraySubscription = this.tasksArray$.subscribe(jobs => this.selectedTask = jobs.find(job => job.id === taskId) || null);
  }

  getMovesTimeValues(tasks: Tasks[]) {
    const maxMoves = Math.max(...tasks.map(j => j.stats?.moves?.length ?? 0));
    const x = Array.from({ length: maxMoves }, (_, i) => i + 1);

    let yValues: number[][] = [];

    tasks.forEach((task) => {
      if (!task.stats) return

      let totalTime = 0;
      task.stats.moves.forEach((move, index) => {
        totalTime += move.time;
        if (!yValues[index]) yValues[index] = [];
        yValues[index].push(totalTime);
      });

      const lastTime = totalTime;
      for (let i = task.stats.moves.length; i < maxMoves; i++) {
        if (!yValues[i]) yValues[i] = [];
        yValues[i].push(lastTime);
      }
    });

    const y = yValues.map(values => values.reduce((sum, value) => sum + value, 0) / values.length)

    return {x, y};
  }

  getWinnerValues(tasks: Tasks[]) {
    const redWins = tasks.filter(task => task.stats?.doesRedWin === true).length;
    const yellowWins = tasks.filter(task => task.stats?.doesRedWin === false).length;
    const draws = tasks.filter(task => task.stats?.doesRedWin == null).length;

    return [redWins, yellowWins, draws];
  }

  getAverageGameTime(tasks: Tasks[]) {
    const timeInMillis = Math.round(tasks.map(task => task.stats!.gameTime).reduce((sum, value) => sum + value) / tasks.length);

    if (timeInMillis < 1000) return `${timeInMillis} ms`;
    else return `${timeInMillis / 1000} s`;
  }

  ngOnDestroy() {
    this.tasksArraySubscription?.unsubscribe();
    this.globalStatsSubscription?.unsubscribe();
  }
}
