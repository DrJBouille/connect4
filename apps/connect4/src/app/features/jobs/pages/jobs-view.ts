import {ChangeDetectionStrategy, Component, OnDestroy} from '@angular/core';
import {AsyncPipe} from "@angular/common";
import {FormsModule} from "@angular/forms";
import {Observable, retry, Subscription} from "rxjs";
import {Jobs} from "../models/Jobs";
import {RemainingTasks} from "../models/RemainingTasks";
import {JobsService} from "../services/jobs-service/jobs-service";
import {ActivatedRoute} from "@angular/router";
import {StatusIndicator} from "../../../shared/components/status-indicator/status-indicator";
import {WinnerIndicator} from "../../../shared/components/winner-indicator/winner-indicator";
import {SimpleValueInformation} from "../../../shared/components/simple-value-information/simple-value-information";
import {PlotlyPieCharts} from "../../../shared/components/plotly-pie-charts/plotly-pie-charts";
import {PlotlyLineCharts} from "../../../shared/components/plotly-line-charts/plotly-line-charts";
import {GameViewer} from "../components/game-viewer/game-viewer";
import {GlobalStats} from "../../batches/models/GlobalStats";

@Component({
  selector: 'app-jobs-view',
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
  templateUrl: './jobs-view.html',
  styleUrl: './jobs-view.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class JobsView implements OnDestroy {
  jobsArray$: Observable<Jobs[]>;
  remainingTasks$: Observable<RemainingTasks>;

  jobsArraySubscription?: Subscription;
  globalStatsSubscription?: Subscription;

  winnerValues: number[] = [0, 0, 0];
  movesTimeValue: {x: number[], y: number[]} = {x: [0], y: [0]};
  averageGameTime: string = '~';

  globalStats?: GlobalStats;
  redDeepness = '~';
  yellowDeepness = '~';
  globalAverageGameTime = '~';

  selectedJob: Jobs | null = null;
  selectedJobId: string | null = null;

  constructor(private jobsService: JobsService, private route: ActivatedRoute) {
    this.route.paramMap.subscribe(params => {
      const batchId = params.get('batchId') ?? '';
      this.jobsService.init(batchId);
    });

    this.jobsArray$ = this.jobsService.jobsArray$;
    this.remainingTasks$ = this.jobsService.remainingTasks$;

    this.jobsArraySubscription = this.jobsArray$.subscribe(jobs => {
      if (jobs.length <= 0) return;
      this.selectedJob = null;

      this.winnerValues = this.getWinnerValues(jobs);
      this.movesTimeValue = this.getMovesTimeValues(jobs);
      this.averageGameTime = this.getAverageGameTime(jobs);

      if (this.selectedJobId) this.selectedJob = jobs.find(job => job.jobsId === this.selectedJobId) || null;
      else this.selectedJob = jobs[0];

      if (!this.selectedJob || !this.selectedJob.stats) return;
      this.globalStatsSubscription = this.jobsService.getGlobalStats(this.selectedJob.stats.redDeepness, this.selectedJob.stats.yellowDeepness).subscribe(globalStats => {
        this.globalStats = globalStats;

        const timeInMillis = Math.round((jobs.map(job => job.stats!.gameTime).reduce((sum, value) => sum + value) / jobs.length) - globalStats.averageGameTime);

        if (timeInMillis < 1000) this.globalAverageGameTime = `${timeInMillis} ms`;
        else this.globalAverageGameTime = `${timeInMillis / 1000} s`;
      });

      this.redDeepness = this.selectedJob.stats.redDeepness.toString();
      this.yellowDeepness = this.selectedJob.stats.yellowDeepness.toString();
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

  selectJob(jobsId: string) {
    this.selectedJobId = jobsId;
    this.jobsArraySubscription = this.jobsArray$.subscribe(jobs => this.selectedJob = jobs.find(job => job.jobsId === jobsId) || null);
  }

  getMovesTimeValues(jobs: Jobs[]) {
    const maxMoves = Math.max(...jobs.map(j => j.stats?.moves?.length ?? 0));
    const x = Array.from({ length: maxMoves }, (_, i) => i + 1);

    let yValues: number[][] = [];

    jobs.forEach((job) => {
      if (!job.stats) return

      let totalTime = 0;
      job.stats.moves.forEach((move, index) => {
        totalTime += move.time;
        if (!yValues[index]) yValues[index] = [];
        yValues[index].push(totalTime);
      });

      const lastTime = totalTime;
      for (let i = job.stats.moves.length; i < maxMoves; i++) {
        if (!yValues[i]) yValues[i] = [];
        yValues[i].push(lastTime);
      }
    });

    const y = yValues.map(values => values.reduce((sum, value) => sum + value, 0) / values.length)

    return {x, y};
  }

  getWinnerValues(jobs: Jobs[]) {
    const redWins = jobs.filter(job => job.stats?.doesRedWin === true).length;
    const yellowWins = jobs.filter(job => job.stats?.doesRedWin === false).length;
    const draws = jobs.filter(job => job.stats?.doesRedWin == null).length;

    return [redWins, yellowWins, draws];
  }

  getAverageGameTime(jobs: Jobs[]) {
    const timeInMillis = Math.round(jobs.map(job => job.stats!.gameTime).reduce((sum, value) => sum + value) / jobs.length);

    if (timeInMillis < 1000) return `${timeInMillis} ms`;
    else return `${timeInMillis / 1000} s`;
  }

  ngOnDestroy() {
    this.jobsArraySubscription?.unsubscribe();
    this.globalStatsSubscription?.unsubscribe();
  }
}
