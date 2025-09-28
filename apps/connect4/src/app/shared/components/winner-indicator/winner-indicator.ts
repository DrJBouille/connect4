import {Component, Input} from '@angular/core';

@Component({
  selector: 'app-winner-indicator',
  imports: [],
  templateUrl: './winner-indicator.html',
  styleUrl: './winner-indicator.css',
})
export class WinnerIndicator {
  @Input() doesRedWin: Boolean | null = null;
}
