import {Component} from '@angular/core';
import {JobsService} from "./services/jobs-service/jobs-service";
import {Observable} from "rxjs";
import {Jobs} from "./models/Jobs";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {JobsParameters, Parameters} from "./models/JobsParameters";
import {AsyncPipe} from "@angular/common";
import {RouterLink} from "@angular/router";
import {StatusIndicator} from "../../shared/components/status-indicator/status-indicator";
import {Images} from "./models/Images";
import {CardParameters} from "./components/card-parameters/card-parameters";

@Component({
  selector: 'app-jobs-view',
  imports: [
    ReactiveFormsModule,
    AsyncPipe,
    FormsModule,
    RouterLink,
    StatusIndicator,
    CardParameters
  ],
  templateUrl: './job-view.component.html',
  styleUrl: './job-view.component.css',
})
export class JobsView {
  jobsParameters: Parameters[] = []

  jobsArray$: Observable<Jobs[]>;
  //remainingTasks$: Observable<RemainingTasks>;

  constructor(private batchesService: JobsService) {
    this.jobsArray$ = batchesService.jobsArray$;
  }

  addJobParameters() {
    this.jobsParameters.push(new Parameters(2, 3, 3, Images.Connect4BotKotlin))
  }

  startBatch() {
    if (this.jobsParameters.length > 0) this.batchesService.startJob(new JobsParameters(this.jobsParameters)).subscribe();
  }
}
