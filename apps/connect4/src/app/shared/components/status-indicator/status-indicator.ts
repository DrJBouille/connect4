import {Component, Input} from '@angular/core';
import {Status} from "../../models/Status";

@Component({
  selector: 'app-status-indicator',
  imports: [],
  templateUrl: './status-indicator.html',
  styleUrl: './status-indicator.css',
})
export class StatusIndicator {
  @Input() status!: Status;
  protected readonly Status = Status;
}
