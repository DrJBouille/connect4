import {Component, Input} from '@angular/core';
import {Move} from "../../models/Stats";
import {MatIcon} from "@angular/material/icon";

@Component({
  selector: 'app-game-viewer',
  imports: [
    MatIcon
  ],
  templateUrl: './game-viewer.html',
  styleUrl: './game-viewer.css',
})
export class GameViewer {
  @Input() moves!: Move[];

  protected moveId = 0;

  changeMoveId(newMoveId: number) {
    if (newMoveId < 0 || newMoveId > this.moves.length - 1) return;
    this.moveId = newMoveId;
  }

  get boardReversed() {
    return [...this.moves[this.moveId].board].reverse();
  }
}
