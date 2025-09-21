import {Injectable, NgZone, OnDestroy} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {BehaviorSubject, map, Observable, Subject, Subscription} from "rxjs";
import {BatchParameters} from "./BatchParameters";
import {BatchIdDTO} from "./BatchIdDTO";
import {Jobs} from "./Jobs";
import {RemainingTasks} from "./RemainingTasks";

@Injectable({
  providedIn: 'root'
})
export class JobsService implements OnDestroy {
  private socket?: WebSocket;

  private jobIdSubject = new Subject<string>();
  private openSubject = new Subject<void>();
  private remainingTasksSubject= new Subject<RemainingTasks>();

  private jobsSubscription ?: Subscription;
  private remainingTaskSubscription ?: Subscription;

  private jobsMap$ = new BehaviorSubject(new Map<string, Jobs>());

  constructor(private http: HttpClient, private ngZone: NgZone) {
  }

  connect(url: string): void {
    if (this.socket) this.socket.close();

    this.socket = new WebSocket(url);

    this.socket.onopen = () => {
      this.openSubject.next();
    };

    this.socket.onmessage = (event) => {
      this.ngZone.run(() => {
        const jobsId = event.data

        this.jobsSubscription = this.getJobs(jobsId).subscribe(jobs => {
          const newMap = new Map(this.jobsMap$.value)
          newMap.set(jobs.jobsId, {...jobs});
          this.jobsMap$.next(newMap);
        });

        this.remainingTaskSubscription = this.getRemainingTasks().subscribe(remainingTasks => {
          console.log(remainingTasks);
          this.remainingTasksSubject.next({...remainingTasks});
        })

        this.jobIdSubject.next(event.data);
      });
    };

    this.socket.onclose = () => {console.log("WebSocket closed");};

    this.socket.onerror = (error) => {console.log(`WebSocket error: ${error}`)}
  }

  get jobsArray$() {
    return this.jobsMap$.pipe(
      map(map => [...map.values()])
    )
  }

  get remainingTasks$() {
    return this.remainingTasksSubject.asObservable();
  }

  startBatch(batchParameters: BatchParameters) {
    return this.http.post<BatchIdDTO>('http://localhost:8080/api/jobs/start', batchParameters);
  }

  private getJobs(id: string) {
    return this.http.get<Jobs>(`http://localhost:8080/api/jobs/${id}`);
  }

  private getRemainingTasks() {
    return this.http.get<RemainingTasks>(`http://localhost:8080/api/jobs/remainingTasks`);
  }

  ngOnDestroy() {
    this.jobsSubscription?.unsubscribe();
    this.remainingTaskSubscription?.unsubscribe();
    this.socket?.close();
  }
}
