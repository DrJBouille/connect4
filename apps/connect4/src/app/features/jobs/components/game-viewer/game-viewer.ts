import {Component, Input, OnInit} from '@angular/core';
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
export class GameViewer implements OnInit{
  @Input() moves!: Move[];

  protected moveId = 0;
  protected board: number[][] = Array.from({ length: 6 }, () => Array(7).fill(0));

  ngOnInit() {
    this.changeMoveId(0)
  }

  changeMoveId(newMoveId: number) {
    if (newMoveId < 0 || newMoveId > this.moves.length - 1) return;
    this.moveId = newMoveId;

    this.board = Array.from({ length: 6 }, () => Array(7).fill(0));

    for (let i = 0; i <= this.moveId; i++) {
      const coordinate = this.moves[i].coordinate
      this.board[coordinate.y][coordinate.x] = this.moves[i].isRedTurn ? 1 : 2;
    }
  }

  get boardReversed() {
    return [...this.board].reverse();
  }
}
