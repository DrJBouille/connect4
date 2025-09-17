import {Injectable, NgZone} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable, Subject} from "rxjs";
import {BatchParameters} from "./BatchParameters";
import {BatchIdDTO} from "./BatchIdDTO";
import {Jobs} from "./Jobs";

@Injectable({
  providedIn: 'root'
})
export class JobsService {
  private socket?: WebSocket;
  private jobIdSubject = new Subject<string>();
  private openSubject = new Subject<void>();

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
        this.jobIdSubject.next(event.data);
      });
    };

    this.socket.onclose = () => {console.log("WebSocket closed");};

    this.socket.onerror = (error) => {console.log(`WebSocket error: ${error}`)}
  }

  onOpen(): Observable<void> {
    return this.openSubject.asObservable();
  }

  getJobId$(): Observable<string> {
    return this.jobIdSubject.asObservable();
  }

  disconnect(): void {
    this.socket?.close();
  }

  startBatch(batchParameters: BatchParameters) {
    return this.http.post<BatchIdDTO>('http://localhost:8080/api/jobs/start', batchParameters);
  }

  getJobs(id: string) {
    return this.http.get<Jobs>(`http://localhost:8080/api/jobs/${id}`);
  }
}
