import {Injectable, OnDestroy} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {WebsocketService} from "../../../../core/websocket/websocket-servicre";
import {BehaviorSubject, map, Subject, Subscription} from "rxjs";
import {Jobs} from "../../models/Jobs";
import {JobsParameters} from "../../models/JobsParameters";
import {RemainingTasks} from "../../models/RemainingTasks";
import {GlobalStats} from "../../models/GlobalStats";

@Injectable({
  providedIn: 'root'
})
export class JobsService implements OnDestroy{
  private jobsSubscription ?: Subscription;
  private jobIdSubscription: Subscription;
  private remainingTaskSubscription ?: Subscription;

  private jobsMap$ = new BehaviorSubject(new Map<string, Jobs>());
  private remainingTasksSubject= new Subject<RemainingTasks>();

  constructor(private http: HttpClient, private ws: WebsocketService) {
    this.getJobs().subscribe(batches => {
      const jobsMap = new Map<string, Jobs>()
      batches.forEach(batch => {
        jobsMap.set(batch.id, batch);
      });
      this.jobsMap$.next(jobsMap);

      this.remainingTaskSubscription = this.getRemainingTasks().subscribe(remainingTasks => this.remainingTasksSubject.next(remainingTasks));
    });

    this.jobIdSubscription = this.ws.connect("http://localhost:8080/ws/jobs").subscribe((notification: any) => {
      if (notification.type === 'CONNECTED') return;

      this.jobsSubscription = this.getJob(notification.id).subscribe(batch => {
        const newMap = new Map(this.jobsMap$.value)
        newMap.set(batch.id, {...batch});
        this.jobsMap$.next(newMap);
      });

      this.remainingTaskSubscription = this.getRemainingTasks().subscribe(remainingTasks => this.remainingTasksSubject.next(remainingTasks));
    });
  }

  get jobsArray$() {
    return this.jobsMap$.pipe(
      map(map => [...map.values()])
    )
  }

  get remainingTasks$() {
    return this.remainingTasksSubject.asObservable();
  }

  startJob(batchParameters: JobsParameters) {
    return this.http.post('http://localhost:8080/api/jobs/start', batchParameters);
  }

  getJob(jobId: string) {
    return this.http.get<Jobs>(`http://localhost:8080/api/jobs/${jobId}`)
  }

  getJobs() {
    return this.http.get<Jobs[]>('http://localhost:8080/api/jobs');
  }

  getGlobalStats(redDeepness: number, yellowDeepness: number) {
    return this.http.get<GlobalStats>(`http://localhost:8080/api/jobs/stats/${redDeepness}/${yellowDeepness}`);
  }

  private getRemainingTasks() {
    return this.http.get<RemainingTasks>(`http://localhost:8080/api/jobs/remainingJobs`);
  }

  ngOnDestroy() {
    this.jobIdSubscription.unsubscribe();
    this.jobsSubscription?.unsubscribe();
    this.remainingTaskSubscription?.unsubscribe();
  }
}
