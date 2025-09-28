import {Injectable, OnDestroy} from '@angular/core';
import {Observable, Subject, takeUntil} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class WebsocketService implements OnDestroy{
  private sockets = new Map<string, WebSocket>
  private destroy$ = new Subject<void>();

  connect(url: string): Observable<any> {
    if (this.sockets.has(url)) return this.listen(url);

    const socket = new WebSocket(url);
    this.sockets.set(url, socket);

    return this.listen(url);
  }

  private listen(url: string): Observable<any> {
    return new Observable(observer => {
      const socket = this.sockets.get(url)!;

      socket.onopen = () => { observer.next({ type: 'CONNECTED' }); };
      socket.onmessage = (event) => observer.next(JSON.parse(event.data));
      socket.onerror = (err) => observer.next(err);
      socket.onclose = () => observer.complete();

      return () => socket.close();
    }).pipe(takeUntil(this.destroy$));
  }

  send(url: string, message: any) {
    const socket = this.sockets.get(url);
    if (socket && socket.readyState === WebSocket.OPEN) socket.send(JSON.stringify(message));
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
    this.sockets.forEach((socket) => socket.close());
  }
}
