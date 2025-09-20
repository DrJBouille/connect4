import {ChangeDetectionStrategy, ChangeDetectorRef, Component, OnDestroy, OnInit} from '@angular/core';
import {JobsService} from "./jobs-service/jobs-service";
import {Observable} from "rxjs";
import {BatchParameters} from "./jobs-service/BatchParameters";
import {JobParameter} from "./jobs-service/JobParameter";
import {Jobs} from "./jobs-service/Jobs";
import {AsyncPipe} from "@angular/common";
import {FormsModule} from "@angular/forms";
import {
  MatCell,
  MatCellDef, MatColumnDef,
  MatHeaderCell, MatHeaderCellDef,
  MatHeaderRow,
  MatHeaderRowDef,
  MatRow,
  MatRowDef, MatTable
} from "@angular/material/table";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {MatButton} from "@angular/material/button";

@Component({
  imports: [
    AsyncPipe,
    FormsModule,
    MatCell,
    MatCellDef,
    MatHeaderCell,
    MatHeaderRow,
    MatHeaderRowDef,
    MatRow,
    MatRowDef,
    MatTable,
    MatColumnDef,
    MatHeaderCellDef,
    MatFormField,
    MatLabel,
    MatInput,
    MatButton
  ],
  selector: 'app-root',
  templateUrl: './app.html',
  styleUrl: './app.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class App implements OnInit, OnDestroy {
  nbOfProcess = 10;
  redDeepness = 3;
  yellowDeepness = 3;

  jobsArray$: Observable<Jobs[]>;
  displayedColumns: string[] = ['Batch UUID', 'Jobs UUID', 'Status', 'Game Time', 'Winner', 'NB of moves'];

  constructor(private jobsService: JobsService, private cdr: ChangeDetectorRef) {
    this.jobsArray$ = this.jobsService.jobsArray$;
  }

  ngOnInit() {
    document.body.classList.add('dark-theme');
    this.jobsService.connect('ws://localhost:8080/ws/jobs');
  }

  ngOnDestroy() {
    this.jobsService.disconnect();
  }

  startBatch() {
    this.jobsService.startBatch(new BatchParameters(this.nbOfProcess, new JobParameter(this.redDeepness, this.yellowDeepness)))
      .subscribe(batchId => console.log('BatchId via HTTP:', batchId));
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
