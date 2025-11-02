import {Injectable, OnDestroy} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {BehaviorSubject, map, Subject, Subscription} from "rxjs";
import {Tasks} from "../../models/Tasks";
import {RemainingTasks} from "../../models/RemainingTasks";
import {WebsocketService} from "../../../../core/websocket/websocket-servicre";
import {IdDTO} from "../../models/IdDTO";
import {Status} from "../../../../shared/models/Status";
import {GlobalStats} from "../../../jobs/models/GlobalStats";

@Injectable({
  providedIn: 'root'
})
export class TasksService implements OnDestroy {
  private remainingTasksSubject= new Subject<RemainingTasks>();

  private tasksSubscription ?: Subscription;
  private remainingTaskSubscription ?: Subscription;
  private wsSubscription ?: Subscription;

  private tasksMap$ = new BehaviorSubject(new Map<string, Tasks>());

  private url = "http://localhost:8080/ws/tasks";

  constructor(private http: HttpClient, private ws: WebsocketService) {}

  init(jobId: string) {
    this.getTasks(jobId).subscribe(jobs => {
      const jobsMap = new Map<string, Tasks>()
      jobs.forEach(job => {
        jobsMap.set(job.id, job);
      });
      this.tasksMap$.next(jobsMap);
      this.getRemainingTasks();
    });

    this.wsSubscription = this.ws.connect(this.url).subscribe((notification: any) => {
      if (notification.type === 'CONNECTED') {
        this.ws.send(this.url, new IdDTO(jobId));
        return;
      }

      this.tasksSubscription = this.getTask(jobId, notification.id).subscribe(jobs => {
        const newMap = new Map(this.tasksMap$.value);
        newMap.set(jobs.id, {...jobs});
        this.tasksMap$.next(newMap);
      });

      this.getRemainingTasks();
    });

    this.ws.send(this.url, new IdDTO(jobId));
  }

  get tasksArray$() {
    return this.tasksMap$.pipe(
      map(map => [...map.values()])
    )
  }

  get remainingTasks$() {
    return this.remainingTasksSubject.asObservable();
  }

  private getTask(jobId: string, taskId: string) {
    return this.http.get<Tasks>(`http://localhost:8080/api/tasks/${jobId}/${taskId}`);
  }

  private getTasks(jobId: string) {
    return this.http.get<Tasks[]>(`http://localhost:8080/api/tasks/${jobId}`);
  }

  getGlobalStats(redDeepness: number, yellowDeepness: number) {
    return this.http.get<GlobalStats>(`http://localhost:8080/api/stats/stats/${redDeepness}/${yellowDeepness}`);
  }

  private getRemainingTasks() {
    this.tasksArray$.subscribe(jobs => {
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
    this.tasksSubscription?.unsubscribe();
    this.remainingTaskSubscription?.unsubscribe();
    this.wsSubscription?.unsubscribe();
  }
}
