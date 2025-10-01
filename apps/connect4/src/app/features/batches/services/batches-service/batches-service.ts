import {Injectable, OnDestroy} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {WebsocketService} from "../../../../core/websocket/websocket-servicre";
import {BehaviorSubject, map, Subject, Subscription} from "rxjs";
import {Batch} from "../../models/Batch";
import {BatchParameters} from "../../models/BatchParameters";
import {RemainingTasks} from "../../models/RemainingTasks";
import {GlobalStats} from "../../models/GlobalStats";

@Injectable({
  providedIn: 'root'
})
export class BatchesService implements OnDestroy{
  private barchesSubscription ?: Subscription;
  private batchIdSubscription: Subscription;
  private remainingTaskSubscription ?: Subscription;

  private batchesMap$ = new BehaviorSubject(new Map<string, Batch>());
  private remainingTasksSubject= new Subject<RemainingTasks>();

  constructor(private http: HttpClient, private ws: WebsocketService) {
    this.getBatches().subscribe(batches => {
      const jobsMap = new Map<string, Batch>()
      batches.forEach(batch => {
        jobsMap.set(batch.id, batch);
      });
      this.batchesMap$.next(jobsMap);

      this.remainingTaskSubscription = this.getRemainingTasks().subscribe(remainingTasks => this.remainingTasksSubject.next(remainingTasks));
    });

    this.batchIdSubscription = this.ws.connect("http://localhost:8080/ws/batches").subscribe((notification: any) => {
      if (notification.type === 'CONNECTED') return;

      this.barchesSubscription = this.getBatch(notification.id).subscribe(batch => {
        const newMap = new Map(this.batchesMap$.value)
        newMap.set(batch.id, {...batch});
        this.batchesMap$.next(newMap);
      });

      this.remainingTaskSubscription = this.getRemainingTasks().subscribe(remainingTasks => this.remainingTasksSubject.next(remainingTasks));
    });
  }

  get batchesArray$() {
    return this.batchesMap$.pipe(
      map(map => [...map.values()])
    )
  }

  get remainingTasks$() {
    return this.remainingTasksSubject.asObservable();
  }

  startBatch(batchParameters: BatchParameters) {
    return this.http.post('http://localhost:8080/api/batches/start', batchParameters);
  }

  getBatch(batchId: string) {
    return this.http.get<Batch>(`http://localhost:8080/api/batches/${batchId}`)
  }

  getBatches() {
    return this.http.get<Batch[]>('http://localhost:8080/api/batches');
  }

  getGlobalStats(redDeepness: number, yellowDeepness: number) {
    return this.http.get<GlobalStats>(`http://localhost:8080/api/batches/stats/${redDeepness}/${yellowDeepness}`);
  }

  private getRemainingTasks() {
    return this.http.get<RemainingTasks>(`http://localhost:8080/api/jobs/remainingTasks`);
  }

  ngOnDestroy() {
    this.batchIdSubscription.unsubscribe();
    this.barchesSubscription?.unsubscribe();
    this.remainingTaskSubscription?.unsubscribe();
  }
}
