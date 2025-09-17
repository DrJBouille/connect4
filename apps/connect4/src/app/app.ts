import {Component, OnDestroy, OnInit} from '@angular/core';
import { RouterModule } from '@angular/router';
import { NxWelcome } from './nx-welcome';
import {JobsService} from "./jobs-service/jobs-service";
import {Subscription} from "rxjs";
import {BatchParameters} from "./jobs-service/BatchParameters";
import {JobParameter} from "./jobs-service/JobParameter";

@Component({
  imports: [NxWelcome, RouterModule],
  selector: 'app-root',
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App implements OnInit, OnDestroy {
  protected title = 'connect4';
  jobsSubscription ?: Subscription

  constructor(private jobsService: JobsService) {
  }

  ngOnInit() {
    this.jobsService.connect('ws://localhost:8080/ws/jobs');

    // attendre que la socket soit ready avant de démarrer le batch
    this.jobsService.onOpen().subscribe(() => {
      this.jobsSubscription = this.jobsService.getJobId$().subscribe((jobId) => {
        this.jobsService.getJobs(jobId).subscribe(jobs => console.log('Jobs:', jobs));
      });

      this.jobsService.startBatch(new BatchParameters(30, new JobParameter(2, 5)))
        .subscribe(batchId => console.log('BatchId via HTTP:', batchId));
    });
  }

  ngOnDestroy() {
    this.jobsSubscription?.unsubscribe();
    this.jobsService.disconnect();
  }
}
