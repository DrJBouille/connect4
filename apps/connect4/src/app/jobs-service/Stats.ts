import {Move} from "./Move";

export class Stats {
  constructor(
    public gameTime: number,
    public doesRedWin: boolean,
    public moves: Move[]
  ) {
  }
}
