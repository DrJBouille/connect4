import {Component} from '@angular/core';
import {BatchesService} from "../services/batches-service/batches-service";
import {Observable} from "rxjs";
import {Batch} from "../models/Batch";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {BatchParameters, JobParameters} from "../models/BatchParameters";
import {AsyncPipe} from "@angular/common";
import {RouterLink} from "@angular/router";
import {StatusIndicator} from "../../../shared/components/status-indicator/status-indicator";
import {Images} from "../models/Images";
import {CardParameters} from "../components/card-parameters/card-parameters";

@Component({
  selector: 'app-batch-view',
  imports: [
    ReactiveFormsModule,
    AsyncPipe,
    FormsModule,
    RouterLink,
    StatusIndicator,
    CardParameters
  ],
  templateUrl: './batch-view.html',
  styleUrl: './batch-view.css',
})
export class BatchView {
  jobsParameters: JobParameters[] = []

  batchesArray$: Observable<Batch[]>;
  //remainingTasks$: Observable<RemainingTasks>;

  constructor(private batchesService: BatchesService) {
    this.batchesArray$ = batchesService.batchesArray$;
  }

  addJobParameters() {
    this.jobsParameters.push(new JobParameters(2, 3, 3, Images.Connect4BotKotlin))
  }

  startBatch() {
    if (this.jobsParameters.length > 0) this.batchesService.startBatch(new BatchParameters(this.jobsParameters)).subscribe();
  }
}
