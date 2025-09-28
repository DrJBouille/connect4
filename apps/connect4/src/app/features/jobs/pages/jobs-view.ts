import {ChangeDetectionStrategy, Component} from '@angular/core';
import {AsyncPipe} from "@angular/common";
import {FormsModule} from "@angular/forms";
import {map, Observable} from "rxjs";
import {Jobs} from "../models/Jobs";
import {RemainingTasks} from "../models/RemainingTasks";
import {JobsService} from "../services/jobs-service/jobs-service";
import {ActivatedRoute} from "@angular/router";
import {StatusIndicator} from "../../../shared/components/status-indicator/status-indicator";
import {WinnerIndicator} from "../../../shared/components/winner-indicator/winner-indicator";
import {SimpleValueInformation} from "../../../shared/components/simple-value-information/simple-value-information";
import {PlotlyPieCharts} from "../../../shared/components/plotly-pie-charts/plotly-pie-charts";

@Component({
  selector: 'app-jobs-view',
  imports: [
    AsyncPipe,
    FormsModule,
    StatusIndicator,
    WinnerIndicator,
    SimpleValueInformation,
    PlotlyPieCharts
  ],
  templateUrl: './jobs-view.html',
  styleUrl: './jobs-view.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class JobsView {
  jobsArray$: Observable<Jobs[]>;
  remainingTasks$: Observable<RemainingTasks>;
  donutValues$: Observable<number[]>

  constructor(private jobsService: JobsService, private route: ActivatedRoute) {
    this.route.paramMap.subscribe(params => {
      const batchId = params.get('batchId') ?? '';
      this.jobsService.init(batchId);
    });

    this.jobsArray$ = this.jobsService.jobsArray$;
    this.remainingTasks$ = this.jobsService.remainingTasks$;

    this.donutValues$ = this.jobsArray$.pipe(
      map(jobs => {
        const redWins = jobs.filter(job => job.stats?.doesRedWin === true).length;
        const yellowWins = jobs.filter(job => job.stats?.doesRedWin === false).length;
        const draws = jobs.filter(job => job.stats?.doesRedWin == null).length;

        return [redWins, yellowWins, draws];
      })
    );
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
}
