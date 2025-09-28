import { Component } from '@angular/core';
import {BatchesService} from "../services/batches-service/batches-service";
import {Observable} from "rxjs";
import {Batch} from "../models/Batch";
import {MatFormField} from "@angular/material/form-field";
import {MatInput, MatLabel} from "@angular/material/input";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {BatchParameters, JobParameters} from "../models/BatchParameters";
import {AsyncPipe} from "@angular/common";
import {RouterLink} from "@angular/router";
import {StatusIndicator} from "../../../shared/components/status-indicator/status-indicator";

@Component({
  selector: 'app-batch-view',
  imports: [
    MatFormField,
    MatInput,
    MatLabel,
    ReactiveFormsModule,
    AsyncPipe,
    FormsModule,
    RouterLink,
    StatusIndicator
  ],
  templateUrl: './batch-view.html',
  styleUrl: './batch-view.css',
})
export class BatchView {
  nbOfProcess = 10;
  redDeepness = 3;
  yellowDeepness = 3;

  batchesArray$: Observable<Batch[]>;
  //remainingTasks$: Observable<RemainingTasks>;

  constructor(private batchesService: BatchesService) {
    this.batchesArray$ = batchesService.batchesArray$;
  }

  startBatch() {
    this.batchesService.startBatch(new BatchParameters(this.nbOfProcess, new JobParameters(this.redDeepness, this.yellowDeepness))).subscribe();
  }
}
