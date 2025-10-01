import {Injectable, OnDestroy} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {BehaviorSubject, map, Subject, Subscription} from "rxjs";
import {Jobs} from "../../models/Jobs";
import {RemainingTasks} from "../../models/RemainingTasks";
import {WebsocketService} from "../../../../core/websocket/websocket-servicre";
import {IdDTO} from "../../models/IdDTO";
import {Status} from "../../../../shared/models/Status";
import {GlobalStats} from "../../../batches/models/GlobalStats";

@Injectable({
  providedIn: 'root'
})
export class JobsService implements OnDestroy {
  private remainingTasksSubject= new Subject<RemainingTasks>();

  private jobsSubscription ?: Subscription;
  private remainingTaskSubscription ?: Subscription;
  private wsSubscription ?: Subscription;

  private jobsMap$ = new BehaviorSubject(new Map<string, Jobs>());

  private url = "http://localhost:8080/ws/jobs";

  constructor(private http: HttpClient, private ws: WebsocketService) {}

  init(batchId: string) {
    this.getJobs(batchId).subscribe(jobs => {
      const jobsMap = new Map<string, Jobs>()
      jobs.forEach(job => {
        jobsMap.set(job.jobsId, job);
      });
      this.jobsMap$.next(jobsMap);
      this.getRemainingTasks();
    });

    this.wsSubscription = this.ws.connect(this.url).subscribe((notification: any) => {
      if (notification.type === 'CONNECTED') {
        this.ws.send(this.url, new IdDTO(batchId));
        return;
      }

      this.jobsSubscription = this.getJob(batchId, notification.jobsId).subscribe(jobs => {
        const newMap = new Map(this.jobsMap$.value)
        newMap.set(jobs.jobsId, {...jobs});
        this.jobsMap$.next(newMap);
      });

      this.getRemainingTasks();
    });

    this.ws.send(this.url, new IdDTO(batchId));
  }

  get jobsArray$() {
    return this.jobsMap$.pipe(
      map(map => [...map.values()])
    )
  }

  get remainingTasks$() {
    return this.remainingTasksSubject.asObservable();
  }

  private getJob(batchId: string, jobsId: string) {
    return this.http.get<Jobs>(`http://localhost:8080/api/jobs/${batchId}/${jobsId}`);
  }

  private getJobs(batchId: string) {
    return this.http.get<Jobs[]>(`http://localhost:8080/api/jobs/${batchId}`);
  }

  getGlobalStats(redDeepness: number, yellowDeepness: number) {
    return this.http.get<GlobalStats>(`http://localhost:8080/api/batches/stats/${redDeepness}/${yellowDeepness}`);
  }

  private getRemainingTasks() {
    this.jobsArray$.subscribe(jobs => {
      this.remainingTasksSubject.next(new RemainingTasks(
        jobs.filter(job => job.status == Status.NOT_STARTED).length,
        jobs.filter(job => job.status == Status.PENDING).length,
        jobs.filter(job => job.status == Status.RUNNING).length,
        jobs.filter(job => job.status == Status.FINISHED).length,
        jobs.filter(job => job.status == Status.FAILED).length
      ));
    });

  }

  ngOnDestroy() {
    this.jobsSubscription?.unsubscribe();
    this.remainingTaskSubscription?.unsubscribe();
    this.wsSubscription?.unsubscribe();
  }
}
